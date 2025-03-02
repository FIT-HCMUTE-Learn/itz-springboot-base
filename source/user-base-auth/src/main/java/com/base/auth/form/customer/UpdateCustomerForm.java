package com.base.auth.form.customer;

import com.base.auth.validation.CustomerGender;
import com.base.auth.validation.Phone;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class UpdateCustomerForm {
    @ApiModelProperty(name = "id", required = true)
    @NotNull(message = "id can not be null")
    private Long id;

    @ApiModelProperty(name = "username", required = false)
    private String username;

    @ApiModelProperty(name = "password", required = false)
    private String password;

    @ApiModelProperty(name = "phone")
    @Phone(allowNull = true)
    private String phone;

    @ApiModelProperty(name = "email")
    @Email
    private String email;

    @ApiModelProperty(name = "full name", required = false)
    private String fullName;

    @ApiModelProperty(name = "status", required = false)
    private Integer status;

    @ApiModelProperty(name = "avatar path")
    private String avatarPath;

    @ApiModelProperty(name = "birthday", required = false)
    @Past(message = "birthday must be in the past")
    private Date birthday;

    @ApiModelProperty(name = "gender", required = false)
    @CustomerGender(allowNull = true)
    private Integer gender;
}
