package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.product.ProductDto;
import com.base.auth.form.product.CreateProductForm;
import com.base.auth.form.product.UpdateProductForm;
import com.base.auth.mapper.ProductMapper;
import com.base.auth.model.Product;
import com.base.auth.model.criteria.ProductCriteria;
import com.base.auth.repository.CartItemRepository;
import com.base.auth.repository.ProductRepository;
import com.base.auth.service.UserBaseApiService;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/v1/product")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController extends ABasicController{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserBaseApiService userBaseApiService;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRO_L')")
    public ApiMessageDto<ResponseListDto<List<ProductDto>>> getProductList(
            @Valid @ModelAttribute ProductCriteria productCriteria,
            Pageable pageable
    ) {
        Specification<Product> specification = productCriteria.getSpecification();
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        ResponseListDto<List<ProductDto>> result = new ResponseListDto<>(
                productMapper.fromEntityToProductDtoList(productPage.getContent()),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<ProductDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get product list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRO_V')")
    public ApiMessageDto<ProductDto> getProductById(@PathVariable Long id) {
        ApiMessageDto<ProductDto> apiMessageDto = new ApiMessageDto<>();

        Product product = productRepository.findById(id).orElseThrow(null);
        if (product == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.PRODUCT_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Product not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(productMapper.fromEntityToProductDto(product));
        apiMessageDto.setMessage("Get product successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRO_C')")
    public ApiMessageDto<String> createProduct(@Valid @RequestBody CreateProductForm createProductForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Product product = productMapper.fromCreateProductFormToEntity(createProductForm);
        productRepository.save(product);
        apiMessageDto.setMessage("Create product successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRO_U')")
    public ApiMessageDto<String> updateProduct(@Valid @RequestBody UpdateProductForm updateProductForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Product product = productRepository.findById(updateProductForm.getId()).orElseThrow(null);
        if (product == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.PRODUCT_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Product not found");
            return apiMessageDto;
        }
        if (StringUtils.isNoneBlank(updateProductForm.getImage())) {
            if(!updateProductForm.getImage().equals(product.getImage())){
                userBaseApiService.deleteFile(product.getImage());
            }
            product.setImage(updateProductForm.getImage());
        }
        productMapper.updateFromUpdateProductForm(product, updateProductForm);
        productRepository.save(product);
        apiMessageDto.setMessage("Update product successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRO_D')")
    public ApiMessageDto<String> deleteProduct(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Product product = productRepository.findById(id).orElseThrow(null);
        if (product == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.PRODUCT_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Product not found");
            return apiMessageDto;
        }
        if (cartItemRepository.existsByProductId(id)) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.PRODUCT_ERROR_IN_CART);
            apiMessageDto.setMessage("Cannot delete product because it exists in a cart");
            return apiMessageDto;
        }

        productRepository.deleteById(id);
        apiMessageDto.setMessage("Delete product successfully");

        return apiMessageDto;
    }
}
