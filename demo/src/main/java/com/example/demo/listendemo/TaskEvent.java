package com.example.demo.listendemo;

public class TaskEvent {

    private WorkFlowService workFlowService;
    
    public TaskEvent() {}
    
    public TaskEvent(WorkFlowService workFlowService) {
        this.workFlowService = workFlowService;
    }

    public WorkFlowService getWorkFlowService() {
        return workFlowService;
    }

    public void setWorkFlowService(WorkFlowService workFlowService) {
        this.workFlowService = workFlowService;
    }
}
