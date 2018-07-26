package com.yaojiafeng.maven.plugin.plugin.enums;

/**
 * User: yaojiafeng
 * Date: 2018/6/14
 * Time: 上午11:57
 * Description:
 */
public enum HttpApiParamInEnum {

    BODY("body"), QUERY("query"), PATH("path");

    private final String value;

    HttpApiParamInEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
