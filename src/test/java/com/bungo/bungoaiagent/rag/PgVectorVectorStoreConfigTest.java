package com.bungo.bungoaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-07-01 20:09
 */
@SpringBootTest
class PgVectorVectorStoreConfigTest {


    @Resource
    @Qualifier("pgVectorVectorStore")
    private VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {
        List<Document> documents = List.of(
                new Document("笨糕的恋爱导航网有什么用？提升情商啊，找到对象啊", Map.of("meta1", "meta2")),
                new Document("程序员笨糕的原创项目教程 codefather.cn"),
                new Document("笨糕这个小伙子比较帅气", Map.of("meta1", "meta2"))
        );
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> result = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("怎么学编程啊？").topK(3).build());
        Assertions.assertNotNull(result);
    }

}