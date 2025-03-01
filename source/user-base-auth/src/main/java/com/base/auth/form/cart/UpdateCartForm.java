package com.base.auth.form.cart;

import com.base.auth.form.items.UpdateCartItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel("Form for updating cart")
public class UpdateCartForm {
    @ApiModelProperty(name = "Product list for updating cart", required = true)
    @NotNull(message = "Product list cannot be null")
    @Size(min = 1, message = "At least one product must be provided")
    private List<UpdateCartItem> cartItems;
}
