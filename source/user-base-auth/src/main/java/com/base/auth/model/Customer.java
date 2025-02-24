package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_user_base_customer")
@Getter
@Setter
public class Customer {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    private Date birthday;

    private Integer gender;

    private String address;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id", referencedColumnName = "id", nullable = false)
    private Nation province;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id", referencedColumnName = "id", nullable = false)
    private Nation district;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commune_id", referencedColumnName = "id", nullable = false)
    private Nation commune;
}
