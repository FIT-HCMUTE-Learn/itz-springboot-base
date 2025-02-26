package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "db_user_base_cart_item")
@Getter
@Setter
public class CartItem {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;
}
