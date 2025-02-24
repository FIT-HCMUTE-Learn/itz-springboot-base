package com.base.auth.model.criteria;

import com.base.auth.model.News;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewsCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String content;
    private String description;
    private Long categoryId;

    public Specification<News> getSpecification() {
        return new Specification<News>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if(!StringUtils.isEmpty(getTitle())) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + getTitle().toLowerCase() + "%"));
                }
                if(!StringUtils.isEmpty(getContent())) {
                    predicates.add(cb.like(cb.lower(root.get("content")), "%" + getContent().toLowerCase() + "%"));
                }
                if(!StringUtils.isEmpty(getDescription())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + getDescription().toLowerCase() + "%"));
                }
                if (getCategoryId() != null) {
                    predicates.add(cb.equal(root.get("category").get("id"), getCategoryId()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
