package com.example.demo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DataSet {
    
    private String tableName;// 表名
    private List<Map<String, Object>> data;// 列值集合
    
    private DataSet() {
        
    }
    
    public DataSet(String tableName, List<Map<String, Object>> data) {
        this.tableName = tableName;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public static List<DataSet> parseDataSet(String jsonStr) {
        List<DataSet> dataSetList = new ArrayList<DataSet>();
//        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
//        jsonObj.forEach((key, value) -> {
//            DataSet dataSet = new DataSet();
//            dataSet.setTableName(key);
//            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
//            Set<String> columnSet = new HashSet<String>();
//            ((JSONArray)value).forEach(e -> {
//                Map<String, Object> map = (Map<String, Object>) e;
//                data.add(map);
//                columnSet.addAll(map.keySet());
//            });
//            dataSet.setData(data);
//            dataSetList.add(dataSet);
//        });
        return dataSetList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        data.forEach(e -> {
            sb.append("{");
            e.forEach((key, value) -> {
                sb.append("\"" + key + "\" : \"" + value + "\", ");
            });
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append("}, ");
        });
        if (sb.length() > 0) {
            sb.insert(0, "[");
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append("]");
        }
        return sb.toString();
    }
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    public Set<String> getColumnSet() {
        HashSet<String> columnSet = new HashSet<String>();
        data.forEach(e -> {
            columnSet.addAll(e.keySet());
        });
        return columnSet;
    }

}
