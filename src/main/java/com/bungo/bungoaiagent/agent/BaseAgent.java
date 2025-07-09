package com.bungo.bungoaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.bungo.bungoaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bungosama_
 * @since 2025-07-08 17:30
 * 抽象概念   状态管理和事件循环
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextPrompt;

    // 代理状态
    private AgentState status = AgentState.IDLE;

    // 执行步骤控制
    private int currentStep = 0;
    private int maxStep = 0;

    // LLM大模型
    private ChatClient chatClient;

    // Memory记忆（需要自主维护会话上下文）
    private List<Message> memoryList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt) {
        // 基础校验
        if (status != AgentState.IDLE) {
            throw new RuntimeException("[" + this.status + "] : This agent is not idle");
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("User prompt is blank");
        }
        // 执行、更改状态
        this.status = AgentState.RUNNING;
        // 记录消息上下文
        memoryList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> resultList = new ArrayList<>();
        try {
            // 执行步骤
            for (int i = 0; i < maxStep && status != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxStep);
                // 单步执行
                String result = step();
                String stepResult = "Step " + stepNumber + " result: " + result;
                resultList.add(stepResult);
            }
            // 检查步骤次数是否超出限制
            if (currentStep >= maxStep) {
                status = AgentState.FINISHED;
                resultList.add("Terminated: Reached max steps (" + maxStep + ")");
            }
            return String.join("\n", resultList);
        } catch (Exception e) {
            status = AgentState.ERROR;
            log.error("Error occurred during agent execution: {}", e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            // 清理资源
            clean();
        }
    }

    /**
     * 定义单个步骤
     * @return
     */
    public abstract String step();

    protected void clean() {
        // 子类重写此方法来清理状态
    };

}
