package com.base.auth.form.items;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("Cart item update request")
public class UpdateCartItem {
    @ApiModelProperty(value = "Product ID", required = true)
    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @ApiModelProperty(value = "Quantity", required = true)
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
