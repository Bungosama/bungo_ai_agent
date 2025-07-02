package com.bungo.bungoaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bungosama_
 * @since 2025-06-28 04:39
 * 恋爱大师向量数据库配置   初始化基于内存的向量数据库bean
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore LoveAppVectorStore (EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documentList = loveAppDocumentLoader.loadMarkdownDocuments();

        // 分割文档
        //List<Document> splitDocumentsList = myTokenTextSplitter.splitDocuments(documentList);
        //simpleVectorStore.add(splitDocumentsList);

        // 文档关键词源信息增强器
        List<Document> enricherDocumentList = myKeywordEnricher.enricherDocument(documentList);
        simpleVectorStore.add(enricherDocumentList);
        return simpleVectorStore;
    }

}
