package com.example.demo.setpayload;

import com.example.demo.base.AbstractComponent;
import com.example.demo.base.SunwayMessage;
import com.example.demo.base.SunwayProcessInstance;

public class SetPaylaod extends AbstractComponent {
    
    private Object value;

    public SetPaylaod(Object value) {
        this.value = value;
    }
    
    @Override
    public Object process(SunwayMessage message) {
        System.out.println("调用SetPayload组件设置payload值为：" + value.toString());
        message.setPayload(value);
        SunwayProcessInstance processInstance = SunwayProcessInstance.getSunwayProcessInstance("20200812");
        processInstance.setMessage(message);
        return value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    

}
