package com.example.demo.datafilter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.data.DataSet;

public class DataFilterModule {
    private List<Condition> filter;// 过滤条件
    private List<DataSet> dataList;// 待处理的数据集
    
    public List<DataSet> doMap() {
        List<DataSet> resultDataList = dataList.stream().filter(e -> verify(e)).collect(Collectors.toList());
        List<DataSet> resultDataList1 = dataList.stream().map(dataSet -> {
            List<Map<String, Object>> rowList = dataSet.getData();
            Iterator<Map<String, Object>> it = rowList.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = it.next();
                
            }
            dataSet.setData(rowList);
            return dataSet;
        }).collect(Collectors.toList());
        return resultDataList;
    }


    private boolean verify(DataSet dataSet) {
        List<Map<String, Object>> rows = dataSet.getData();
        boolean result = true;
        filter.stream().map(condition -> {
            switch (condition.getOperator()) {
            case 1:
                
                return true;
            case 2:

                return true;
            case 3:

                return true;
            case 4:

                return true;
            case 5:

                return true;
            default:
                return true;
            }
        });
        return true;
    }


    public List<DataSet> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataSet> dataList) {
        this.dataList = dataList;
    }


    public List<Condition> getFilter() {
        return filter;
    }


    public void setFilter(List<Condition> filter) {
        this.filter = filter;
    }
}
