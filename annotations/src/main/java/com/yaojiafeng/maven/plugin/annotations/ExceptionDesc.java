package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/5/11
 * Time: 上午9:13
 * Description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExceptionDesc {

    String description();// 异常描述

}
