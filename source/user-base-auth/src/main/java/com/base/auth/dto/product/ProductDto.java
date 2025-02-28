package com.base.auth.dto.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductDto {
    @ApiModelProperty(name = "Id")
    private Long id;
    @ApiModelProperty(name = "Name")
    private String name;
    @ApiModelProperty(name = "Short description")
    private String shortDescription;
    @ApiModelProperty(name = "Description")
    private String description;
    @ApiModelProperty(name = "Price")
    private Double price;
    @ApiModelProperty(name = "Sale off")
    private Integer saleOff;
    @ApiModelProperty(name = "Image")
    private String image;
}
