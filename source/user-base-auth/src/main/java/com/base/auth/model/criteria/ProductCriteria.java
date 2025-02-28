package com.base.auth.model.criteria;

import com.base.auth.model.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "Criteria for filtering products")
public class ProductCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Product ID for exact matching", example = "1")
    private Long id;

    @ApiModelProperty(value = "Product name for partial matching", example = "Laptop")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @ApiModelProperty(value = "Short description of the product", example = "Gaming laptop")
    @Size(max = 255, message = "Short description must not exceed 255 characters")
    private String shortDescription;

    @ApiModelProperty(value = "Detailed product description", example = "This is a powerful gaming laptop")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @ApiModelProperty(value = "Minimum price range for filtering", example = "500.0")
    @Min(value = 0, message = "Start price must be greater than or equal to 0")
    private Double startPrice;

    @ApiModelProperty(value = "Maximum price range for filtering", example = "1500.0")
    @Min(value = 0, message = "End price must be greater than or equal to 0")
    private Double endPrice;

    @ApiModelProperty(value = "Minimum discount percentage", example = "10")
    @Min(value = 0, message = "Sale off must be greater than or equal to 0")
    @Max(value = 100, message = "Sale off must not exceed 100%")
    private Integer leastSaleOff;

    public Specification<Product> getSpecification() {
        return new Specification<Product>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if(!StringUtils.isEmpty(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
                }
                if(!StringUtils.isEmpty(getShortDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("shortDescription")), "%" + getShortDescription().toLowerCase() + "%"));
                }
                if(!StringUtils.isEmpty(getDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + getDescription().toLowerCase() + "%"));
                }
                if (getStartPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), getStartPrice()));
                }
                if (getEndPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), getEndPrice()));
                }
                if (getLeastSaleOff() >= 0) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("saleOff"), getLeastSaleOff()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
