package com.base.auth.model.criteria;

import com.base.auth.model.CustomerAddress;
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
public class CustomerAddressCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long customerId;
    private Long provinceId;
    private Long districtId;
    private Long communeId;
    private String address;
    private Integer type;
    private Boolean isDefault;

    public Specification<CustomerAddress> getSpecification() {
        return new Specification<CustomerAddress>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CustomerAddress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getProvinceId() != null) {
                    predicates.add(cb.equal(root.get("province").get("id"), getProvinceId()));
                }
                if (getDistrictId() != null) {
                    predicates.add(cb.equal(root.get("district").get("id"), getDistrictId()));
                }
                if (getCommuneId() != null) {
                    predicates.add(cb.equal(root.get("commune").get("id"), getCommuneId()));
                }
                if (!StringUtils.isEmpty(getAddress())) {
                    predicates.add(cb.like(cb.lower(root.get("address")), "%" + getAddress().toLowerCase() + "%"));
                }
                if (getType() != null) {
                    predicates.add(cb.equal(root.get("type"), getType()));
                }
                if (getIsDefault() != null) {
                    predicates.add(cb.equal(root.get("isDefault"), getIsDefault()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
