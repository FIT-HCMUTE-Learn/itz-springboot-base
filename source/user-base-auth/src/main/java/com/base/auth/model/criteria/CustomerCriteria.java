package com.base.auth.model.criteria;

import com.base.auth.model.Customer;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CustomerCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long accountId;
    private Date startBirthday;
    private Date endBirthday;
    private Integer gender;

    public Specification<Customer> getSpecification() {
        return new Specification<Customer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getAccountId() != null) {
                    predicates.add(cb.equal(root.get("account").get("id"), getAccountId()));
                }
                if (getStartBirthday() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("birthday"), getStartBirthday()));
                }
                if (getEndBirthday() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("birthday"), getEndBirthday()));
                }
                if (getGender() != null) {
                    predicates.add(cb.equal(root.get("gender"), getAccountId()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
