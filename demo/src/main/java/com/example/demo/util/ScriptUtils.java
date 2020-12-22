package com.example.demo.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class ScriptUtils {
    private static final ScriptEngineManager manager = new ScriptEngineManager();
    private static final ScriptEngine javaScriptEngine = manager.getEngineByName("JavaScript");
    
    @SuppressWarnings("unchecked")
    public static final <T> T evalJavaScript(String javaScript) {
        try {
            return (T) javaScriptEngine.eval(javaScript);
        } catch (ScriptException e) {
            System.out.println("执行javascript脚本出错，出错语句为："+javaScript);
        }
        return null;
    }
}
