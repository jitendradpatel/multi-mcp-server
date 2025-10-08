package com.example.mcp.tools;

import com.example.mcp.annotation.PlanningMcpServer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@PlanningMcpServer(endpoint = "stock", serverName = "stock-server")
public class StockService {
    @Tool(name = "stocks.alert", description = "Get stock price alert for ticker")
    public String alert(String ticker) {
        return "Alert for " + ticker + ": threshold exceeded";
    }
}
