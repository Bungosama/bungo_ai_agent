package com.bungo.bungoaiagent.demo.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-07-02 19:04
 */
@SpringBootTest
class MyMultiQueryExpanderTest {

    @Resource
    private MyMultiQueryExpander myMultiQueryExpander;

    @Test
    void expand() {
        List<Query> expand = myMultiQueryExpander.expand("谁是程序员笨糕啊？？？？？啊啊啊啊啊啊？？？请回答我哈哈哈哈哈");
        Assertions.assertNotNull(expand);
    }

}