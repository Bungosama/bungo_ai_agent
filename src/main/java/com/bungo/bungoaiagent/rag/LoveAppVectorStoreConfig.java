package com.bungo.bungoaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bungosama_
 * @since 2025-06-28 04:39
 * 恋爱大师向量数据库配置   初始化基于内存的向量数据库bean
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    VectorStore LoveAppVectorStore (EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        simpleVectorStore.doAdd(loveAppDocumentLoader.loadMarkdownDocuments());
        return simpleVectorStore;
    }

}
