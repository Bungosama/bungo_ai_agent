package com.bungo.bungoaiagent.demo.invoke;


import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author bungosama_
 * @since 2025-06-24 20:39
 * 阿里云灵积 AI http 调用
 */
public class HttpAiInvoke {
    public static void main(String[] args) {
        String apiKey = System.getenv(TestApiKey.API_KEY); // 或直接写成字符串
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 构造 JSON 数据
        JSONObject inputJson = new JSONObject();
        JSONObject messageJson = new JSONObject();
        // 添加系统消息
        JSONObject sysMessage = new JSONObject();
        sysMessage.set("role", "system");
        sysMessage.set("content", "You are a helpful assistant.");
        // 添加用户消息
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");

        // 组装message数据
        messageJson.set("message", JSONUtil.createArray().set(sysMessage).set(userMessage));

        // 构建参数
        JSONObject parameterJson = new JSONObject();
        parameterJson.set("result_format", "message");

        // 构建完整请求体
        JSONObject requestJson = new JSONObject();
        requestJson.set("model", "qwen-plus");
        requestJson.set("input", messageJson);
        requestJson.set("parameters", parameterJson);

        // 发送请求
        String result = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", ContentType.JSON.toString())
                .body(requestJson.toString())
                .execute()
                .body();

        // 输出响应
        System.out.println(result);
    }
}
