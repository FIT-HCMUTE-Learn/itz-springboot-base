package com.base.auth.mapper;

import com.base.auth.dto.order.OrderDto;
import com.base.auth.model.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface OrderMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "totalSaleOff", target = "totalSaleOff")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "state", target = "state")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToOrderDto")
    OrderDto fromEntityToOrderDto(Order order);

    @IterableMapping(elementTargetType = OrderDto.class, qualifiedByName = "fromEntityToOrderDto")
    List<OrderDto> fromEntityToOrderDtoList(List<Order> orders);
}
