package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.form.cart.UpdateCartForm;
import com.base.auth.model.Cart;
import com.base.auth.model.CartItem;
import com.base.auth.model.Product;
import com.base.auth.repository.CartItemRepository;
import com.base.auth.repository.CartRepository;
import com.base.auth.repository.ProductRepository;
import com.base.auth.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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

        Long accountId = SecurityUtils.getAccountId();
        Cart cart = cartRepository.findByCustomerId(accountId);

        Map<Long, Integer> productQuantities = updateCartForm.getProductQuantities();
        List<Long> productIds = new ArrayList<>(productQuantities.keySet());

        // Get list CartItem already in Cart
        List<CartItem> existingCartItems = cartItemRepository.findByCartIdAndProductIds(cart.getId(), productIds);
        Map<Long, CartItem> existingCartItemMap = existingCartItems.stream()
                .collect(Collectors.toMap(ci -> ci.getProduct().getId(), ci -> ci));

        // Get list Product before go to loop
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItem> newCartItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (existingCartItemMap.containsKey(productId)) {
                cartItemRepository.updateQuantity(cart.getId(), productId, quantity);
            } else {
                Product product = productMap.get(productId);
                if (product != null) {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setCart(cart);
                    newCartItem.setProduct(product);
                    newCartItem.setQuantity(quantity);
                    newCartItems.add(newCartItem);
                }
            }
        }
        if (!newCartItems.isEmpty()) {
            cartItemRepository.saveAll(newCartItems);
        }
        cartItemRepository.deleteCartItemsNotIn(cart.getId(), productIds);

        apiMessageDto.setMessage("Cart updated successfully");
        return apiMessageDto;
    }
}
