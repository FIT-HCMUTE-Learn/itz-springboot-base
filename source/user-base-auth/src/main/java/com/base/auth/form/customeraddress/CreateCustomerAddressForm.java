package com.base.auth.form.customeraddress;

import com.base.auth.validation.AddressType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Form for create customer address")
public class CreateCustomerAddressForm {
    @ApiModelProperty(name = "Province id", required = true)
    @NotNull(message = "Province id can not be null")
    private Long provinceId;

    @ApiModelProperty(name = "District id", required = true)
    @NotNull(message = "District id can not be null")
    private Long districtId;

    @ApiModelProperty(name = "Commune id", required = true)
    @NotNull(message = "Commune id can not be null")
    private Long communeId;

    @ApiModelProperty(name = "Address", required = true)
    @NotEmpty(message = "Address can not be empty")
    private String address;

    @ApiModelProperty(name = "Type", required = true)
    @AddressType(allowNull = false)
    private Integer type;

    @ApiModelProperty(name = "Is default", required = true)
    @NotNull(message = "Is default can not be null")
    private Boolean isDefault;
}
