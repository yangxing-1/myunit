package com.example.demo.listendemo;

public interface TaskListener {
    
    public void doStart(TaskEvent taskEvent);
    public void doExecute(TaskEvent taskEvent);

}
