package com.bungo.bungoaiagent.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.bungo.bungoaiagent.agent.BungoManus;
import com.bungo.bungoaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author bungosama_
 * @since 2025-07-09 18:28
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] availableTools;

    @Resource
    private DashScopeChatModel dashscopeChatModel;

    /**
     *  * 同步调用
     * @param message
     * @param chatId
     * @return
     */
    @PutMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * sse流式调用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * sse流式调用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder().
                        data(chunk)
                        .build()
                );
    }

    /**
     * sse流式调用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 设置一个超时时长3分钟的emitter
        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        // 获取flux响应式数据流并直接通过订阅推送给emitter
        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        emitter.send(chunk);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }, emitter::completeWithError, emitter::complete);
        return emitter;
    }

    /**
     * sse流式调用
     * @param message
     * @return
     */
    @GetMapping(value = "/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        BungoManus bungoManus = new BungoManus(availableTools, dashscopeChatModel);
        return bungoManus.runStream(message);
    }

}
