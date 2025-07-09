package com.bungo.bungoaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * reasoning 和 acting  代理抽象类
 * 实现了思考+行动的循环模式
 * @author bungosama_
 * @since 2025-07-08 17:30
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent{

    /**
     * 处理当前状态并决定下一步行动
     * @return
     */
    public abstract boolean think();

    /**
     * 执行下一步行动
     * @return
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            // 先思考
            boolean thinkResult = think();
            if (!thinkResult) {
                return "思考完成，无需行动";
            }
            // 再行动
            String actResult = act();
            return actResult;
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败：" + e.getMessage();
        }

    }
}
