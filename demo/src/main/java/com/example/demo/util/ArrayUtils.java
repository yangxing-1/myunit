package com.example.demo.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.exception.EmptyArrayException;
import com.example.demo.exception.OutOfRangeException;
import com.example.demo.exception.TypeMismatchException;

public abstract class ArrayUtils {
    /**
     * 获取数组中元素的实际类型
     */
    public static final Class<?> getArrayType(Class<?> arrayClass) {
        if (!arrayClass.isArray()) {
            throw new TypeMismatchException("array", arrayClass);
        }
        
        Class<?> componentType = arrayClass.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        
        return componentType;
    }
    
    /**
     * 判断数组是否为{@literal null}或空（长度为{@literal 0}）
     */
    public static final boolean hasElement(Object array) {
        return (array != null && array.getClass().isArray() && Array.getLength(array) > 0);
    }
    
    /**
     * 判断数组是否含有给定的元素
     */
    public static final <T> boolean contains(T[] array, T value) {
        return array != null && array.length > 0 && Arrays.stream(array).anyMatch(t -> (value == null && t == null) || (value != null && value.equals(t)));
    }
    
    /**
     * 判断字符串数组是否含有给定的元素
     */
    public static final boolean containsIgnoreCase(String[] array, String value) {
        return array != null && array.length > 0 && Arrays.stream(array).anyMatch(t -> (value == null && t == null) || (value != null && value.equalsIgnoreCase(t)));
    }
    
    /**
     * 拼接两个数组
     */
    public static final <T> T[] concat(T[] first, T[] second) {
        if (first == null) {
            return null;
        }
        
        if (second == null) {
            return first;
        }
        
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length);
        
        System.arraycopy(first, 0, array, 0, first.length);
        System.arraycopy(second, 0, array, first.length, second.length);
        
        return array;
    }
    
    /**
     * 在数组后面添加一个元素
     */
    public static final <T> T[] append(T[] array, T target) {
        if (target == null) {
            return array;
        }
        
        int length = (array == null ? 0 : array.length);
        
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) Array.newInstance(target.getClass(), length + 1);
        
        if (length > 0) {
            System.arraycopy(array, 0, newArray, 0, length);
        }
        newArray[length] = target;
        
        return newArray;
    }
    
    /**
     * 转换数组为字符串，用给定的分隔符分开
     */
    public static <T> String arrayToDelimitedString(T[] array, String delim) {
        if (!ArrayUtils.hasElement(array)) {
            return "";
        }
        
        if (array.length == 1) {
            return ConvertUtils.convert(array[0], String.class, "");
        }
        
        final StringBuilder sb = new StringBuilder();
        
        Arrays.stream(array).forEach(v -> sb.append(ConvertUtils.convert(v, String.class, "")).append(delim));
        
        return sb.substring(0, sb.length() - delim.length());
    }
    
    /**
     * 如果给定的数组为{@literal null}或空（长度为{@literal 0}）时抛出异常
     */
    public static <T> T[] requireNotEmpty(T[] array) {
        if (!hasElement(array)) {
            throw new EmptyArrayException();
        }
        
        return array;
    }
    
    /**
     * 通过泛型类来创建一个空的一维数组
     */
    public static <T> T[] emptyArray(Class<T> componentType) {
        return newInstance(componentType, 0);
    }
    
    /**
     * 通过泛型类来创建指定长度的一维数数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newInstance(Class<T> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }
    
    /**
     * 通过泛型类和给定的{@literal instance}来创建一个一维数组
     */
    public static <T> T[] instanceToArray(T instance) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(instance.getClass(), 1);
        array[0] = instance;
        
        return array;
    }
    
    /**
     * 从指定的数组中获取指定类型的唯一值
     */
    @SuppressWarnings("unchecked")
    public static <A, T> T getUniqueTypeValue(A[] array, Class<T> type) {
        if (array == null || array.length == 0) {
            return null;
        }
        
        List<T> list = Arrays.stream(array).filter(a -> a != null && a.getClass().equals(type)).map(a -> (T) a).collect(Collectors.toList());
        
        if (list.size() > 1) {
            throw new OutOfRangeException(GsonUtils.toJson(array) + " contains more than one instance of type " + type + ".");
        }
        
        if (list.isEmpty()) {
            return null;
        }
        
        return list.get(0);
    }
    
    /**
     * 从指定的数组中获取指定类型子类的唯一值
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getUniqueAssignableType(Class<?>[] array, Class<T> type) {
        if (array == null || array.length == 0) {
            return null;
        }
        
        List<Class<?>> list = Arrays.stream(array).filter(a -> a != null && type.isAssignableFrom(a)).collect(Collectors.toList());
        
        if (list.size() > 1) {
            throw new OutOfRangeException(GsonUtils.toJson(array) + " contains more than one class assignable type " + type + ".");
        }
        
        if (list.isEmpty()) {
            return null;
        }
        
        return (Class<? extends T>) list.get(0);
    }
    
    /**
     * Test whether the given two array contains the same values by comparing with equals method
     */
    public static <T> boolean isEquals(T[] left, T[] right) {
        if (left == null && right == null) {
            return true;
        }
        
        if ((left == null && right != null)
                || (left != null && right == null)) {
            return false;
        }
        
        if (left.length != right.length) {
            return false;
        }
        
        for (int i = 0, j = left.length; i < j; i++) {
            T leftValue = left[i];
            T rightValue = right[i];
            
            if (leftValue == null) {
                if (rightValue != null) {
                    return false;
                }
            } else {
                if (!leftValue.equals(rightValue)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 数组转换成{@code List}
     */
    public static <T> List<T> asList(@SuppressWarnings("unchecked") T ... items) {
        if (items == null) {
            return CollectionUtils.emptyList();
        } else {
            return Arrays.asList(items);
        }
    }
    
    /**
     * 判断数组是否为空
     */
    @SuppressWarnings("unchecked") 
    public static <T> boolean isEmpty(T ... items) {
        return items == null || items.length == 0;
    }
    
    /**
     * 通过数组的开始下标（0-based）和长度获取数组的子集,
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] array, int start, int length) {
        if (array == null || array.length == 0) {
            return array;
        }
        
        Class<T> type = (Class<T>) array[0].getClass();
        
        if (array.length < start + length) {
            return (T[]) ArrayUtils.emptyArray(type);
        }
        
        T[] subarray = ArrayUtils.newInstance(type, length);
        
        System.arraycopy(array, start, subarray, 0, length);
        
        return subarray;
    }
}
