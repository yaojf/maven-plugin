package com.yaojiafeng.maven.plugin.sample.service;

import com.yaojiafeng.maven.plugin.annotations.*;
import com.yaojiafeng.maven.plugin.sample.bean.User;
import com.yaojiafeng.maven.plugin.sample.exception.UserNotFoundException;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 下午2:36
 * Description:
 */
@Api(name = "用户服务接口", description = "提供用户服务")
public interface UserService {

    @ApiMethod(name = "查询用户", description = "根据手机号查询用户")
    @ApiResponse(description = "用户信息")
    @Throws(value = {@Throw(type = UserNotFoundException.class, description = "用户未找到异常")})
    User findByPhone(@ApiParam(name = "phone", description = "手机号") String phone) throws UserNotFoundException;

}
