package com.bungo.bungoaiagent;

import cn.hutool.poi.excel.ExcelUtil;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class BungoAiAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BungoAiAgentApplication.class, args);
	}

}
