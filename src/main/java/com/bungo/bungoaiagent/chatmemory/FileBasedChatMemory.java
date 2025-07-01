package com.bungo.bungoaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 */
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        validateId(conversationId); //  安全校验，防止路径穿越注入（如 ../）

        //  转换为可变列表，避免 Kryo 无法序列化 List.of(...) 返回的不可变 List
        List<Message> newMessages = new ArrayList<>(messages);

        //  从已有文件中获取历史消息（若文件不存在则返回空）
        List<Message> conversationMessages = getOrCreateConversation(conversationId);

        //  合并旧消息 + 新消息
        conversationMessages.addAll(newMessages);

        //  保存回文件（持久化）
        saveConversation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        validateId(conversationId); //  conversationId 校验
        List<Message> allMessages = getOrCreateConversation(conversationId);

        //  提取最后 N 条消息，避免索引越界
        int fromIndex = Math.max(0, allMessages.size() - lastN);
        return new ArrayList<>(allMessages.subList(fromIndex, allMessages.size()));
    }

    @Override
    public void clear(String conversationId) {
        validateId(conversationId); //  conversationId 校验
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            boolean deleted = file.delete(); //  删除对应会话文件
            if (!deleted) {
                throw new RuntimeException("Failed to delete memory file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * 从文件中读取会话，如果文件不存在则创建一个空会话
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        if (!file.exists()) return new ArrayList<>();

        try (Input input = new Input(new FileInputStream(file))) {
            //  反序列化为 ArrayList（注意：必须使用 Kryo 支持的结构）
            return kryo.readObject(input, ArrayList.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read memory file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 将会话消息写入文件（序列化）
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            //  使用 Kryo 将消息写入文件（会覆盖旧文件）
            kryo.writeObject(output, messages);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write memory file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 构造出指定会话 ID 的保存文件路径
     */
    private File getConversationFile(String conversationId) {
        //  文件名使用 conversationId.kryo，存在 BASE_DIR 下
        return new File(BASE_DIR, conversationId + ".kryo");
    }


    //  防止 ../ 路径注入攻击
    private void validateId(String id) {
        if (id.contains("..") || id.contains("/") || id.contains("\\") || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid conversation ID: " + id);
        }
    }
}