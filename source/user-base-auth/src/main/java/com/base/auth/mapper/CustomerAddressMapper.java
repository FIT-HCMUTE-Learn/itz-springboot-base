package com.base.auth.mapper;

import com.base.auth.dto.customeraddress.CustomerAddressDto;
import com.base.auth.form.customeraddress.CreateCustomerAddressForm;
import com.base.auth.form.customeraddress.UpdateCustomerAddressForm;
import com.base.auth.model.CustomerAddress;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {NationMapper.class})
public interface CustomerAddressMapper {
    @Mapping(source = "address", target = "address")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isDefault", target = "isDefault")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCustomerAddressCreateFormToEntity")
    CustomerAddress fromCustomerAddressCreateFormToEntity(CreateCustomerAddressForm createCustomerAddressForm);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isDefault", target = "isDefault")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateCustomerAddressForm")
    void updateFromUpdateCustomerAddressForm(@MappingTarget CustomerAddress customerAddress, UpdateCustomerAddressForm updateCustomerAddressForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationDto")
    @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToNationDto")
    @Mapping(source = "commune", target = "commune", qualifiedByName = "fromEntityToNationDto")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isDefault", target = "isDefault")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCustomerAddressDto")
    CustomerAddressDto fromEntityToCustomerAddressDto(CustomerAddress customerAddress);

    @IterableMapping(elementTargetType = CustomerAddressDto.class, qualifiedByName = "fromEntityToCustomerAddressDto")
    @Named("fromEntityToCustomerAddressDtoList")
    List<CustomerAddressDto> fromEntityToCustomerAddressDtoList(List<CustomerAddress> customerAddresses);
}
