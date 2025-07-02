package com.bungo.bungoaiagent.rag;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import java.util.List;

/**
 * @author bungosama_
 * @since 2025-07-02 19:55
 * 创建自定义的rag检索增强顾问工厂
 */
public class LoveAppRagCustomAdvisorFactory {


    /**
     * 创建自定义的rag检索增强顾问
     * @param vectorStore
     * @param status
     * @return
     */

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                // 过滤条件
                .filterExpression(expression)
                // 返回文档数量
                .topK(3)
                // 相似度阈值
                .similarityThreshold(0.5)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createLoveAppContextualQueryAugmenter())
                .build();
    }

}
