package com.base.auth.form.customeraddress;

import com.base.auth.validation.AddressType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Form for update customer address")
public class UpdateCustomerAddressForm {
    @ApiModelProperty(name = "Id", required = true)
    @NotNull(message = "Id can not be null")
    private Long id;

    @ApiModelProperty(name = "Province id", required = false)
    @NotNull(message = "Province id can not be null")
    private Long provinceId;

    @ApiModelProperty(name = "District id", required = false)
    @NotNull(message = "District id can not be null")
    private Long districtId;

    @ApiModelProperty(name = "Commune id", required = false)
    @NotNull(message = "Commune id can not be null")
    private Long communeId;

    @ApiModelProperty(name = "Address", required = false)
    @NotEmpty(message = "Address can not be empty")
    private String address;

    @ApiModelProperty(name = "Type", required = false)
    @AddressType(allowNull = true)
    private Integer type;

    @ApiModelProperty(name = "Is default", required = true)
    @NotNull(message = "Is default can not be null")
    private Boolean isDefault;
}
