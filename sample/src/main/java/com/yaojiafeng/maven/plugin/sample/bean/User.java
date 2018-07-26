package com.yaojiafeng.maven.plugin.sample.bean;


import com.yaojiafeng.maven.plugin.annotations.ApiModel;
import com.yaojiafeng.maven.plugin.annotations.ApiModelProperty;

/**
 * User: yaojiafeng
 * Date: 2018/5/9
 * Time: 下午2:36
 * Description:
 */
@ApiModel(description = "用户")
@io.swagger.annotations.ApiModel("用户")
public class User {

    @ApiModelProperty(description = "姓名")
    @io.swagger.annotations.ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty(description = "电话")
    @io.swagger.annotations.ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty(description = "地址")
    @io.swagger.annotations.ApiModelProperty("地址")
    private Address address;
}
