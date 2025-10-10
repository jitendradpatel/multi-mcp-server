package com.example.mcp.tools;

import com.example.mcp.annotation.PlanningMcpServer;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PlanningMcpServer(endpoint = "stock", serverName = "stock-server")
public class StockService {
    @McpTool(name = "stocks_alert", description = "Get stock price alert for ticker")
    @Tool(name = "stocks_alert", description = "Get stock price alert for ticker")
    @PreAuthorize("hasAuthority('admin')")
    public String alert(String ticker) {
        return "Alert for " + ticker + ": threshold exceeded";
    }
}
