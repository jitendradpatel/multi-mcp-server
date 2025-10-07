package com.example.mcpdemo.tools;

//import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    @Tool(name = "stocks.alert", description = "Get stock price alert for ticker")
    public String alert(String ticker) {
        return "Alert for " + ticker + ": threshold exceeded";
    }
}
