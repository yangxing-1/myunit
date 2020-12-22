package com.example.demo.base;

import java.io.Serializable;

public interface Mediator extends Serializable {
    
    Long getServiceId();
    void setServiceId(Long serviceId);
    Object process(SunwayMessage message);
}
