package com.yaojiafeng.maven.plugin.sample.exception;


import com.yaojiafeng.maven.plugin.annotations.ExceptionDesc;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 下午2:49
 * Description:
 */
@ExceptionDesc(description = "用户未找到异常")
public class UserNotFoundException extends RuntimeException {
}
