package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.customer.CustomerDto;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.customer.CreateCustomerForm;
import com.base.auth.form.customer.UpdateCustomerForm;
import com.base.auth.mapper.CustomerMapper;
import com.base.auth.model.Account;
import com.base.auth.model.Customer;
import com.base.auth.model.Nation;
import com.base.auth.model.criteria.CustomerCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.CustomerRepository;
import com.base.auth.repository.NationRepository;
import com.base.auth.utils.EntityFinderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private NationRepository nationRepository;

    @Autowired
    private CustomerMapper customerMapper;

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

        Customer customer = EntityFinderUtils.findByIdOrThrow(customerRepository, id,
                () -> new NotFoundException("Customer not found"));

        apiMessageDto.setData(customerMapper.fromEntityToCustomerDto(customer));
        apiMessageDto.setMessage("Get customer successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_C')")
    public ApiMessageDto<String> createCustomer(@Valid @RequestBody CreateCustomerForm createCustomerForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Account account = EntityFinderUtils.findByIdOrThrow(accountRepository, createCustomerForm.getAccountId(),
                () -> new NotFoundException("Account not found"));
        Nation province = EntityFinderUtils.findByIdOrThrow(nationRepository, createCustomerForm.getProvinceId(),
                () -> new NotFoundException("Province not found"));
        Nation district = EntityFinderUtils.findByIdOrThrow(nationRepository, createCustomerForm.getDistrictId(),
                () -> new NotFoundException("District not found"));
        Nation commune = EntityFinderUtils.findByIdOrThrow(nationRepository, createCustomerForm.getCommuneId(),
                () -> new NotFoundException("Commune not found"));

        Customer customer = customerMapper.fromCreateCustomerFormToEntity(createCustomerForm);
        customer.setAccount(account);
        customer.setProvince(province);
        customer.setDistrict(district);
        customer.setCommune(commune);
        customerRepository.save(customer);
        apiMessageDto.setMessage("Create customer successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_U')")
    public ApiMessageDto<String> updateCustomer(@Valid @RequestBody UpdateCustomerForm updateCustomerForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Customer customer = EntityFinderUtils.findByIdOrThrow(customerRepository, updateCustomerForm.getId(),
                () -> new NotFoundException("Customer not found"));

        Account account = EntityFinderUtils.findByIdOrThrow(accountRepository, updateCustomerForm.getAccountId(),
                () -> new NotFoundException("Account not found"));
        Nation province = EntityFinderUtils.findByIdOrThrow(nationRepository, updateCustomerForm.getProvinceId(),
                () -> new NotFoundException("Province not found"));
        Nation district = EntityFinderUtils.findByIdOrThrow(nationRepository, updateCustomerForm.getDistrictId(),
                () -> new NotFoundException("District not found"));
        Nation commune = EntityFinderUtils.findByIdOrThrow(nationRepository, updateCustomerForm.getCommuneId(),
                () -> new NotFoundException("Commune not found"));

        customerMapper.updateFromUpdateCustomerForm(customer, updateCustomerForm);
        customer.setAccount(account);
        customer.setProvince(province);
        customer.setDistrict(district);
        customer.setCommune(commune);
        customerRepository.save(customer);
        apiMessageDto.setMessage("Update customer successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_D')")
    public ApiMessageDto<String> deleteCustomer(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Customer customer = EntityFinderUtils.findByIdOrThrow(customerRepository, id,
                () -> new NotFoundException("Customer not found"));

        customerRepository.deleteById(id);
        apiMessageDto.setMessage("Delete customer successfully");

        return apiMessageDto;
    }
}

