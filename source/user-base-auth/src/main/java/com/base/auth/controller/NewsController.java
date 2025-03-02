package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.news.NewsAdminDto;
import com.base.auth.form.news.CreateNewsForm;
import com.base.auth.form.news.UpdateNewsForm;
import com.base.auth.mapper.NewsMapper;
import com.base.auth.model.News;
import com.base.auth.model.criteria.NewsCriteria;
import com.base.auth.repository.NewsRepository;
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
@RequestMapping("/v1/news")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NewsController extends ABasicController{
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsMapper newsMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_L')")
    public ApiMessageDto<ResponseListDto<List<NewsAdminDto>>> getNewsAdminList(NewsCriteria newsCriteria, Pageable pageable) {
        Specification<News> specification = newsCriteria.getSpecification();
        Page<News> newsPage = newsRepository.findAll(specification, pageable);

        ResponseListDto<List<NewsAdminDto>> result = new ResponseListDto<>(
                newsMapper.fromEntityToNewsAdminDtoList(newsPage.getContent()),
                newsPage.getTotalElements(),
                newsPage.getTotalPages()
        );

        ApiMessageDto<ResponseListDto<List<NewsAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get news admin list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_V')")
    public ApiMessageDto<NewsAdminDto> getNewsById(@PathVariable Long id) {
        ApiMessageDto<NewsAdminDto> apiMessageDto = new ApiMessageDto<>();

        News news = newsRepository.findById(id).orElseThrow(null);
        if (news == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NEWS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("News not found");
            return apiMessageDto;
        }

        apiMessageDto.setData(newsMapper.fromEntityToNewsAdminDto(news));
        apiMessageDto.setMessage("Get news admin successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_C')")
    public ApiMessageDto<String> createNews(@Valid @RequestBody CreateNewsForm createNewsForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        if (newsRepository.existsByTitle(createNewsForm.getTitle())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NEWS_ERROR_EXISTED);
            apiMessageDto.setMessage("Title already exists");
            return apiMessageDto;
        }

        News news = newsMapper.fromCreateNewsFormToEntity(createNewsForm);
        newsRepository.save(news);
        apiMessageDto.setMessage("Create news successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_U')")
    public ApiMessageDto<String> updateNews(@Valid @RequestBody UpdateNewsForm updateNewsForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        News news = newsRepository.findById(updateNewsForm.getId()).orElseThrow(null);
        if (news == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NEWS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("News not found");
            return apiMessageDto;
        }

        if (!Objects.equals(news.getTitle(), updateNewsForm.getTitle())
                && newsRepository.existsByTitle(updateNewsForm.getTitle())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NEWS_ERROR_EXISTED);
            apiMessageDto.setMessage("Title already exists");
            return apiMessageDto;
        }

        newsMapper.updateNewsFromUpdateNewsForm(updateNewsForm, news);
        newsRepository.save(news);
        apiMessageDto.setMessage("Update news successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_D')")
    public ApiMessageDto<String> deleteNews(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        News news = newsRepository.findById(id).orElseThrow(null);
        if (news == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NEWS_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("News not found");
            return apiMessageDto;
        }

        newsRepository.deleteById(id);
        apiMessageDto.setMessage("Delete news successfully");

        return apiMessageDto;
    }
}
