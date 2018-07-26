package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/4/28
 * Time: 上午11:51
 * Description: 单个API入参描述
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ApiParam {
    String name(); // 参数名称

    String description() default "";// 参数描述

    boolean required() default true;// 参数是否必传

}
