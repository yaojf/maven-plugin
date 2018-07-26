package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/4/28
 * Time: 上午11:55
 * Description: 返回值描述
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiResponse {
    String description() default "";// 返回值描述

    boolean allowNullValue() default false;// 是否允许null

}
