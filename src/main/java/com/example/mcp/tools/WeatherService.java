package com.example.mcp.tools;

import com.example.mcp.annotation.PlanningMcpServer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@PlanningMcpServer(endpoint = "weather", serverName = "weather-server")
public class WeatherService {
    @Tool(name = "weather.lookup", description = "Lookup current weather for a city")
    public String lookup(@ToolParam(description = "city name") String city) {
        return "Sunny in " + city;
    }
}
