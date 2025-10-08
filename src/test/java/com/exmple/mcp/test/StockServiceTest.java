package com.exmple.mcp.test;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StockServiceTest {

    private static final String BEARER_TOKEN = "xxxxx";
    private static McpSyncClient mcpSyncClient;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ENDPOINT = "stock";

    @BeforeAll
    public static void setup() {
        mcpSyncClient = McpClient.sync(HttpClientStreamableHttpTransport.builder(BASE_URL)
                .endpoint(ENDPOINT)
                .httpRequestCustomizer((builder, uri, method, body, headers) ->
                        builder.header("Authorization", "Bearer " + BEARER_TOKEN)
                )
                .build()
        ).build();
        mcpSyncClient.initialize();
        mcpSyncClient.ping();
    }

    @Test
    public void testListTools() {
        McpSchema.ListToolsResult toolsList = mcpSyncClient.listTools();
        for (var tool : toolsList.tools()) {
            System.out.println("Tool = " + tool.name() + ", description = " + tool.description());
        }
    }

    @Test
    public void testCallStocksAlert() {
        System.out.println("Calling tool: stocks_alert");
        
        McpSchema.CallToolRequest arguments = McpSchema.CallToolRequest.builder()
                .name("stocks_alert")
                .arguments(java.util.Map.of("ticker", "AAPL"))
                .build();
        
        try {
            McpSchema.CallToolResult result = mcpSyncClient.callTool(arguments);
            System.out.println("Tool stocks_alert result: " + result.content());
        } catch (Exception e) {
            System.err.println("Error calling tool stocks_alert: " + e.getMessage());
        }
    }
}
