package com.base.auth.mapper;

import com.base.auth.dto.customer.CustomerDto;
import com.base.auth.form.customer.CreateCustomerForm;
import com.base.auth.form.customer.UpdateCustomerForm;
import com.base.auth.model.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class, NationMapper.class})
public interface CustomerMapper {
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "address", target = "address")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateCustomerFormToEntity")
    Customer fromCreateCustomerFormToEntity(CreateCustomerForm createCustomerForm);

    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "address", target = "address")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateCustomerForm")
    void updateFromUpdateCustomerForm(@MappingTarget Customer customer, UpdateCustomerForm updateCustomerForm);

    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToNationDto")
    @Mapping(source = "commune", target = "commune", qualifiedByName = "fromEntityToNationDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCustomerDto")
    CustomerDto fromEntityToCustomerDto(Customer customer);

    @IterableMapping(elementTargetType = CustomerDto.class, qualifiedByName = "fromEntityToCustomerDto")
    List<CustomerDto> fromEntityToCustomerDtoList(List<Customer> customers);
}
