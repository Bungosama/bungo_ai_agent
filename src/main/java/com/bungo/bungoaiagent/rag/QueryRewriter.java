package com.bungo.bungoaiagent.rag;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * @author bungosama_
 * @since 2025-07-02 19:23
 * 查询重写器
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;


    public QueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    // 查询问题重写
    public String doQueryRewrite(String query) {
        Query transform = queryTransformer.transform(new Query(query));
        return transform.text();
    }

}
