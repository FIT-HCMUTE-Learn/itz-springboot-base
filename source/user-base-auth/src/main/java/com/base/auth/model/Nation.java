package com.base.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "db_user_base_nation")
@Getter
@Setter
public class Nation extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private Integer type;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Nation parent;
}
