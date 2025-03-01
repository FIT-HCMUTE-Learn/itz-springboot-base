package com.base.auth.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Form for create order")
public class CreateOrderForm {
    @ApiModelProperty(name = "Customer address Id", required = true)
    @NotNull(message = "Customer address Id can not be null")
    private Long customerAddressId;
}
