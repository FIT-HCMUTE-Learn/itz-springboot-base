package com.base.auth.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderDto {
    @ApiModelProperty(name = "Id")
    private Long id;
    @ApiModelProperty(name = "Code")
    private String code;
    @ApiModelProperty(name = "Total money")
    private Double totalMoney;
    @ApiModelProperty(name = "Total sale off")
    private Double totalSaleOff;
    @ApiModelProperty(name = "Customer id")
    private Long customerId;
    @ApiModelProperty(name = "State")
    private Integer state;
}
