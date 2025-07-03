package com.bungo.bungoaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.bungo.bungoaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author bungosama_
 * @since 2025-07-03 03:36
 *  资源下载工具
 */
public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            //HttpUtil.downloadFile(url, new File(filePath));
            //return "Resource downloaded successfully to: " + filePath;
            // 构造请求，添加 User-Agent
            HttpResponse response = HttpRequest.get(url)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X)")
                    .execute();

            if (response.isOk()) {
                // 下载成功，写入文件
                IoUtil.write(new FileOutputStream(filePath), true, response.bodyStream().readAllBytes());
                return "Resource downloaded successfully to: " + filePath;
            } else {
                return "Error: Server returned status code " + response.getStatus();
            }
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}