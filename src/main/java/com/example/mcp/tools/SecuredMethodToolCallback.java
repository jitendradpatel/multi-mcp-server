package com.example.mcp.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;

import java.lang.reflect.Method;

public class SecuredMethodToolCallback implements ToolCallback {

    private final Object serviceBean;
    private final MethodToolCallback delegate;
    private final Object targetBean;
    private final Method method;

    public SecuredMethodToolCallback(Object serviceBean,
                                     Object targetBean,
                                     Method method, MethodToolCallback delegate) {
        this.serviceBean = serviceBean;
        this.targetBean = targetBean;
        this.method = method;
        this.delegate = delegate;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public String call(String toolInput) {
//        delegate
//        return proxyDelegate.call(args);
        return null;
    }
}
