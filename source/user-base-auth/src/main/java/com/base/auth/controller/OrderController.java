package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.order.OrderDto;
import com.base.auth.form.order.UpdateOrderForm;
import com.base.auth.mapper.OrderMapper;
import com.base.auth.model.Cart;
import com.base.auth.model.CartItem;
import com.base.auth.model.Order;
import com.base.auth.model.OrderItem;
import com.base.auth.model.criteria.OrderCriteria;
import com.base.auth.repository.CartItemRepository;
import com.base.auth.repository.CartRepository;
import com.base.auth.repository.OrderItemRepository;
import com.base.auth.repository.OrderRepository;
import com.base.auth.utils.CodeGeneratorUtils;
import com.base.auth.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CodeGeneratorUtils codeGeneratorUtils;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN_ORD_L')")
    public ApiMessageDto<ResponseListDto<List<OrderDto>>> getOrderList(
            @Valid @ModelAttribute OrderCriteria orderCriteria,
            Pageable pageable
    ) {
        Specification<Order> specification = orderCriteria.getSpecification();
        Page<Order> orderPage = orderRepository.findAll(specification, pageable);

        ResponseListDto<List<OrderDto>> result = new ResponseListDto<>(
                orderMapper.fromEntityToOrderDtoList(orderPage.getContent()),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<OrderDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get order list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/my-orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_L_MY')")
    public ApiMessageDto<ResponseListDto<List<OrderDto>>> getMyOrders(
            @Valid @ModelAttribute OrderCriteria orderCriteria,
            Pageable pageable
    ) {
        Long accountId = SecurityUtils.getAccountId();
        orderCriteria.setCustomerId(accountId);
        Specification<Order> specification = orderCriteria.getSpecification();
        Page<Order> orderPage = orderRepository.findAll(specification, pageable);

        ResponseListDto<List<OrderDto>> result = new ResponseListDto<>(
                orderMapper.fromEntityToOrderDtoList(orderPage.getContent()),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<OrderDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get my orders successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_C')")
    @Transactional
    public ApiMessageDto<String> createOrder() {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long accountId = SecurityUtils.getAccountId();
        Cart cart = cartRepository.findByCustomerId(accountId);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CART_ERROR_EMPTY);
            apiMessageDto.setMessage("Cart is empty");
            return apiMessageDto;
        }

        // Create Order
        Order order = new Order();
        order.setCode(codeGeneratorUtils.generateUniqueCode(Order.class, "code", 6));
        order.setCustomer(cart.getCustomer());
        order.setState(1); // default BOOKING

        double totalMoney = 0;
        double totalSaleOff = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSinglePrice(cartItem.getProduct().getPrice());
            orderItem.setSaleOff(cartItem.getProduct().getSaleOff());

            double itemTotal = cartItem.getQuantity() * cartItem.getProduct().getPrice();
            double itemDiscount = itemTotal * (cartItem.getProduct().getSaleOff() / 100.0);
            totalMoney += itemTotal;
            totalSaleOff += itemDiscount;

            orderItems.add(orderItem);
        }
        order.setTotalMoney(totalMoney);
        order.setTotalSaleOff(totalSaleOff);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        // Delete CartItem after create Order
        cartItemRepository.deleteByCartId(cart.getId());

        apiMessageDto.setMessage("Order created successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN_ORD_U')")
    @Transactional
    public ApiMessageDto<String> approveOrder(@Valid @RequestBody UpdateOrderForm updateOrderForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Order order = orderRepository.findById(updateOrderForm.getId()).orElse(null);
        if (order == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Order not found");
            return apiMessageDto;
        }

        Integer newState = updateOrderForm.getState();
        if (newState != order.getState() + 1) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_INVALID_TRANSITION);
            apiMessageDto.setMessage("New state must be the next sequential state");
            return apiMessageDto;
        }
        order.setState(newState);
        orderRepository.save(order);

        apiMessageDto.setMessage("Order state updated successfully!");
        return apiMessageDto;
    }
}
