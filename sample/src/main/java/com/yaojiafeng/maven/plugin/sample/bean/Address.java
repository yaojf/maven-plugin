package com.yaojiafeng.maven.plugin.sample.bean;


import com.yaojiafeng.maven.plugin.annotations.ApiModel;
import com.yaojiafeng.maven.plugin.annotations.ApiModelProperty;

/**
 * User: yaojiafeng
 * Date: 2018/5/17
 * Time: 下午5:10
 * Description:
 */
@ApiModel(description = "地址")
@io.swagger.annotations.ApiModel("地址")
public class Address {

    @ApiModelProperty(description = "城市")
    @io.swagger.annotations.ApiModelProperty("城市")
    private String city;

}
