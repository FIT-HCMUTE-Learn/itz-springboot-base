package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "db_user_base_customer")
@Getter
@Setter
public class Customer extends Auditable<String> {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false)
    private Account account;

    private Date birthday;

    private Integer gender;

    @OneToMany(mappedBy = "customer")
    private List<CustomerAddress> customerAddresses;
}
