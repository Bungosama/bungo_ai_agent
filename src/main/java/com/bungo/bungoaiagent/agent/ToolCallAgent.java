package com.bungo.bungoaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.bungo.bungoaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理共苦调用的基础代理类  具有增强的抽象
 * @author bungosama_
 * @since 2025-07-08 17:31
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ToolCallAgent extends ReActAgent{

    // 可用的工具列表
    private final ToolCallback[] availableTools;

    // 保存工具调用信息的响应结果
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private ToolCallingManager toolCallingManager;

    // 禁用SpringAI内置的工具调用，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    // 由于ToolCallAgent不是springai托管的bean，所以要手动注入
    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true) // true表示关闭代理工具调用  启动自主调用
                .build();
    }

    @Override
    public boolean think() {
        // 校验提示词，拼接用户提示词
        if (StrUtil.isNotBlank(getNextPrompt())) {
            UserMessage userMessage = new UserMessage(getNextPrompt());
            getMemoryList().add(userMessage);
        }
        // 调用AI大模型，获取工具调用结果
        List<Message> messages = getMemoryList();
        Prompt prompt = new Prompt(messages, this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于act
            this.toolCallChatResponse = chatResponse;
            // 解析工具调用结果，获取要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取工具调用列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            // 输出提示信息
            String result = assistantMessage.getText();
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了" + toolCalls.size() + "个工具");
            String collect = toolCalls.stream()
                    .map(toolCall -> String.format("工具名称：%s，工具参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(collect);
            // 如果不需要调用工具返回false
            if (toolCalls.isEmpty()) {
                // 只有不需要调用工具的时候，才需要手动记录助手消息
                getMemoryList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具，返回true，无需手动记录，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的自考过程遇到了问题：" + e.getMessage());
            getMemoryList().add(new AssistantMessage("处理问题是出现了以下错误：" + e.getMessage()));
            return false;
        }

    }

    @Override
    public String act() {
        // 验证工具调用
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 获取工具调用
        Prompt toolCallPrompt = new Prompt(getMemoryList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(toolCallPrompt, this.toolCallChatResponse);
        // 记录消息上下文，conversationHistory已经包含了助手消息和工具调用返回结果
        setMemoryList(toolExecutionResult.conversationHistory());
        // 拿到最新一条并输出
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) toolExecutionResult.conversationHistory().get(toolExecutionResult.conversationHistory().size() - 1);
        // 是否调用了终止工具
        boolean doTerminateResult = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        if (doTerminateResult) {
            setStatus(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses()
                .stream()
                .map(toolResponse -> "工具" + toolResponse.name() + "返回的结果：" + toolResponse.responseData())
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果：" + results);
        return results;
    }
}
