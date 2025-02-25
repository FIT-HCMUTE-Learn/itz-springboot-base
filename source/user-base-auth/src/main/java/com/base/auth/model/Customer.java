package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_user_base_customer")
@Getter
@Setter
public class Customer {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    private Date birthday;

    private Integer gender;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id", referencedColumnName = "id", nullable = false)
    private Nation province;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id", referencedColumnName = "id", nullable = false)
    private Nation district;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commune_id", referencedColumnName = "id", nullable = false)
    private Nation commune;
}
