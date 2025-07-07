package com.bungo.bungoimagesearchmcpserver.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-07-04 19:42
 */
@SpringBootTest
@ActiveProfiles("local")
class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        Optional<String> result = imageSearchTool.getFirstImageOriginalUrl("tiger");
        Assertions.assertNotNull(result);
    }
}