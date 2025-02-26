package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.form.cart.UpdateCartForm;
import com.base.auth.model.Cart;
import com.base.auth.model.CartItem;
import com.base.auth.model.Product;
import com.base.auth.repository.CartItemRepository;
import com.base.auth.repository.CartRepository;
import com.base.auth.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/v1/cart")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CartController {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAR_U')")
    @Transactional
    public ApiMessageDto<String> updateCart(@Valid @RequestBody UpdateCartForm updateCartForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Cart cart = cartRepository.findById(updateCartForm.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Map<Long, Integer> productQuantities = updateCartForm.getProductQuantities();
        List<Long> productIds = new ArrayList<>(productQuantities.keySet());

        // Delete Product not in the new list
        cartItemRepository.deleteCartItemsNotIn(cart.getId(), productIds);
        // Update Cart
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (cartItemRepository.existsByCartAndProduct(cart.getId(), productId)) {
                cartItemRepository.incrementQuantity(cart.getId(), productId, quantity);
            } else {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
            }
        }

        apiMessageDto.setMessage("Cart updated successfully");
        return apiMessageDto;
    }
}
