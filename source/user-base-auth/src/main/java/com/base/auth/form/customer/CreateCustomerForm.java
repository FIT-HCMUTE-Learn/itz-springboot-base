package com.base.auth.form.customer;

import com.base.auth.validation.CustomerGender;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class CreateCustomerForm {
    @ApiModelProperty(name = "account id", required = true)
    @NotNull(message = "account id can not be null")
    private Long accountId;

    @ApiModelProperty(name = "birthday", required = true)
    @NotNull(message = "birthday can not be null")
    @Past(message = "birthday must be in the past")
    private Date birthday;

    @ApiModelProperty(name = "gender", required = true)
    @CustomerGender(allowNull = false)
    private Integer gender;

    @ApiModelProperty(name = "address", required = true)
    @NotEmpty(message = "address can not be empty")
    private String address;

    @ApiModelProperty(name = "province id", required = true)
    @NotNull(message = "province id can not be null")
    private Long provinceId;

    @ApiModelProperty(name = "district id", required = true)
    @NotNull(message = "district id can not be null")
    private Long districtId;

    @ApiModelProperty(name = "commune id", required = true)
    @NotNull(message = "commune id can not be null")
    private Long communeId;
}
