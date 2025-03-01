package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "db_user_base_product")
@Getter
@Setter
public class Product extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    private String shortDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private Double price;

    private Integer saleOff;

    private String image;
}
