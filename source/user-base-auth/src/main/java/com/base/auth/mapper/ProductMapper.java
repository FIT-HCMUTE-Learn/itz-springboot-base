package com.base.auth.mapper;

import com.base.auth.dto.product.ProductDto;
import com.base.auth.form.product.CreateProductForm;
import com.base.auth.form.product.UpdateProductForm;
import com.base.auth.model.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface ProductMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description", qualifiedByName = "setDefaultDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff", qualifiedByName = "setDefaultSaleOff")
    @Mapping(source = "image", target = "image")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateProductFormToEntity")
    Product fromCreateProductFormToEntity(CreateProductForm createProductForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description", qualifiedByName = "setDefaultDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff", qualifiedByName = "setDefaultSaleOff")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateProductForm")
    void updateFromUpdateProductForm(@MappingTarget Product product, UpdateProductForm updateProductForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "image", target = "image")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToProductDto")
    ProductDto fromEntityToProductDto(Product product);

    @IterableMapping(elementTargetType = ProductDto.class, qualifiedByName = "fromEntityToProductDto")
    List<ProductDto> fromEntityToProductDtoList(List<Product> products);

    @Named("setDefaultDescription")
    default String setDefaultDescription(String description) {
        return (description == null) ? "No description available" : description;
    }

    @Named("setDefaultSaleOff")
    default Integer setDefaultSaleOff(Integer saleOff) {
        return (saleOff == null) ? 0 : saleOff;
    }
}
