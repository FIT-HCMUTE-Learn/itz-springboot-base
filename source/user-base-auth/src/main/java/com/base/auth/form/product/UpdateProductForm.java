package com.base.auth.form.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel(description = "Form for update product")
public class UpdateProductForm {
    @ApiModelProperty(name = "Id", required = true)
    @NotNull(message = "Id can not be null")
    private Long id;

    @ApiModelProperty(name = "Name", required = false)
    private String name;

    @ApiModelProperty(name = "Short description", required = false)
    @Size(max = 255, message = "Short description must not exceed 255 characters")
    private String shortDescription;

    @ApiModelProperty(name = "Description", required = false)
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @ApiModelProperty(name = "Price", required = false)
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    @ApiModelProperty(name = "Sale off", required = false)
    @Min(value = 0, message = "Sale off must be greater than or equal to 0")
    @Max(value = 100, message = "Sale off must not exceed 100%")
    private Integer saleOff;

    @ApiModelProperty(name = "Image", required = false)
    private String image;
}
