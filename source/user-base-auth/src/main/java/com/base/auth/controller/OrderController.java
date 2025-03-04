package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.order.OrderDto;
import com.base.auth.form.order.CancelOrderForm;
import com.base.auth.form.order.ChangeOrderStateForm;
import com.base.auth.form.order.CreateOrderForm;
import com.base.auth.mapper.OrderMapper;
import com.base.auth.model.*;
import com.base.auth.model.criteria.OrderCriteria;
import com.base.auth.repository.*;
import com.base.auth.utils.CodeGeneratorUtils;
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

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController extends ABasicController{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
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

    @GetMapping(value = "/client-my-orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_L')")
    public ApiMessageDto<ResponseListDto<List<OrderDto>>> getMyOrders(
            @Valid @ModelAttribute OrderCriteria orderCriteria,
            Pageable pageable
    ) {
        Long accountId = getCurrentUser();
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

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN_ORD_V')")
    public ApiMessageDto<OrderDto> getOrderDetail(@PathVariable Long id) {
        ApiMessageDto<OrderDto> apiMessageDto = new ApiMessageDto<>();

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Order not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(orderMapper.fromEntityToOrderDto(order));
        apiMessageDto.setMessage("Get order successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_V')")
    public ApiMessageDto<OrderDto> getOrderDetailForClient(@PathVariable Long id) {
        ApiMessageDto<OrderDto> apiMessageDto = new ApiMessageDto<>();

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Order not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(orderMapper.fromEntityToOrderDto(order));
        apiMessageDto.setMessage("Get order successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/client-create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_C')")
    @Transactional
    public ApiMessageDto<String> createOrder(@Valid @RequestBody CreateOrderForm createOrderForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long accountId = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(accountId);
        if (cart == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CART_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Cart not found");
            return apiMessageDto;
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CART_ERROR_EMPTY);
            apiMessageDto.setMessage("Cart is empty");
            return apiMessageDto;
        }

        // Create Order
        Order order = new Order();
        order.setCode(codeGeneratorUtils.generateCode(6));
        order.setCustomer(cart.getCustomer());
        order.setState(UserBaseConstant.ORDER_STATE_BOOKING);

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

        // Add CustomerAddress to Order
        CustomerAddress customerAddress = customerAddressRepository.findById(createOrderForm.getCustomerAddressId()).orElse(null);
        if (customerAddress == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ADDRESS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer address not found");
            return apiMessageDto;
        }
        order.setCustomerAddress(customerAddress);

        // Save Order and OrderItem
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        // Delete CartItem after create Order
        cartItemRepository.deleteByCartId(cart.getId());

        apiMessageDto.setMessage("Order created successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/change-state", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN_ORD_U')")
    public ApiMessageDto<String> approveOrder(@Valid @RequestBody ChangeOrderStateForm changeOrderStateForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Order order = orderRepository.findById(changeOrderStateForm.getId()).orElse(null);
        if (order == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Order not found");
            return apiMessageDto;
        }

        Integer newState = changeOrderStateForm.getState();
        if (newState != order.getState() + 1) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_INVALID_TRANSITION);
            apiMessageDto.setMessage("New state must be the next sequential state");
            return apiMessageDto;
        }
        order.setState(newState);
        orderRepository.save(order);

        apiMessageDto.setMessage("Change order state successfully!");
        return apiMessageDto;
    }

    @PutMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ORD_U')")
    public ApiMessageDto<String> cancelOrder(@Valid @RequestBody CancelOrderForm cancelOrderForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Order order = orderRepository.findById(cancelOrderForm.getId()).orElse(null);
        if (order == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ORDER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Order not found");
            return apiMessageDto;
        }
        order.setState(UserBaseConstant.ORDER_STATE_CANCEL);
        orderRepository.save(order);

        apiMessageDto.setMessage("Cancel order successfully!");
        return apiMessageDto;
    }
}
