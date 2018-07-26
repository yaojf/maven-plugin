package com.yaojiafeng.maven.plugin.plugin.util;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * User: yaojiafeng
 * Date: 2018/5/16
 * Time: 下午5:57
 * Description:
 */
public class TypeUtils {

    /**
     * 基础数据类型以及封装类
     */
    private static final String[] JAVA_BASE_TYPE = {"byte", "java.lang.Byte", "short", "java.lang.Short", "int", "java.lang.Integer",
            "long", "java.lang.Long", "double", "java.lang.Double", "float", "java.lang.Float",
            "boolean", "java.lang.Boolean", "char", "java.lang.Character", "java.lang.String", "void", "java.lang.Void", "java.util.Date"};

    /**
     * 基础引用类型
     */
    public static final List<String> BASE_TYPE_LIST = Arrays.asList(JAVA_BASE_TYPE);

    public static boolean isBaseType(Class<?> classType) {
        return BASE_TYPE_LIST.contains(classType.getName()) || classType.isEnum();
    }

    public static boolean isArray(Class<?> classType) {
        return classType.getName().startsWith("[");
    }

    public static boolean isCollection(Class<?> clazz) {
        return java.util.Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isMap(Class<?> classType) {
        return java.util.Map.class.isAssignableFrom(classType);
    }

    public static boolean isCustom(Class<?> classType, URLClassLoader urlClassLoader) {
        return classType.getClassLoader() == urlClassLoader || classType.getClassLoader() == urlClassLoader.getParent();
    }
}
