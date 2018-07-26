package com.yaojiafeng.maven.plugin.annotations;

import java.lang.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 下午2:45
 * Description: 声明一个异常
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Throw {

    Class<? extends Throwable> type();// 异常类型

    String description();// 异常描述
}
