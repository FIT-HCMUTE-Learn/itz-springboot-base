package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.customer.CustomerDto;
import com.base.auth.form.customer.CreateCustomerForm;
import com.base.auth.form.customer.UpdateCustomerForm;
import com.base.auth.mapper.CustomerMapper;
import com.base.auth.model.*;
import com.base.auth.model.criteria.CustomerCriteria;
import com.base.auth.repository.*;
import com.base.auth.service.UserBaseApiService;
import com.base.auth.utils.CodeGeneratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/customer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private NationRepository nationRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private UserBaseApiService userBaseApiService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CodeGeneratorUtils codeGeneratorUtils;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_L')")
    public ApiMessageDto<ResponseListDto<List<CustomerDto>>> getCustomerList(CustomerCriteria customerCriteria, Pageable pageable) {
        Specification<Customer> specification = customerCriteria.getSpecification();
        Page<Customer> customerPage = customerRepository.findAll(specification, pageable);

        ResponseListDto<List<CustomerDto>> result = new ResponseListDto<>(
                customerMapper.fromEntityToCustomerDtoList(customerPage.getContent()),
                customerPage.getTotalElements(),
                customerPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<CustomerDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get customer list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_V')")
    public ApiMessageDto<CustomerDto> getCustomerById(@PathVariable Long id) {
        ApiMessageDto<CustomerDto> apiMessageDto = new ApiMessageDto<>();

        Customer customer = customerRepository.findById(id).orElseThrow(null);
        if (customer == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(customerMapper.fromEntityToCustomerDto(customer));
        apiMessageDto.setMessage("Get customer successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_C')")
    @Transactional
    public ApiMessageDto<String> createCustomer(@Valid @RequestBody CreateCustomerForm createCustomerForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        // Create Account
        if (accountRepository.existsByUsername(createCustomerForm.getUsername())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
            apiMessageDto.setMessage("Username already exists");
            return apiMessageDto;
        }
        Group group = groupRepository.findFirstByKind(UserBaseConstant.USER_KIND_CUSTOMER);
        Account account = new Account();
        account.setKind(UserBaseConstant.USER_KIND_CUSTOMER);
        account.setUsername(createCustomerForm.getUsername());
        account.setPassword(passwordEncoder.encode(createCustomerForm.getPassword()));
        account.setPhone(createCustomerForm.getPhone());
        account.setEmail(createCustomerForm.getEmail());
        account.setFullName(createCustomerForm.getFullName());
        account.setGroup(group);
        account.setStatus(createCustomerForm.getStatus());
        account.setAvatarPath(createCustomerForm.getAvatarPath());
        Account savedAccount = accountRepository.save(account);

        // Create Customer
        Nation province = nationRepository.findByIdAndType(createCustomerForm.getProvinceId(),
                UserBaseConstant.NATION_KIND_PROVINCE).orElseThrow(null);
        if (province == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Province not found");
            return apiMessageDto;
        }
        Nation district = nationRepository.findByIdAndType(createCustomerForm.getDistrictId(),
                UserBaseConstant.NATION_KIND_DISTRICT).orElseThrow(null);
        if (district == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("District not found");
            return apiMessageDto;
        }
        Nation commune = nationRepository.findByIdAndType(createCustomerForm.getCommuneId(),
                UserBaseConstant.NATION_KIND_COMMUNE).orElseThrow(null);
        if (commune == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Commune not found");
            return apiMessageDto;
        }
        Customer customer = customerMapper.fromCreateCustomerFormToEntity(createCustomerForm);
        customer.setAccount(savedAccount);
        customer.setProvince(province);
        customer.setDistrict(district);
        customer.setCommune(commune);
        customerRepository.save(customer);

        // Create Cart
        Cart cart = new Cart();
        String cartCode = codeGeneratorUtils.generateUniqueCode(Cart.class, "code", 6);
        cart.setCode(cartCode);
        cart.setCustomer(customer);
        cartRepository.save(cart);

        // Complete
        apiMessageDto.setMessage("Create customer successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_U')")
    @Transactional
    public ApiMessageDto<String> updateCustomer(@Valid @RequestBody UpdateCustomerForm updateCustomerForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Customer customer = customerRepository.findById(updateCustomerForm.getId()).orElseThrow(null);
        if (customer == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer not found");
            return apiMessageDto;
        }

        // Update Account
        Account account = customer.getAccount();
        if (!account.getUsername().equals(updateCustomerForm.getUsername())) {
            if (accountRepository.existsByUsername(updateCustomerForm.getUsername())) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
                apiMessageDto.setMessage("Username already exists");
                return apiMessageDto;
            }
        }
        account.setUsername(updateCustomerForm.getUsername());
        if (StringUtils.isNoneBlank(updateCustomerForm.getPassword())
        && !passwordEncoder.matches(updateCustomerForm.getPassword(), account.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateCustomerForm.getPassword()));
        }
        account.setPhone(updateCustomerForm.getPhone());
        account.setEmail(updateCustomerForm.getEmail());
        account.setFullName(updateCustomerForm.getFullName());
        if (StringUtils.isNoneBlank(updateCustomerForm.getAvatarPath())) {
            if(!updateCustomerForm.getAvatarPath().equals(account.getAvatarPath())){
                userBaseApiService.deleteFile(account.getAvatarPath());
            }
            account.setAvatarPath(updateCustomerForm.getAvatarPath());
        }
        accountRepository.save(account);

        // Update Customer
        if (!updateCustomerForm.getProvinceId().equals(customer.getProvince().getId())) {
            Nation province = nationRepository.findByIdAndType(updateCustomerForm.getProvinceId(),
                    UserBaseConstant.NATION_KIND_PROVINCE).orElseThrow(null);
            if (province == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("Province not found");
                return apiMessageDto;
            }
            customer.setProvince(province);
        }
        if (!updateCustomerForm.getDistrictId().equals(customer.getDistrict().getId())) {
            Nation district = nationRepository.findByIdAndType(updateCustomerForm.getDistrictId(),
                    UserBaseConstant.NATION_KIND_DISTRICT).orElseThrow(null);
            if (district == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("District not found");
                return apiMessageDto;
            }
            customer.setDistrict(district);
        }
        if (!updateCustomerForm.getCommuneId().equals(customer.getCommune().getId())) {
            Nation commune = nationRepository.findByIdAndType(updateCustomerForm.getCommuneId(),
                    UserBaseConstant.NATION_KIND_COMMUNE).orElseThrow(null);
            if (commune == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("Commune not found");
                return apiMessageDto;
            }
            customer.setCommune(commune);
        }
        customerMapper.updateFromUpdateCustomerForm(customer, updateCustomerForm);
        customerRepository.save(customer);
        apiMessageDto.setMessage("Update customer successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_D')")
    @Transactional
    public ApiMessageDto<String> deleteCustomer(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Customer customer = customerRepository.findById(id).orElseThrow(null);
        if (customer == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer not found");
            return apiMessageDto;
        }

        // Delete Cart
        Cart cart = cartRepository.findByCustomerId(id);
        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getId());
            cartRepository.delete(cart);
        }
        // Delete Account
        accountRepository.deleteById(customer.getAccount().getId());
        // Delete Customer
        customerRepository.deleteById(id);
        apiMessageDto.setMessage("Delete customer successfully");

        return apiMessageDto;
    }
}

