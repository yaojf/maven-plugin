package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/4/28
 * Time: 上午11:39
 * Description: 描述这是一个API接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Api {

    String name() default ""; // 接口名称

    String description() default ""; // 接口描述

    String version() default "1.0.0";// 接口版本

    String group() default "";// 接口分组
}
