package com.example.demo.listendemo;

public class WorkFlowService {

    private TaskListener taskListener;

    public void addTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public void start() {
        if (taskListener != null) {
            taskListener.doStart(new TaskEvent(this));
        }
        System.out.println("就绪。。。");
    }

    public void execute() {
        if (taskListener != null) {
            taskListener.doExecute(new TaskEvent(this));
        }
        System.out.println("执行。。。");
    }

}
