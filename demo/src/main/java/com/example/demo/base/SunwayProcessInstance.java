package com.example.demo.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.demo.router.ChoiceRouter;
import com.example.demo.router.RouterBranch;
import com.example.demo.setpayload.SetPaylaod;

public class SunwayProcessInstance {

    private static Map<String, SunwayProcessInstance> processInstanceReference;
    private String processId;//流程实例ID
    private List<AbstractComponent> componentList;//组件队列
    private SunwayMessage message;// 消息传递参数

    private SunwayProcessInstance() {

    }

    private SunwayProcessInstance(String processId) {
        this.processId = processId;
        this.message = new SunwayMessage();
        //读取流程图配置构建队列
        componentList = new ArrayList<AbstractComponent>();

        // 分支表达式
        // 校验json样例
        String exp1 = "(${data.content[1].value} == '你好 萝卜') && ${data.content[id=11 || (id='\\}' || id='002')].id} == '002'";
        // 校验xml样例
        String exp2 = "(${flow.scatter-gather.custom-transformer[1].name} == 'Java4') && ${flow.scatter-gather.custom-transformer[class='com.yx.test.CustomDataMapper2' || name='Java2'].name} == 'Java2'";
        // 校验jdbc样例
        String exp3 = "${row[1].id} != 'aaa' || (${row[username='杨星' || (id < 2 && username ='c')].id} < 3 || ${row[0].username} == 'm')";
        // 默认分支
        String exp4= "default";

        SetPaylaod p1 = new SetPaylaod("json branch");
        SetPaylaod p2 = new SetPaylaod("xml branch");
        SetPaylaod p3 = new SetPaylaod("jdbc branch");
        SetPaylaod p4 = new SetPaylaod("defalt branch");
        ArrayList<AbstractComponent> q1 = new ArrayList<AbstractComponent>();
        ArrayList<AbstractComponent> q2 = new ArrayList<AbstractComponent>();
        ArrayList<AbstractComponent> q3 = new ArrayList<AbstractComponent>();
        ArrayList<AbstractComponent> q4 = new ArrayList<AbstractComponent>();
        q1.add(p1);
        q2.add(p2);
        q3.add(p3);
        q4.add(p4);
        RouterBranch routerBranch1 = new RouterBranch(exp1, q1);
        RouterBranch routerBranch2 = new RouterBranch(exp2, q2);
        RouterBranch routerBranch3 = new RouterBranch(exp3, q3);
        RouterBranch routerBranch4 = new RouterBranch(exp4, q4);
        ChoiceRouter router = new ChoiceRouter();
        router.addRouterBranch(routerBranch1);
        router.addRouterBranch(routerBranch2);
        router.addRouterBranch(routerBranch3);
        router.addRouterBranch(routerBranch4);

        componentList.add(router);
    }

    public static SunwayProcessInstance getSunwayProcessInstance(String processId) {
        //        if (processInstanceReference == null) {
        //            List<SunwayProcessInstance> processInstanceList = new ArrayList<SunwayProcessInstance>();
        //            processInstanceReference = new ThreadLocal<List<SunwayProcessInstance>>();
        //            processInstanceReference.set(processInstanceList);
        //        }
        //        if (processInstanceReference.get() != null) {
        //            List<SunwayProcessInstance> processInstanceList = processInstanceReference.get().stream().filter(e -> e.getProcessId().equals(processId)).collect(Collectors.toList());
        //            if (!processInstanceList.isEmpty()) {
        //                return processInstanceList.get(0);
        //            }
        //        }
        //        SunwayProcessInstance processInstance = new SunwayProcessInstance(processId);
        //        processInstanceReference.get().add(processInstance);
        //        return processInstance;
        if (processInstanceReference == null) {
            processInstanceReference = new HashMap<String, SunwayProcessInstance>();
        }
        if (processInstanceReference.get(processId) == null) {
            processInstanceReference.put(processId, new SunwayProcessInstance(processId));
        }
        return processInstanceReference.get(processId);
    }

    public void excute() {
        componentList.forEach(e -> {
            e.process(message);
        });
    }


    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public SunwayMessage getMessage() {
        return message;
    }

    public void setMessage(SunwayMessage message) {
        this.message = message;
    }

    public List<AbstractComponent> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<AbstractComponent> componentList) {
        this.componentList = componentList;
    }

}
