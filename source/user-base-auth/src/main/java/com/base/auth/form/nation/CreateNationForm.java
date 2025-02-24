package com.base.auth.form.nation;

import com.base.auth.validation.NationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateNationForm{
    @NotEmpty(message = "name cant not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @ApiModelProperty(name = "description", required = false)
    private String description;

    @ApiModelProperty(name = "type", required = true)
    @NationType(allowNull = false)
    private Integer type;
}
