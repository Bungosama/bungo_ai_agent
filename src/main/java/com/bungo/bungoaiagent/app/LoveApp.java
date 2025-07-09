package com.bungo.bungoaiagent.app;




import com.bungo.bungoaiagent.advisor.MyLoggerAdvisor;
import com.bungo.bungoaiagent.advisor.ReReadingAdvisor;
import com.bungo.bungoaiagent.chatmemory.FileBasedChatMemory;
import com.bungo.bungoaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.bungo.bungoaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


/**
 * @author bungosama_
 * @since 2025-06-26 04:10
 */
@Component
@Slf4j
public class LoveApp {

    private Logger logger = LoggerFactory.getLogger(LoveApp.class);

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;
    // mcp调用
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    // 构造一个chat client
    private final ChatClient chatClient;

    private static final String SYS_PROMPT = "扮演深耕恋爱心理领域的专家，开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问。"+
            "单身状态询问社交圈拓展及追求心仪对象的困扰，恋爱状态询问沟通习惯差异引发的矛盾，已婚已婚状态询问家庭责任与亲属关系处理的问题。"+
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案";

    /**
     * 初始化ai客户端
     * @param dashscopeChatModel
     */
    public LoveApp(ChatModel dashscopeChatModel) {
        // 基于文件的会话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 基于内存的会话记忆
        // ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYS_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                        // 自定义日志拦截器
                        ,new MyLoggerAdvisor()
                        // 自定义推理增强advisor，可按需开启
                        // ,new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * ai 基础对话  支持多轮记忆
     * @param message
     * @param chatId
     * @return
     */
    public String doChat (String message, String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        logger.info("text:{}", text);
        return text;
    }

    /**
     * ai 基础对话  支持多轮记忆   支持sse流式传输
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream (String message, String chatId){
        Flux<String> stringFlux = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
        return stringFlux;
    }

    record LoveReport(String title, List<String> suggestions){}

    /**
     * ai 恋爱报告功能 演示结构化输出
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport (String message, String chatId){
        LoveReport loveReport = chatClient.prompt()
                .system(SYS_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        logger.info("loveReport:{}", loveReport);
        return loveReport;
    }

    /**
     * 和RAG知识问答库进行对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag (String message, String chatId){

        // 方法重写器重写方法
        String query = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient.prompt()
                .user(query)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                // 应用RAG知识问答库
                //.advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用RAG检索增强服务  基于云知识库服务
                //.advisors(loveAppRagCloudAdvisor)
                // 应用RAG检索增强服务  基于pgVector向量存储
                //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // 应用RAG检索增强服务  文档查询器+上下文增强器
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                loveAppVectorStore, "单身"
                        )
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        logger.info("content:{}", content);
        return content;
    }

    /**
     * 恋爱报告功能  支持工具调用
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools (String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        logger.info("text:{}", text);
        return text;
    }

    /**
     * 恋爱报告功能  mcp
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp (String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        logger.info("text:{}", text);
        return text;
    }

}
