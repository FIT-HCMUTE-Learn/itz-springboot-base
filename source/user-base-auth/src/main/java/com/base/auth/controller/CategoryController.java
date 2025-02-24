package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.form.category.CreateCategoryForm;
import com.base.auth.form.category.UpdateCategoryForm;
import com.base.auth.mapper.CategoryMapper;
import com.base.auth.model.Category;
import com.base.auth.model.criteria.CategoryCriteria;
import com.base.auth.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CategoryController extends ABasicController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_L')")
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> getCategoryList(CategoryCriteria categoryCriteria, Pageable pageable) {
        Specification<Category> specification = categoryCriteria.getSpecification();
        Page<Category> categoryPage = categoryRepository.findAll(specification, pageable);

        ResponseListDto<List<CategoryDto>> result = new ResponseListDto<>(
                categoryMapper.fromEntityToDtoList(categoryPage.getContent()),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages()
        );

        ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get category list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_V')")
    public ApiMessageDto<CategoryDto> getById(@PathVariable Long id) {
        ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(id).orElseThrow(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        apiMessageDto.setData(categoryMapper.fromEntityToCategoryDto(category));
        apiMessageDto.setMessage("Get category successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_C')")
    public ApiMessageDto<String> createCategory(@Valid @RequestBody CreateCategoryForm createCategoryForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        if (categoryRepository.existsByName(createCategoryForm.getName())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_EXIST);
            apiMessageDto.setMessage("Title already exists");
            return apiMessageDto;
        }

        Category category = categoryMapper.fromCreateCategory(createCategoryForm);
        categoryRepository.save(category);
        apiMessageDto.setMessage("Create category successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_U')")
    public ApiMessageDto<String> updateCategory(@Valid @RequestBody UpdateCategoryForm updateCategoryForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(updateCategoryForm.getCategoryId()).orElseThrow(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        if (!Objects.equals(category.getName(), updateCategoryForm.getName())
        && categoryRepository.existsByName(updateCategoryForm.getName())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_EXIST);
            apiMessageDto.setMessage("Title already exists");
            return apiMessageDto;
        }

        categoryMapper.mappingForUpdateServiceCategory(updateCategoryForm, category);
        categoryRepository.save(category);
        apiMessageDto.setMessage("Update category successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_D')")
    public ApiMessageDto<String> deleteCategory(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(id).orElseThrow(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        categoryRepository.deleteById(id);
        apiMessageDto.setMessage("Delete category successfully");

        return apiMessageDto;
    }
}
