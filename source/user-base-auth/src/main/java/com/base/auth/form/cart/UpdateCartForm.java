package com.base.auth.form.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@ApiModel("Form for updating cart")
public class UpdateCartForm {
    @ApiModelProperty(name = "Cart ID", required = true)
    @NotNull(message = "Cart ID cannot be null")
    private Long id;

    @ApiModelProperty(name = "Product quantities", required = true)
    @NotNull(message = "Product quantities cannot be null")
    private Map<Long, @Min(value = 1, message = "Quantity must be at least 1") Integer> productQuantities;
}
