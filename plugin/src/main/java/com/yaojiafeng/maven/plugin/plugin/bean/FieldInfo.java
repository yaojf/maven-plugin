package com.yaojiafeng.maven.plugin.plugin.bean;

import lombok.Data;

/**
 * User: yaojiafeng
 * Date: 2018/7/24
 * Time: 上午11:27
 * Description:
 */
@Data
public class FieldInfo {

    private String name; // 字段名称

    private String type;// 字段类型，带范型信息

}
