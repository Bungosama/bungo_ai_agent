package com.bungo.bungoaiagent.agent;

import org.springframework.ai.tool.annotation.Tool;

/**
 * 合理终止工具
 * @author bungosama_
 * @since 2025-07-09 04:36
 */
public class TerminateTool {

    @Tool(description = "If you think the task is about to be completed or cannot be completed, then terminate it")
    public String doTerminate() {
        return "终止任务";
    }

}
