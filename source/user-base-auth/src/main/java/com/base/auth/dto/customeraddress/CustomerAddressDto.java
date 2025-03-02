package com.base.auth.dto.customeraddress;

import com.base.auth.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerAddressDto {
    @ApiModelProperty(name = "Id")
    private Long id;
    @ApiModelProperty(name = "Customer id")
    private Long customerId;
    @ApiModelProperty(name = "Province")
    private NationDto province;
    @ApiModelProperty(name = "District")
    private NationDto district;
    @ApiModelProperty(name = "Commune")
    private NationDto commune;
    @ApiModelProperty(name = "Address")
    private String address;
    @ApiModelProperty(name = "Type")
    private Integer type;
    @ApiModelProperty(name = "Is default")
    private Boolean isDefault;
}
