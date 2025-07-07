package com.bungo.bungoimagesearchmcpserver.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author bungosama_
 * @since 2025-07-04 19:29
 */
@Service
public class ImageSearchTool {

    private static final String BASE_URL = "https://api.pexels.com/v1/search";

    private final String apiKey;

    public ImageSearchTool(@Value("${pexels.api-key}")String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 根据关键词获取一张图片的原图地址
     * @param query 搜索关键词，例如 "nature"
     * @return 图片 URL（original 尺寸），如果失败返回空 Optional
     */
    public Optional<String> getFirstImageOriginalUrl(String query) {
        String url = BASE_URL + "?query=" + query + "&per_page=1";

        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", apiKey)
                .timeout(5000)
                .execute();

        if (response.getStatus() != 200) {
            System.err.println("请求失败，状态码：" + response.getStatus());
            return Optional.empty();
        }

        JSONObject json = JSONUtil.parseObj(response.body());
        JSONArray photos = json.getJSONArray("photos");

        if (photos == null || photos.isEmpty()) {
            System.out.println("没有找到图片");
            return Optional.empty();
        }

        JSONObject firstPhoto = photos.getJSONObject(0);
        JSONObject src = firstPhoto.getJSONObject("src");

        return Optional.ofNullable(src.getStr("original"));
    }

    // 如果你还想获取摄影师信息，也可以单独封装方法
    public Optional<String> getPhotographerName(String query) {
        String url = BASE_URL + "?query=" + query + "&per_page=1";

        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", apiKey)
                .timeout(5000)
                .execute();

        if (response.getStatus() != 200) {
            return Optional.empty();
        }

        JSONObject json = JSONUtil.parseObj(response.body());
        JSONArray photos = json.getJSONArray("photos");

        if (photos == null || photos.isEmpty()) {
            return Optional.empty();
        }

        JSONObject firstPhoto = photos.getJSONObject(0);
        return Optional.ofNullable(firstPhoto.getStr("photographer"));
    }

}
