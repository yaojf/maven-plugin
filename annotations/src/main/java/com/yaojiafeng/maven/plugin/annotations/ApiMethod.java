package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/4/28
 * Time: 上午11:42
 * Description: 描述一个接口方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiMethod {
    String name() default ""; // 方法名称

    String description() default "";// 方法描述

}
