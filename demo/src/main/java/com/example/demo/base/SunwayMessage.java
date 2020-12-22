package com.example.demo.base;

import javax.servlet.http.HttpServletRequest;

public class SunwayMessage {
    private HttpServletRequest request;//http请求消息
    private Object requestBody;//http请求body正文-目前支持json和xml
    private transient Object payload;//组件间传递的消息值

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }
}
