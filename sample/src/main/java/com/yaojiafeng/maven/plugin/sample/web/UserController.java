package com.yaojiafeng.maven.plugin.sample.web;

import com.yaojiafeng.maven.plugin.sample.bean.Address;
import com.yaojiafeng.maven.plugin.sample.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * User: yaojiafeng
 * Date: 2018/6/8
 * Time: 上午11:04
 * Description:
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户接口", description = "用户接口")
public class UserController {

    @ApiOperation(value = "查询用户信息")
    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Long id, @ModelAttribute Address address) {
        return new User();
    }

}
