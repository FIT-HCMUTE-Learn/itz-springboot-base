package com.base.auth.repository;

import com.base.auth.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long>, JpaSpecificationExecutor<CustomerAddress> {
    Long countByCustomerId(Long customerId);

    @Modifying
    @Query("UPDATE CustomerAddress SET isDefault = false WHERE customer.id = :customerId AND isDefault = true")
    void reverseIsDefaultByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(ca) FROM CustomerAddress ca " +
            "WHERE ca.province.id = :nationId OR ca.district.id = :nationId OR ca.commune.id = :nationId")
    Long countCustomerAddressByNationId(@Param("nationId") Long nationId);
}
