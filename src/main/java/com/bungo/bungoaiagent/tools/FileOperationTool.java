package com.bungo.bungoaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.bungo.bungoaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author bungosama_
 * @since 2025-07-03 03:36
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "read content from a file")
    public String readFile(@ToolParam(description = "name of file to read") String fileName){
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "write content to a file")
    public String writeFile(@ToolParam(description = "name of file to write")String fileName,
                            @ToolParam(description = "content to write to the file")String content){
        String filePath = FILE_DIR + "/" + fileName;
        FileUtil.mkdir(FILE_DIR);
        FileUtil.writeUtf8String(content, filePath);
        try {
            return "File written successfully to" + filePath;
        } catch (Exception e) {
            return "Error writing file: " + e.getMessage();
        }
    }

}
