package com.example.mcp.config;

import com.example.mcp.annotation.PlanningMcpServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class McpServerConfig implements BeanFactoryPostProcessor {

    private final List<WebMvcSseServerTransportProvider> providers = new ArrayList<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> type = beanFactory.getType(beanName);
            if (type != null && type.isAnnotationPresent(PlanningMcpServer.class)) {
                PlanningMcpServer annotation = type.getAnnotation(PlanningMcpServer.class);

                // Transport provider
                WebMvcSseServerTransportProvider provider = WebMvcSseServerTransportProvider.builder()
                        .sseEndpoint(annotation.endpoint())
                        .messageEndpoint(annotation.endpoint())
                        .build();

                // Underlying service bean (WeatherService, StockService, etc.)
                Object serviceBean = beanFactory.getBean(beanName);

                // MCP Server
                McpSyncServer mcpServer = McpServer.sync(provider)
                        .serverInfo(annotation.serverName(), annotation.version())
                        .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                        .tools(McpToolUtils.toSyncToolSpecifications(ToolCallbacks.from(serviceBean)))
                        .build();

                // Register beans
                beanFactory.registerSingleton(annotation.endpoint() + "TransportProvider", provider);
                beanFactory.registerSingleton(annotation.serverName(), mcpServer);

                providers.add(provider);
            }
        }
    }

    @Bean
    public RouterFunction<?> mcpRouterFunction() {
        return providers.stream()
                .map(WebMvcSseServerTransportProvider::getRouterFunction)
                .reduce(RouterFunction::and)
                .orElse(request -> null);
    }
}
