package com.example.demo.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import com.example.demo.exception.ClassInstantiationException;
import com.example.demo.exception.UnexpectedException;
import com.example.demo.exception.ZipException;
import com.example.demo.support.MemoryCacheHelper;
import com.example.demo.support.Pair;


public class ClassUtils {
    /**
     * Map with primitive wrapper type as key and corresponding primitive type as
     * value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    /**
     * Map with primitive type as key and corresponding wrapper type as value, for
     * example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    // initialize
    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        primitiveWrapperTypeMap.forEach((w, p) -> primitiveTypeToWrapperMap.put(p, w));
    }

    /**
     * Instantiate a class with the less parameters constructor, the constructor is
     * not restricted as public. The constructor parameters if present are default
     * values.
     *
     * @see #getPrimitiveDefaultValue(Class)
     */
    @SuppressWarnings("unchecked")
    public static final <T> T newInstance(final Class<T> clazz) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        final Constructor<?> constructor = Arrays.stream(constructors)
                .min((c1, c2) -> c1.getParameterCount() - c2.getParameterCount()).get();

        final Class<?>[] parameterTypes = constructor.getParameterTypes();

        final Object[] parameterDefaultValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];

            parameterDefaultValues[i] = (isPrimitiveType(parameterType) ? getPrimitiveDefaultValue(parameterType)
                    : null);
        }

        try {
            return (T) constructor.newInstance(parameterDefaultValues);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ClassInstantiationException(clazz, e);
        }
    }

    /**
     * Instantiate a class with type value pairs, the {@code typeValuePairs} types
     * are<br>
     * {@code Class} objects that identify the constructor's formal parameter
     * types,<br>
     * in declared order. The {@code typeValuePairs} values are the initialization
     * parameters to create and initialize with the identical constructor.
     */
    @SafeVarargs
    public static final <T> T newInstance(final Class<T> clazz,
            final Pair<? extends Class<?>, ? extends Object>... typeValuePairs) {
        if (typeValuePairs == null || typeValuePairs.length == 0) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ClassInstantiationException(clazz, e);
            }
        }

        final Class<?>[] parameterTypes = new Class<?>[typeValuePairs.length];
        final Object[] parameterValues = new Object[typeValuePairs.length];

        for (int i = 0; i < typeValuePairs.length; i++) {
            parameterTypes[i] = typeValuePairs[i].getFirst();
            parameterValues[i] = typeValuePairs[i].getSecond();
        }

        try {
            final Constructor<T> constructor = clazz.getConstructor(parameterTypes);

            return constructor.newInstance(parameterValues);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new ClassInstantiationException(clazz, e);
        }
    }

    /**
     * Determine whether the given class has a public constructor with the given
     * signature, and return it if available (else return {@code null}).
     * <p>
     * Essentially translates {@code NoSuchMethodException} to {@code null}.
     */
    public static final <T> Constructor<T> getConstructorIfAvailable(final Class<T> clazz,
            final Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Get raw type of the specified class, the class may be a proxy
     */
    public static final Class<?> getRawType(Class<?> clazz) {
        if (clazz.getName().startsWith("com.sun.proxy.$Proxy")) {
            return clazz.getInterfaces()[0];
        }

        while (clazz.getName().contains("CGLIB$") || clazz.getName().contains("$$EnhancerByCGLIB$$")) {
            clazz = clazz.getSuperclass();
        }

        return clazz;
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     */
    public static final boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null
                && (clazz.getName().contains("CGLIB$") || clazz.getName().contains("$$EnhancerByCGLIB$$")));
    }

    /**
     * Check whether the specified class is a primitive type
     */
    public static final boolean isPrimitiveType(Class<?> clazz) {
        return primitiveTypeToWrapperMap.containsKey(clazz);
    }

    /**
     * Check whether the specified class is a wrapper type
     */
    public static final boolean isWrapperType(Class<?> clazz) {
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * Get wrapper type of the specified primitive type
     */
    public static final Class<?> getWrapperType(Class<?> primitiveType) {
        return primitiveTypeToWrapperMap.get(primitiveType);
    }

    /**
     * Get primitive type of the specified wrapper class
     */
    public static final Class<?> getPrimitiveType(Class<?> wrapperType) {
        return primitiveWrapperTypeMap.get(wrapperType);
    }

    /**
     * Get primitive default value
     */
    public static final Object getPrimitiveDefaultValue(Class<?> primitiveType) {
        if (Boolean.TYPE == primitiveType) {
            return false;
        } else if (Byte.TYPE == primitiveType) {
            return '0';
        } else if (Character.TYPE == primitiveType) {
            return '0';
        } else if (Short.TYPE == primitiveType) {
            return Short.parseShort("0");
        } else if (Integer.TYPE == primitiveType) {
            return 0;
        } else if (Float.TYPE == primitiveType) {
            return 0f;
        } else if (Double.TYPE == primitiveType) {
            return 0d;
        } else if (Long.TYPE == primitiveType) {
            return 0l;
        } else {
            return null;
        }
    }

    /**
     * Resolve the given class if it is a primitive class, returning the
     * corresponding primitive wrapper type instead.
     */
    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        return ((clazz.isPrimitive() && clazz != void.class) ? getWrapperType(clazz) : clazz);
    }

    /**
     * Check if the given type represents a "simple" value type: a primitive, a
     * String or other CharSequence, a Number, a Date, a JSR310 or an Object.
     */
    public static final boolean isSimpleType(Class<?> type) {
        return isPrimitiveType(type) || isWrapperType(type) || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)
                || Temporal.class.isAssignableFrom(type) || Object.class == type;
    }

    /**
     * get the specified class witht the given class name
     */
    @SuppressWarnings("unchecked")
    public static final <T> Class<T> getClass(String className) {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = contextCL == null ? ClassLoader.getSystemClassLoader() : contextCL;

        try {
            String abbreviation = getClassAbbreviation(className);
            if (className.equals(abbreviation)) {
                return (Class<T>) Class.forName(getFullyQualifiedName(className), false, loader);
            } else { // primitive type
                return (Class<T>) Class.forName("[" + abbreviation, false, loader).getComponentType();
            }
        } catch (ClassNotFoundException cnfe) {
            throw new UnexpectedException(cnfe);
        }
    }

    @SuppressWarnings("unchecked")
    public static final <T> Class<T> getSunwayClass(String simpleName) {
        return (Class<T>) getAllClasses("com.sunwayworld").stream()
                .filter(c -> c.getSimpleName().equalsIgnoreCase(simpleName)).findFirst().orElse(null);
    }

    /**
     * get the predicated classes under the given package
     */
    public static final List<Class<?>> getPredicatedClasses(String packageName, Predicate<Class<?>> classTester) {
        String packageDirName = StringUtils
                .removeStart(StringUtils.removeEnd(StringUtils.replace(packageName, ".", "/"), "/"), "/");

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packageDirName + "/**/*.class");
            List<Class<?>> list = new ArrayList<Class<?>>();
            // 把每一个class文件找出来
            for (Resource r : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
                String className = metadataReader.getClassMetadata().getClassName();
                // 为了避免扫描到starter中的包导致NoClassDefFoundError异常，增加该处理
                if (className != null && !className.startsWith("com.sunwayworld.starter")) {
                    Class<?> clazz = ClassUtils.getClass(metadataReader.getClassMetadata().getClassName());

                    if (classTester.test(clazz)) {
                        list.add(clazz);
                    }
                }
            }

            return list;
        } catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }

    /**
     * test whether contains any the predicated classes under the given package
     */
    public static final boolean hasAnyMatchClass(String packageName, Predicate<Class<?>> classTester) {
        List<Class<?>> classList = getAllClasses(packageName);

        for (Class<?> clazz : classList) {
            if (classTester.test(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines all interfaces implemented by the class or interface.
     */
    public static final List<Class<?>> getAllInterfaces(Class<?> clazz) {
        List<Class<?>> interfaceList = new ArrayList<>();

        while (clazz != null && !clazz.equals(Object.class)) {
            if (clazz.isInterface()) {
                interfaceList.add(clazz);
            }

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                interfaceList.addAll(getAllInterfaces(interfaceClass));
            }

            clazz = clazz.getSuperclass();
        }

        return interfaceList.stream().distinct().collect(Collectors.toList());
    }

    public static final List<Class<?>> getAllClasses(String packageName) {
        String key = "classesUnderPackage:" + packageName;
        
        List<Class<?>> classList = MemoryCacheHelper.get(key);
        if (classList == null) {
            synchronized (ClassUtils.class) {
                classList = MemoryCacheHelper.get(key);

                if (classList == null) {
                    classList = new ArrayList<>();

                    String packageDirName = StringUtils
                            .removeStart(StringUtils.removeEnd(StringUtils.replace(packageName, ".", "/"), "/"), "/");

                    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
                    MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

                    try {
                        Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packageDirName + "/**/*.class");

                        // 把每一个class文件找出来
                        for (Resource r : resources) {
                            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);

                            // for unknown reason, this class throws exception.
                            if ("com.sunwayworld.cloud.common.core.util.EncodingUtils$BytesEncodingDetect"
                                    .equals(metadataReader.getClassMetadata().getClassName())) {
                                continue;
                            }

                            Class<?> clazz = ClassUtils.getClass(metadataReader.getClassMetadata().getClassName());

                            classList.add(clazz);
                        }
                    } catch (IOException ioe) {
                        throw new UnexpectedException(ioe);
                    }

                    MemoryCacheHelper.put(key, Collections.unmodifiableList(classList));
                }
            }
        }

        return classList;
    }

    @SafeVarargs
    public static final List<Resource> getResourceList(String packageName, Predicate<Resource>... predicates) {
        StringBuilder sb = new StringBuilder("classpath:");
        packageName = StringUtils.replace(packageName, ".", "/");

        if (!packageName.startsWith("/")) {
            sb.append("/");
        }
        sb.append(packageName);
        if (!packageName.endsWith("/")) {
            sb.append("/");
        }

        sb.append("**");

        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(sb.toString());

            return Arrays.stream(resources).filter(r -> r.exists()
                    && r.isReadable()
                    && (ArrayUtils.isEmpty(predicates)
                            || Arrays.stream(predicates).allMatch(p -> p.test(r))))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            throw new ZipException(ioe);
        }
    }

    // -----------------------------------------------------------------------------
    // 私有方法
    // -----------------------------------------------------------------------------
    private static final String getFullyQualifiedName(final String className) {
        if (className.endsWith("[]")) { // 对于Array、Entry等要加以判断
            StringBuffer sb = new StringBuffer();

            String name = className;

            while (name.endsWith("[]")) {
                name = name.substring(0, name.length() - 2);

                sb.append("[");
            }

            final String abbreviation = getClassAbbreviation(name);

            if (StringUtils.isEmpty(abbreviation)) {
                sb.append("L").append(name).append(";");
            } else {
                sb.append(abbreviation);
            }

            return sb.toString();
        }

        return className;
    }

    /**
     * 获取类名的简写，用于从{@link ClassLoader}那里获取对应的{@link Class}
     *
     * @param className 类名
     * @return 类名的简写，如果有的话
     */
    private static final String getClassAbbreviation(final String className) {
        if ("int".equals(className)) {
            return "I";
        } else if ("long".equals(className)) {
            return "J";
        } else if ("double".equals(className)) {
            return "D";
        } else if ("float".equals(className)) {
            return "F";
        } else if ("char".equals(className)) {
            return "C";
        } else if ("short".equals(className)) {
            return "S";
        } else if ("boolean".equals(className)) {
            return "Z";
        } else if ("byte".equals(className)) {
            return "B";
        } else if ("void".equals(className)) {
            return "V";
        }

        return className;
    }
}
