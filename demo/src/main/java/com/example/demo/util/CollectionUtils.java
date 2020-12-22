package com.example.demo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.exception.UnexpectedException;


public abstract class CollectionUtils {
    /**
     * 判断集合是否为空
     */
    public static <T> boolean isEmpty(Collection<T> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
    
    /**
     * 获取两个集合的交集（不含相同的值）
     */
    public static final <T> List<T> union(Collection<T> first, Collection<T> second) {
        List<T> unionList = new ArrayList<>(first);
        unionList.addAll(second);
        
        return unionList.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * 获取两个集合的并集（不含相同的值）
     */
    public static final <T> List<T> intersection(Collection<T> first, Collection<T> second) {
        return first.stream().filter(f -> second.contains(f)).distinct().collect(Collectors.toList());
    }
    
    /**
     * 获取两个集合的补集（不含相同的值）
     */
    public static final <T> List<T> disjunction(Collection<T> first, Collection<T> second) {
        List<T> disjunctionList = subtract(first, second);
        disjunctionList.addAll(subtract(second, first));
        
        return disjunctionList;
    }
    
    /**
     * 获取两个集合的差集（不含相同的值）
     */
    public static final <T> List<T> subtract(Collection<T> first, Collection<T> second) {
        return first.stream().filter(f -> !second.contains(f)).distinct().collect(Collectors.toList());
    }
    
    public static final boolean containsIgnoreCase(Collection<String> collection, String value) {
        if (collection.isEmpty()) {
            return false;
        }
        
        for (String v : collection) {
            if (v == null) {
                if (value == null) {
                    return true;
                }
            } else {
                if (v.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static final Object getFirstValue(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        
        return map.values().toArray()[0];
    }
    
    public static final Object getValueIgnorecase(Map<String, ?> map, String key) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        
        String actualKey = map.keySet().stream().filter(k -> k.equalsIgnoreCase(key)).findFirst().orElse(null);
        
        if (StringUtils.isEmpty(actualKey)) {
            return null;
        }
        
        return map.get(actualKey);
    }
    
    @SuppressWarnings("unchecked")
    public static final <S, T> List<T> convert(List<S> itemList, Class<T> type) {
        if (itemList.isEmpty()) {
            return Collections.emptyList();
        }
        
        Class<S> sourceType = (Class<S>) itemList.get(0).getClass();
        
        if (type.isAssignableFrom(sourceType)) {
            return itemList.stream().map(i -> (T) i).collect(Collectors.toList());
        }
        
        return itemList.stream().map(s -> (T) ConvertUtils.convert(s, type)).collect(Collectors.toList());
    }
    
    public static final <T> List<T> distinct(List<T> itemList) {
        return itemList.stream().distinct().collect(Collectors.toList());
    }
    
    public static final void lowerCaseKey(Map<String, Object> map) {
        new HashSet<>(map.keySet()).forEach(k -> {
            String lowerCaseKey = k.toLowerCase();
            if (!k.equals(lowerCaseKey)) {
                Object value = map.get(k);
                map.remove(k);
                map.put(lowerCaseKey, value);
            }
        });
    }
    
    public static final void upperCaseKey(Map<String, Object> map) {
        new HashSet<>(map.keySet()).forEach(k -> {
            String upperCaseKey = k.toUpperCase();
            if (!k.equals(upperCaseKey)) {
                Object value = map.get(k);
                map.remove(k);
                map.put(upperCaseKey, value);
            }
        });
    }
    
    public static final <K, V> void putIfNotBlank(Map<K, V> map, K key, V value) {
        if (value != null
                && !StringUtils.isBlank(value.toString())) {
            map.put(key, value);
        }
    }
    
    /**
     * 冒泡排序
     */
    public static final <T> void sort(List<T> list, Comparator<T> comparator) {
        int size = list.size();
        
        if (size <= 1) {
            return;
        }
        
        List<T> sortedList = new ArrayList<>();
        
        while(sortedList.size() < size) {
            int innerSize = list.size();
            
            boolean minFound = true; // 是否已找到最小值，默认是
            
            for(int i = 0; i < innerSize; i++) {
                T item = list.get(i);
                
                minFound = true;
                
                for (int j = 0; j < innerSize; j++) {
                    if (i != j) {
                        T another = list.get(j);
                        
                        if (comparator.compare(item, another) > 0) {
                            minFound = false;
                            break;
                        }
                    }
                }
                
                if (minFound) {
                    sortedList.add(item);
                    list.remove(i);
                    break;
                }
            }
            
            if (!minFound) {
                throw new UnexpectedException("GIKAM.EXCEPTION.ORDER_INFINITE_LOOP");
            }
        }
        
        list.addAll(sortedList);
    }
    
    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }
    
    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>();
    }
    
    public static final <T> String toDelimitedString(Collection<T> collection, String delim) {
        StringBuilder sb = new StringBuilder();
        
        for (T item : collection) {
            if (sb.length() > 0) {
                sb.append(delim);
            }
            
            sb.append(ConvertUtils.convert(item, String.class));
        }
        
        return sb.toString();
    }
    
    public static final <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> sortedMap = new LinkedHashMap<>();
        
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
        
        return sortedMap;
    }
}
