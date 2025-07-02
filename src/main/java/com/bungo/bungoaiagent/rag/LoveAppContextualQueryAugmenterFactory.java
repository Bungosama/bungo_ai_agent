package com.bungo.bungoaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * @author bungosama_
 * @since 2025-07-02 20:32
 * 创建上下文查询增强器的工厂
 */
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createLoveAppContextualQueryAugmenter() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate(
                "你应该输出下面的内容：抱歉，我只能回答恋爱相关的问题，别的没有办法帮到您哦，有问题您可以联系客服 000-00000"
        );
        return ContextualQueryAugmenter.builder()
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .allowEmptyContext(false)
                .build();
    }

}
