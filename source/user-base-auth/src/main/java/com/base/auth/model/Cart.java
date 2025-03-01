package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "db_user_base_cart")
@Getter
@Setter
public class Cart extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Column(unique = true, length = 6, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;
}
