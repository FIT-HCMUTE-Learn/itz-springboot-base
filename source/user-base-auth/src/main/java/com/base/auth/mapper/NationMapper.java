package com.base.auth.mapper;

import com.base.auth.dto.nation.NationDto;
import com.base.auth.form.nation.CreateNationForm;
import com.base.auth.form.nation.UpdateNationForm;
import com.base.auth.form.news.UpdateNewsForm;
import com.base.auth.model.Nation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface NationMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateNationFormToEntity")
    Nation fromCreateNationFormToEntity(CreateNationForm createNationForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateNationForm")
    void updateFromUpdateNationForm(@MappingTarget Nation nation, UpdateNationForm updateNationForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNationDto")
    NationDto fromEntityToNationDto(Nation nation);

    @IterableMapping(elementTargetType = NationDto.class, qualifiedByName = "fromEntityToNationDto")
    List<NationDto> fromEntityToNationDtoList(List<Nation> nations);
}
