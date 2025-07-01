package com.bungo.bungoaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-06-26 04:31
 */
@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;

    @Test
    void testDoChat() {

        String chatId = UUID.randomUUID().toString();

        //第一轮
        String message = "你好，我是笨糕";
        String answer = loveApp.doChat(message, chatId);
        //第二轮
        message = "我想让另一半（聪明糕）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我的另一半叫什么来着，我刚才跟你说过，你帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat() {
    }

    @Test
    void doChatWithReport() {

        String chatId = UUID.randomUUID().toString();

        String message = "你好，我是笨糕，我想让另一半（聪明糕）更爱我，但我不知道该怎么做";

        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);

    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好，我是笨糕，我已经结婚了，但是婚后关系不太亲密，怎么办？";

        String chatWithRag = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(chatWithRag);
    }
}