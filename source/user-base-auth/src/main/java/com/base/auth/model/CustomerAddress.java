package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "db_user_base_customer_address")
@Getter
@Setter
public class CustomerAddress extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "id", nullable = false)
    private Nation province;

    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id", nullable = false)
    private Nation district;

    @ManyToOne
    @JoinColumn(name = "commune_id", referencedColumnName = "id", nullable = false)
    private Nation commune;

    private String address;

    private Integer type;

    private Boolean isDefault;
}
