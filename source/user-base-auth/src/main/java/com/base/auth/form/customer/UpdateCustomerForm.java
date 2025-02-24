package com.base.auth.form.customer;

import com.base.auth.validation.CustomerGender;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class UpdateCustomerForm {
    @ApiModelProperty(name = "id", required = true)
    @NotNull(message = "id can not be null")
    private Long id;

    @ApiModelProperty(name = "account id", required = false)
    private Long accountId;

    @ApiModelProperty(name = "birthday", required = false)
    @Past(message = "birthday must be in the past")
    private Date birthday;

    @ApiModelProperty(name = "gender", required = false)
    @CustomerGender(allowNull = true)
    private Integer gender;

    @ApiModelProperty(name = "address", required = false)
    private String address;

    @ApiModelProperty(name = "province id", required = false)
    private Long provinceId;

    @ApiModelProperty(name = "district id", required = false)
    private Long districtId;

    @ApiModelProperty(name = "commune id", required = false)
    private Long communeId;
}
