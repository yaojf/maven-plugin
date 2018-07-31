package com.yaojiafeng.maven.plugin.plugin.bean;

import com.yaojiafeng.maven.plugin.plugin.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User: yaojiafeng
 * Date: 2018/7/24
 * Time: 上午11:31
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiModelInfo {

    private String type;// 类名,带范型信息

    private List<FieldInfo> fieldInfoList;// 字段信息

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiModelInfo that = (ApiModelInfo) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public String visitField(Field field, String[] typeParameterArray, TypeVariable[] typeVariables) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setName(field.getName());
        String type = null;
        if (field.getGenericType() instanceof Class) {
            type = field.getType().getName();
        } else if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            StringBuilder sb = new StringBuilder(field.getType().getName());
            sb.append(Constants.LT);
            for (Type actualTypeArgument : actualTypeArguments) {
                if (actualTypeArgument instanceof TypeVariable) {
                    String name = ((TypeVariable) actualTypeArgument).getName();
                    for (int i = 0; i < typeVariables.length; i++) {
                        if (name.equals(typeVariables[i].getName())) {
                            if (i < typeParameterArray.length) {
                                sb.append(typeParameterArray[i]).append(Constants.COMMA);
                            } else {
                                sb.append(Object.class.getName()).append(Constants.COMMA);
                            }
                        }
                    }
                } else if (actualTypeArgument instanceof Class) {
                    sb.append(((Class) actualTypeArgument).getName()).append(Constants.COMMA);
                } else {
                    sb.append(((ParameterizedType) actualTypeArgument).toString()).append(Constants.COMMA);
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(Constants.GT);
            type = sb.toString();
        } else {
            TypeVariable genericType = (TypeVariable) field.getGenericType();
            String name = genericType.getName();
            for (int i = 0; i < typeVariables.length; i++) {
                if (name.equals(typeVariables[i].getName())) {
                    if (i < typeParameterArray.length) {
                        type = typeParameterArray[i];
                    } else {
                        type = Object.class.getName();
                    }
                }
            }
        }
        fieldInfo.setType(type);
        if (fieldInfoList == null) {
            fieldInfoList = new ArrayList<>();
        }
        fieldInfoList.add(fieldInfo);
        return type;
    }
}
