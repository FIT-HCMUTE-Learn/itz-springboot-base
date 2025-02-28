package com.base.auth.model.criteria;

import com.base.auth.model.Order;
import com.base.auth.validation.OrderState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "Criteria for filtering orders")
public class OrderCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Order ID for exact matching", example = "1")
    private Long id;

    @ApiModelProperty(value = "Order code for partial matching", example = "ABC123")
    private String code;

    @ApiModelProperty(value = "Customer ID for filtering", example = "1001")
    private Long customerId;

    @ApiModelProperty(value = "Order state filter", example = "1")
    @OrderState(allowNull = true)
    private Integer state;

    public Specification<Order> getSpecification() {
        return new Specification<Order>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if(!StringUtils.isEmpty(getCode())) {
                    predicates.add(cb.like(cb.lower(root.get("code")), "%" + getCode() + "%"));
                }
                if (getCustomerId() != null) {
                    predicates.add(cb.equal(root.get("customer").get("id"), getCustomerId()));
                }
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
