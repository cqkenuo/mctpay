package com.mctpay.manager.model.param;

import com.mctpay.common.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chenshubiao
 * @Description: 商户参数
 * @Date: 2020-02-28  22:22:01
 */
@Data
public class MerchantParam extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "", hidden = true)
    private String id;
    /**
     * 商户名
     */
    @ApiModelProperty(value = "商户名")
    private String merchantName;
    /**
     * 法人
     */
    @ApiModelProperty(value = "法人")
    private String legalPerson;
    /**
     * 身份证后4位
     */
    @ApiModelProperty(value = "身份证后4位")
    private String idLastNumber;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮编")
    private String postcode;

    /**
     * 注册地址
     */
    @ApiModelProperty(value = "注册地址")
    private String registeredAddress;
    /**
     * 营业地址
     */
    @ApiModelProperty(value = "营业地址")
    private String businessAddress;

    @ApiModelProperty(value = "地理位置经度")
    private String longitude;

    @ApiModelProperty(value = "地理位置纬度")
    private String latitude;

    /**
     * 营业时间
     */
    @ApiModelProperty(value = "营业时间")
    private String businessTime;
    /**
     * 行业
     */
    @ApiModelProperty(value = "行业")
    private String industry;
    /**
     * 数据字典
     */
    @ApiModelProperty(value = "数据字典")
    private String dataDictionary;
    /**
     * 解释内容
     */
    @ApiModelProperty(value = "解释内容")
    private String explainContent;
    /**
     * 经营业务
     */
    @ApiModelProperty(value = "经营业务")
    private String businessContent;

    /**
     * 会员码地址
     */
    @ApiModelProperty(value = "会员码地址", hidden = true)
    private String memberQrcodeUrl;
    /**
     * 门头照
     */
    @ApiModelProperty(value = "门头照")
    private String shopPhoto;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", hidden = true)
    private String creator;
}
