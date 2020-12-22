package com.example.demo.router;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.base.AbstractComponent;
import com.example.demo.base.SunwayMessage;

import lombok.Data;

@Data
public class RouterBranch extends AbstractComponent {
    
    private String expresion;
    
    private List<AbstractComponent> componentList;
    
    public RouterBranch(String expresion, List<AbstractComponent> componentList) {
        this.expresion = expresion;
        this.componentList = componentList;
    }
    
    @Override
    public Object process(SunwayMessage message) {
        for (AbstractComponent abstractComponent : componentList) {
            message.setPayload(abstractComponent.process(message));
        };
        return message.getPayload();
    }

    public void addComponent(AbstractComponent abstractComponent) {
        if (componentList == null) {
            componentList = new ArrayList<AbstractComponent>();
        }
        componentList.add(abstractComponent);
    }
}
