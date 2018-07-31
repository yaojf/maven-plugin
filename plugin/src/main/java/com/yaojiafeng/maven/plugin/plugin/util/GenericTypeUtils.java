package com.yaojiafeng.maven.plugin.plugin.util;

import com.yaojiafeng.maven.plugin.plugin.bean.ApiModelInfo;
import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * User: yaojiafeng
 * Date: 2018/7/24
 * Time: 上午11:15
 * Description:
 */
public class GenericTypeUtils {

    private static void doResolve(String name, URLClassLoader urlClassLoader, Set<ApiModelInfo> apiModelInfoSet) throws ClassNotFoundException {
        Class<?> aClass = null;
        String[] typeParameterArray = new String[0];
        if (name.indexOf(Constants.LT) != -1) {
            aClass = Class.forName(name.substring(0, name.indexOf(Constants.LT)), true, urlClassLoader);
            String typeParameters = name.substring(name.indexOf(Constants.LT) + 1, name.lastIndexOf(Constants.GT));
            // 类型参数
            if (StringUtils.isNotBlank(typeParameters)) {
                typeParameterArray = typeParameters.split(",");
                for (int i = 0; i < typeParameterArray.length; i++) {
                    typeParameterArray[i] = typeParameterArray[i].trim();
                }
            }
        } else {
            aClass = Class.forName(name, true, urlClassLoader);
        }

        if (TypeUtils.isCustom(aClass, urlClassLoader)) {
            ApiModelInfo apiModelInfo = new ApiModelInfo();
            apiModelInfo.setType(name);
            // 防止循环引用
            apiModelInfoSet.add(apiModelInfo);
            Class<?> clazz = aClass;
            while (clazz != null) {
                Field[] declaredFields = clazz.getDeclaredFields();
                if (ArrayUtils.isNotEmpty(declaredFields)) {
                    for (Field field : declaredFields) {
                        if (!Modifier.isStatic(field.getModifiers())) {
                            String fieldName = apiModelInfo.visitField(field, typeParameterArray, aClass.getTypeParameters());
                            if (!TypeUtils.isBaseType(field.getType()) && !apiModelInfoSet.contains(new ApiModelInfo(fieldName, null))) {
                                doResolve(fieldName, urlClassLoader, apiModelInfoSet);
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } else if (TypeUtils.isCollection(aClass) && typeParameterArray.length > 0) {
            for (String typeParameter : typeParameterArray) {
                doResolve(typeParameter, urlClassLoader, apiModelInfoSet);
            }
        } else if (TypeUtils.isMap(aClass) && typeParameterArray.length > 0) {
            for (String typeParameter : typeParameterArray) {
                doResolve(typeParameter, urlClassLoader, apiModelInfoSet);
            }
        } else {
            // jdk其他类型忽略
        }
    }


    public static String resolve(Parameter parameter, URLClassLoader urlClassLoader, Set<ApiModelInfo> modelInfoList) throws ClassNotFoundException {
        String type = null;
        if (TypeUtils.isBaseType(parameter.getType())) {
            type = parameter.getType().getName();
        } else if (TypeUtils.isCustom(parameter.getType(), urlClassLoader)) {
            if (parameter.getParameterizedType() instanceof Class) {
                type = parameter.getType().getName();
                GenericTypeUtils.doResolve(parameter.getType().getName(), urlClassLoader, modelInfoList);
            } else {
                type = parameter.getParameterizedType().toString();
                GenericTypeUtils.doResolve(parameter.getParameterizedType().toString(), urlClassLoader, modelInfoList);
            }
        } else if (TypeUtils.isCollection(parameter.getType()) && !(parameter.getParameterizedType() instanceof Class)) {
            String name = parameter.getParameterizedType().toString();
            type = name;
            GenericTypeUtils.doResolve(name.substring(name.indexOf(Constants.LT) + 1, name.lastIndexOf(Constants.GT)), urlClassLoader, modelInfoList);
        } else if (TypeUtils.isMap(parameter.getType()) && !(parameter.getParameterizedType() instanceof Class)) {
            String name = parameter.getParameterizedType().toString();
            type = name;
            String[] typeParameters = name.substring(name.indexOf(Constants.LT) + 1, name.lastIndexOf(Constants.GT)).split(Constants.COMMA);
            for (String typeParameter : typeParameters) {
                GenericTypeUtils.doResolve(typeParameter.trim(), urlClassLoader, modelInfoList);
            }
        } else {
            type = parameter.getType().getName();
        }
        return type;
    }

    public static String resolve(Method method, URLClassLoader urlClassLoader, Set<ApiModelInfo> modelInfoList) throws ClassNotFoundException {
        String type = null;
        if (TypeUtils.isBaseType(method.getReturnType())) {
            type = method.getReturnType().getName();
        } else if (TypeUtils.isCustom(method.getReturnType(), urlClassLoader)) {
            if (method.getGenericReturnType() instanceof Class) {
                type = method.getReturnType().getName();
                GenericTypeUtils.doResolve(method.getReturnType().getName(), urlClassLoader, modelInfoList);
            } else {
                // 分析的类型即使有范型，也解析为Object
                type = method.getGenericReturnType().toString();
                GenericTypeUtils.doResolve(method.getGenericReturnType().toString(), urlClassLoader, modelInfoList);
            }
        } else if (TypeUtils.isCollection(method.getReturnType()) && !(method.getGenericReturnType() instanceof Class)) {
            String name = method.getGenericReturnType().toString();
            type = name;
            GenericTypeUtils.doResolve(name.substring(name.indexOf(Constants.LT) + 1, name.lastIndexOf(Constants.GT)), urlClassLoader, modelInfoList);
        } else if (TypeUtils.isMap(method.getReturnType()) && !(method.getGenericReturnType() instanceof Class)) {
            String name = method.getGenericReturnType().toString();
            type = name;
            String[] typeParameters = name.substring(name.indexOf(Constants.LT) + 1, name.lastIndexOf(Constants.GT)).split(Constants.COMMA);
            for (String typeParameter : typeParameters) {
                GenericTypeUtils.doResolve(typeParameter.trim(), urlClassLoader, modelInfoList);
            }
        } else {
            type = method.getReturnType().getName();
        }
        return type;
    }
}
