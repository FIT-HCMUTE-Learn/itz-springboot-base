package com.base.auth.controller;

import com.base.auth.form.items.UpdateCartItem;
import com.base.auth.dto.ApiMessageDto;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/cart")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CartController extends ABasicController{
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
        Long accountId = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(accountId);
        List<CartItem> cartItems = cart.getCartItems();

        // Convert list from form to Map<ProductId, Quantity>
        Map<Long, Integer> updatedProducts = updateCartForm.getCartItems().stream()
                .collect(Collectors.toMap(UpdateCartItem::getProductId, UpdateCartItem::getQuantity));

        // Iterate over cartItems and remove items not in the updated list, update existing ones
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem cartItem = iterator.next();
            Long productId = cartItem.getProduct().getId();
            if (updatedProducts.containsKey(productId)) {
                cartItem.setQuantity(cartItem.getQuantity() + updatedProducts.get(productId));
                updatedProducts.remove(productId);
            } else {
                iterator.remove();
            }
        }

        // Add new products that were not present in the original cart
        if (!updatedProducts.isEmpty()) {
            List<Product> newProducts = productRepository.findAllById(updatedProducts.keySet());
            for (Product product : newProducts) {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(updatedProducts.get(product.getId()));
                cartItems.add(newCartItem);
            }
        }

        cartRepository.save(cart);
        apiMessageDto.setMessage("Cart updated successfully");
        return apiMessageDto;
    }
}
