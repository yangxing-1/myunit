package com.example.demo.datafilter;

public class Condition {
    private String key;
    private int operator;// 1-等于，2-大于，3-小于，4-包含，5-不包含
    private String value;
    private boolean union;// true-and,false-or
    
    public Condition(String key, int operator, String value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public int getOperator() {
        return operator;
    }
    public void setOperator(int operator) {
        this.operator = operator;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUnion() {
        return union;
    }

    public void setUnion(boolean union) {
        this.union = union;
    }
    
    

}
