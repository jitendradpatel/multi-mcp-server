package com.example.mcpdemo.config;

import com.example.mcpdemo.tools.StockService;
import com.example.mcpdemo.tools.WeatherService;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;

@Configuration
public class McpServerConfig {

    @Bean
    public WebMvcSseServerTransportProvider weatherTransportProvider() {
        return WebMvcSseServerTransportProvider.builder()
                .sseEndpoint("weather")
                .messageEndpoint("weather")
                .build();
    }

    @Bean
    public WebMvcSseServerTransportProvider stockTransportProvider() {
        return WebMvcSseServerTransportProvider.builder()
                .sseEndpoint("stock")
                .messageEndpoint("stock")
                .build();
    }

    @Bean
    public RouterFunction<?> mcpRouterFunction(WebMvcSseServerTransportProvider weatherTransportProvider, WebMvcSseServerTransportProvider stockTransportProvider) {
        return weatherTransportProvider.getRouterFunction().and(stockTransportProvider.getRouterFunction());
    }

    @Bean
    public McpSyncServer weatherMcpServer(WebMvcSseServerTransportProvider weatherTransportProvider, WeatherService weatherService) {
        return McpServer.sync(
                        weatherTransportProvider
                )
                .serverInfo("weather-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .tools(McpToolUtils.toSyncToolSpecifications(ToolCallbacks.from(weatherService)))
                .build();
    }

    @Bean
    public McpSyncServer stockMcpServer(WebMvcSseServerTransportProvider stockTransportProvider, StockService stockService) {
        return McpServer.sync(
                        stockTransportProvider
                )
                .serverInfo("stock-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .tools(McpToolUtils.toSyncToolSpecifications(ToolCallbacks.from(stockService)))
                .build();
    }
}
