package com.base.auth.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Form for cancel order")
public class CancelOrderForm {
    @ApiModelProperty(name = "Id", required = true)
    @NotNull(message = "Id can not be null")
    private Long id;
}
