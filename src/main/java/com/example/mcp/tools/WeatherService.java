package com.example.mcp.tools;

import com.example.mcp.annotation.PlanningMcpServer;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@PlanningMcpServer(endpoint = "weather", serverName = "weather-server")
public class WeatherService {
    @McpTool(name = "weather_lookup", description = "Lookup current weather for a city")
    @Tool(name = "weather_lookup", description = "Lookup current weather for a city")
    public String lookup(String city) {
        return "Sunny in " + city;
    }
}
