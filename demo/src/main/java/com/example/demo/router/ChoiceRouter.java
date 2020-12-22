package com.example.demo.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.example.demo.base.AbstractComponent;
import com.example.demo.base.SunwayMessage;
import com.example.demo.base.SunwayProcessInstance;
import com.example.demo.util.RouterUtils;

import lombok.Data;

@Data
public class ChoiceRouter extends AbstractComponent {
    @Deprecated
    private Long type = 1L;// 类型：1-条件判断选择分支，2-路由全部分支
    //private List<String> expressionList;//各分支得表达式集合，其中默认分支为“default”,表达式前缀有两种，js$$:表示用js语法解析(在前端实现更好)；java$$表示用java语法解析
    private List<RouterBranch> branchList;//分支队列，必须有默认分支，并且默认分支在最后一个
    
    @Override
    public Object process(SunwayMessage message) {
        if (type == 1) {
            return doRouterByCondition(message);
        } else if (type == 2) {
            return doRouterAll(message);
        }
        return message.getPayload();
    }

    private Object doRouterByCondition(SunwayMessage message) {
        List<AbstractComponent> defaultComponentList = null;
        for (RouterBranch routerBranch : branchList) {
            if ("default".equals(routerBranch.getExpresion())) {
                return routerBranch.process(message);
            }
            boolean pass = RouterUtils.evalExpression(routerBranch.getExpresion(), message.getPayload(), "");
            if (pass) {
                return routerBranch.process(message);
            }
        }
        return null;
    }

    public void addRouterBranch(RouterBranch routerBranch) {
        if (branchList == null) {
            branchList = new ArrayList<RouterBranch>();
        }
        branchList.add(routerBranch);
    }
    @Deprecated
    private Object doRouterAll(SunwayMessage message) {
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 10, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
        List<Future<Object>> results = new ArrayList<Future<Object>>();
        branchList.forEach(e -> {
            Task task = new Task(e.getComponentList());
            Future<Object> result = tpe.submit(task);
            results.add(result);
        });
        List<Object> retList = results.stream().map(e -> {
            try {
                return e.get();
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        message.setPayload(retList);
        return message.getPayload();
    }
    
    @Deprecated
    class Task implements Callable<Object> {
        private List<AbstractComponent> componentList;

        public Task(List<AbstractComponent> componentList) {
            this.componentList = componentList;
        }
        @Override
        public Object call() throws Exception {
            SunwayMessage message = SunwayProcessInstance.getSunwayProcessInstance("20200812").getMessage();
            componentList.forEach(e -> {
                Object ret = e.process(message);
                message.setPayload(ret);
            });
            return message.getPayload();
        }

    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }
    
}
