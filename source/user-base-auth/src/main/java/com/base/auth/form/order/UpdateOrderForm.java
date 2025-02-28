package com.base.auth.form.order;

import com.base.auth.validation.OrderState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Form for update order")
public class UpdateOrderForm {
    @ApiModelProperty(name = "Id", required = true)
    @NotNull(message = "Id can not be null")
    private Long id;

    @ApiModelProperty(name = "State", required = true)
    @OrderState
    private Integer state;
}
