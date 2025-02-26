package com.base.auth.repository;

import com.base.auth.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByProductId(Long id);
    void deleteByCartId(Long id);

    // Delete CartItem not in the new productIds
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id NOT IN :productIds")
    void deleteCartItemsNotIn(@Param("cartId") Long cartId, @Param("productIds") List<Long> productIds);

    // Check Product in Cart
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    boolean existsByCartAndProduct(@Param("cartId") Long cartId, @Param("productId") Long productId);

    // Increment follow new quantity
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = ci.quantity + :quantity WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    void incrementQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);
}
