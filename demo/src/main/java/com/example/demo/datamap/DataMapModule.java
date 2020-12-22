package com.example.demo.datamap;

import java.util.List;
import java.util.Map;

import com.example.demo.data.DataSet;

public class DataMapModule {

    private Map<String, Map<String, String>> tableMap;// 映射源列名称和目标列名称的键值对,表名-映射前列名-映射后列名
    private List<DataSet> dataList;// 待处理的数据集
    
    public List<DataSet> doMap() {
        dataList.forEach(dataSet -> {
            tableMap.forEach((tableName, map) -> {
                if (tableName.equalsIgnoreCase(dataSet.getTableName())) {
                    map.forEach((sourceColumn, targetColumn) -> {
                        dataSet.getData().forEach(row -> {
                            if (row.containsKey(sourceColumn)) {
                                Object value = row.get(sourceColumn);
                                row.remove(sourceColumn);
                                row.put(targetColumn, value);
                            }
                        });
                    });
                }
            });
        });
        return dataList;
    }

    public Map<String, Map<String, String>> getColumnMap() {
        return tableMap;
    }

    public void setColumnMap(Map<String, Map<String, String>> tableMap) {
        this.tableMap = tableMap;
    }

    public List<DataSet> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataSet> dataList) {
        this.dataList = dataList;
    }
    
}
