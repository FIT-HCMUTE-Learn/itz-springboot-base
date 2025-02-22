package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.form.category.CreateCategoryForm;
import com.base.auth.form.category.UpdateCategoryForm;
import com.base.auth.mapper.CategoryMapper;
import com.base.auth.model.Category;
import com.base.auth.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CA_L')")
    public ApiMessageDto<List<CategoryDto>> getCategoryList() {
        ApiMessageDto<List<CategoryDto>> apiMessageDto = new ApiMessageDto<>();

        List<Category> categoryList = categoryRepository.findAll();
        apiMessageDto.setData(categoryMapper.fromEntityToDtoList(categoryList));
        apiMessageDto.setMessage("Get category list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CA_V')")
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
    @PreAuthorize("hasRole('CA_C')")
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
    @PreAuthorize("hasRole('CA_U')")
    public ApiMessageDto<String> updateCategory(@Valid @RequestBody UpdateCategoryForm updateCategoryForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(updateCategoryForm.getCategoryId()).orElseThrow(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        categoryMapper.mappingForUpdateServiceCategory(updateCategoryForm, category);
        categoryRepository.save(category);
        apiMessageDto.setMessage("Update category successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_D')")
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
