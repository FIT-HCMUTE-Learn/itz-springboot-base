package com.base.auth.form.nation;

import com.base.auth.validation.NationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateNationForm {
    @ApiModelProperty(name = "id", required = true)
    @NotNull(message = "id can not be null")
    private Long id;

    @ApiModelProperty(name = "name", required = false)
    private String name;

    @ApiModelProperty(name = "description", required = false)
    private String description;

    @ApiModelProperty(name = "type", required = false)
    @NationType(allowNull = true)
    private Integer type;
}
