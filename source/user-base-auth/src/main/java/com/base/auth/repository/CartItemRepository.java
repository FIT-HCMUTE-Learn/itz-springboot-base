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
    List<CartItem> findByCartId(Long id);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id NOT IN :productIds")
    void deleteCartItemsNotIn(@Param("cartId") Long cartId, @Param("productIds") List<Long> productIds);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id IN :productIds")
    List<CartItem> findByCartIdAndProductIds(@Param("cartId") Long cartId, @Param("productIds") List<Long> productIds);

    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = ci.quantity + :quantity WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    void updateQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);
}
