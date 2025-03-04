package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.customeraddress.CustomerAddressDto;
import com.base.auth.form.customeraddress.CreateCustomerAddressForm;
import com.base.auth.form.customeraddress.UpdateCustomerAddressForm;
import com.base.auth.mapper.CustomerAddressMapper;
import com.base.auth.model.Customer;
import com.base.auth.model.CustomerAddress;
import com.base.auth.model.Nation;
import com.base.auth.model.criteria.CustomerAddressCriteria;
import com.base.auth.repository.CustomerAddressRepository;
import com.base.auth.repository.CustomerRepository;
import com.base.auth.repository.NationRepository;
import com.base.auth.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/customer-address")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerAddressController extends ABasicController {
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private NationRepository nationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerAddressMapper customerAddressMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_ADR_L')")
    public ApiMessageDto<ResponseListDto<List<CustomerAddressDto>>> getCustomerAddressList(
            @Valid @ModelAttribute CustomerAddressCriteria customerAddressCriteria,
            Pageable pageable
    ) {
        Specification<CustomerAddress> specification = customerAddressCriteria.getSpecification();
        Page<CustomerAddress> customerAddressPage = customerAddressRepository.findAll(specification, pageable);

        ResponseListDto<List<CustomerAddressDto>> result = new ResponseListDto<>(
                customerAddressMapper.fromEntityToCustomerAddressDtoList(customerAddressPage.getContent()),
                customerAddressPage.getTotalElements(),
                customerAddressPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<CustomerAddressDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get customer address list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_ADR_V')")
    public ApiMessageDto<CustomerAddressDto> getCustomerAddressById(@PathVariable Long id) {
        ApiMessageDto<CustomerAddressDto> apiMessageDto = new ApiMessageDto<>();

        CustomerAddress customerAddress = customerAddressRepository.findById(id).orElseThrow(null);
        if (customerAddress == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ADDRESS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer address not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(customerAddressMapper.fromEntityToCustomerAddressDto(customerAddress));
        apiMessageDto.setMessage("Get customer address successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_ADR_C')")
    @Transactional
    public ApiMessageDto<String> createCustomerAddress(@Valid @RequestBody CreateCustomerAddressForm createCustomerAddressForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        // Get current user
        CustomerAddress customerAddress = customerAddressMapper.fromCustomerAddressCreateFormToEntity(createCustomerAddressForm);
        Long accountId = getCurrentUser();
        Customer customer = customerRepository.findById(accountId).orElseThrow(null);
        customerAddress.setCustomer(customer);

        // Valid Nation Province
        Nation province = nationRepository.findByIdAndType(createCustomerAddressForm.getProvinceId(),
                UserBaseConstant.NATION_KIND_PROVINCE).orElseThrow(null);
        if (province == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Province not found");
            return apiMessageDto;
        }
        customerAddress.setProvince(province);

        // Valid Nation District
        Nation district = nationRepository.findByIdAndType(createCustomerAddressForm.getDistrictId(),
                UserBaseConstant.NATION_KIND_DISTRICT).orElseThrow(null);
        if (district == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("District not found");
            return apiMessageDto;
        }
        if (!district.getParent().getId().equals(province.getId())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_PARENT_INVALID);
            apiMessageDto.setMessage("District parent invalid");
            return apiMessageDto;
        }
        customerAddress.setDistrict(district);

        // Valid Nation Commune
        Nation commune = nationRepository.findByIdAndType(createCustomerAddressForm.getCommuneId(),
                UserBaseConstant.NATION_KIND_COMMUNE).orElseThrow(null);
        if (commune == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Commune not found");
            return apiMessageDto;
        }
        if (!commune.getParent().getId().equals(district.getId())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_PARENT_INVALID);
            apiMessageDto.setMessage("Commune parent invalid");
            return apiMessageDto;
        }
        customerAddress.setCommune(commune);

        // Process default CustomerAddress
        if (createCustomerAddressForm.getIsDefault()) {
            customerAddressRepository.reverseIsDefaultByCustomerId(customer.getId());
        }

        customerAddressRepository.save(customerAddress);
        apiMessageDto.setMessage("Create customer address successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_ADR_U')")
    @Transactional
    public ApiMessageDto<String> updateCustomerAddress(@Valid @RequestBody UpdateCustomerAddressForm updateCustomerAddressForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        // Get CustomerAddress
        CustomerAddress customerAddress = customerAddressRepository.findById(updateCustomerAddressForm.getId()).orElseThrow(null);
        if (customerAddress == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ADDRESS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer address not found");
            return apiMessageDto;
        }

        // Update Nation Province
        customerAddressMapper.updateFromUpdateCustomerAddressForm(customerAddress, updateCustomerAddressForm);
        if (!updateCustomerAddressForm.getProvinceId().equals(customerAddress.getProvince().getId())) {
            Nation province = nationRepository.findByIdAndType(updateCustomerAddressForm.getProvinceId(),
                    UserBaseConstant.NATION_KIND_PROVINCE).orElseThrow(null);
            if (province == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("Province not found");
                return apiMessageDto;
            }
            customerAddress.setProvince(province);
        }

        // Update Nation District
        if (!updateCustomerAddressForm.getDistrictId().equals(customerAddress.getDistrict().getId())) {
            Nation district = nationRepository.findByIdAndType(updateCustomerAddressForm.getDistrictId(),
                    UserBaseConstant.NATION_KIND_DISTRICT).orElseThrow(null);
            if (district == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("District not found");
                return apiMessageDto;
            }
            if (!district.getParent().getId().equals(customerAddress.getProvince().getId())) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_PARENT_INVALID);
                apiMessageDto.setMessage("District parent invalid");
                return apiMessageDto;
            }
            customerAddress.setDistrict(district);
        }

        // Update Nation Commune
        if (!updateCustomerAddressForm.getCommuneId().equals(customerAddress.getCommune().getId())) {
            Nation commune = nationRepository.findByIdAndType(updateCustomerAddressForm.getCommuneId(),
                    UserBaseConstant.NATION_KIND_COMMUNE).orElseThrow(null);
            if (commune == null){
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("Commune not found");
                return apiMessageDto;
            }
            if (!commune.getParent().getId().equals(customerAddress.getDistrict().getId())) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.NATION_ERROR_PARENT_INVALID);
                apiMessageDto.setMessage("Commune parent invalid");
                return apiMessageDto;
            }
            customerAddress.setCommune(commune);
        }

        // Process default CustomerAddress
        Long accountId = getCurrentUser();
        if (updateCustomerAddressForm.getIsDefault()) {
            customerAddressRepository.reverseIsDefaultByCustomerId(accountId);
        }

        customerAddressRepository.save(customerAddress);
        apiMessageDto.setMessage("Update customer address successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUS_ADR_D')")
    public ApiMessageDto<String> deleteCustomerAddress(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        CustomerAddress customerAddress = customerAddressRepository.findById(id).orElseThrow(null);
        if (customerAddress == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ADDRESS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Customer address not found");
            return apiMessageDto;
        }
        Long count = orderRepository.countByCustomerAddressId(id);
        if (count > 0) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CUSTOMER_ADDRESS_CANT_DELETE_RELATIONSHIP_WITH_ORDER);
            apiMessageDto.setMessage("There are still orders in the customer address");
            return apiMessageDto;
        }
        customerAddressRepository.deleteById(id);
        apiMessageDto.setMessage("Delete customer address successfully");

        return apiMessageDto;
    }
}
