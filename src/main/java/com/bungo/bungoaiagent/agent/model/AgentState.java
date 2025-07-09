package com.bungo.bungoaiagent.agent.model;

/**
 * @author bungosama_
 * @since 2025-07-08 16:19
 */
public enum AgentState {
    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 已完成状态
     */
    FINISHED,

    /**
     * 错误状态
     */
    ERROR,
}
