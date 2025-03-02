package com.base.auth.form.customer;

import com.base.auth.validation.CustomerGender;
import com.base.auth.validation.Phone;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class CreateCustomerForm {
    @ApiModelProperty(name = "username", required = true)
    @NotEmpty(message = "username cant not be null")
    private String username;

    @ApiModelProperty(name = "password", required = true)
    @NotEmpty(message = "password cant not be null")
    private String password;

    @ApiModelProperty(name = "phone")
    @Phone
    private String phone;

    @ApiModelProperty(name = "email")
    @Email(message = "Invalid email format")
    private String email;

    @ApiModelProperty(name = "full name", required = true)
    @NotEmpty(message = "full name cant not be null")
    private String fullName;

    @ApiModelProperty(name = "status", required = true)
    @NotNull(message = "status cant not be null")
    private Integer status;

    @ApiModelProperty(name = "avatar path")
    private String avatarPath;

    @ApiModelProperty(name = "birthday", required = true)
    @NotNull(message = "birthday can not be null")
    @Past(message = "birthday must be in the past")
    private Date birthday;

    @ApiModelProperty(name = "gender", required = true)
    @CustomerGender(allowNull = false)
    private Integer gender;
}
