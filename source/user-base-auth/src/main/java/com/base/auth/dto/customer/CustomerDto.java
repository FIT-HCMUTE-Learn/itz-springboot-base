package com.base.auth.dto.customer;

import com.base.auth.dto.account.AccountDto;
import com.base.auth.dto.customeraddress.CustomerAddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CustomerDto {
    @ApiModelProperty(name = "Id")
    private Long id;
    @ApiModelProperty(name = "Account")
    private AccountDto account;
    @ApiModelProperty(name = "Birthday")
    private Date birthday;
    @ApiModelProperty(name = "Gender")
    private Integer gender;
    @ApiModelProperty(name = "Address list")
    private List<CustomerAddressDto> customerAddresses;
}
