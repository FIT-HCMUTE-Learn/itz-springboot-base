package com.base.auth.dto.customer;

import com.base.auth.dto.account.AccountDto;
import com.base.auth.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerDto {
    @ApiModelProperty(name = "id")
    private String id;
    @ApiModelProperty(name = "account")
    private AccountDto account;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "gender")
    private Integer gender;
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "province")
    private NationDto province;
    @ApiModelProperty(name = "district")
    private NationDto district;
    @ApiModelProperty(name = "commune")
    private NationDto commune;
}
