package com.bungo.bungoaiagent.agent;

import com.bungo.bungoaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 拥有自主规划能力的超级智能体
 * @author bungosama_
 * @since 2025-07-08 17:31
 */
@Component
public class BungoManus extends ToolCallAgent{

    public BungoManus(ToolCallback[] availableTools, ChatModel dashscopeChatModel) {
        super(availableTools);
        this.setName("bungoManus");
        String SYSTEM_PROMPT = """
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextPrompt(NEXT_STEP_PROMPT);

        this.setMaxStep(20);

        // 初始化AI客户端
        this.setChatClient(ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build()
        );
    }

}
