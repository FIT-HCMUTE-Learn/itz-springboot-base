package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.nation.NationDto;
import com.base.auth.form.nation.CreateNationForm;
import com.base.auth.form.nation.UpdateNationForm;
import com.base.auth.mapper.NationMapper;
import com.base.auth.model.Nation;
import com.base.auth.model.criteria.NationCriteria;
import com.base.auth.repository.CustomerRepository;
import com.base.auth.repository.NationRepository;
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
@RequestMapping("/v1/nation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NationController {
    @Autowired
    private NationRepository nationRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private NationMapper nationMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NAT_L')")
    public ApiMessageDto<ResponseListDto<List<NationDto>>> getNationList(NationCriteria nationCriteria, Pageable pageable) {
        Specification<Nation> specification = nationCriteria.getSpecification();
        Page<Nation> nationPage = nationRepository.findAll(specification, pageable);

        ResponseListDto<List<NationDto>> result = new ResponseListDto<>(
                nationMapper.fromEntityToNationDtoList(nationPage.getContent()),
                nationPage.getTotalElements(),
                nationPage.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<NationDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(result);
        apiMessageDto.setMessage("Get nation list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NAT_V')")
    public ApiMessageDto<NationDto> getNationById(@PathVariable Long id) {
        ApiMessageDto<NationDto> apiMessageDto = new ApiMessageDto<>();

        Nation nation = nationRepository.findById(id).orElseThrow(null);
        if (nation == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Nation not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(nationMapper.fromEntityToNationDto(nation));
        apiMessageDto.setMessage("Get nation successfully");

        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NAT_C')")
    public ApiMessageDto<String> createNation(@Valid @RequestBody CreateNationForm createNationForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Nation nation = nationMapper.fromCreateNationFormToEntity(createNationForm);
        nationRepository.save(nation);
        apiMessageDto.setMessage("Create nation successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NAT_U')")
    public ApiMessageDto<String> updateNation(@Valid @RequestBody UpdateNationForm updateNationForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Nation nation = nationRepository.findById(updateNationForm.getId()).orElseThrow(null);
        if (nation == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Nation not found");
            return apiMessageDto;
        }
        nationMapper.updateFromUpdateNationForm(nation, updateNationForm);
        nationRepository.save(nation);
        apiMessageDto.setMessage("Update nation successfully");

        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NAT_D')")
    public ApiMessageDto<String> deleteNation(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Nation nation = nationRepository.findById(id).orElseThrow(null);
        if (nation == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Nation not found");
            return apiMessageDto;
        }
        if (customerRepository.countCustomerByNationId(nation.getId()) > 0) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.NATION_ERROR_CANT_DELETE_RELATIONSHIP_WITH_CUSTOMER);
            apiMessageDto.setMessage("There are still customers in the nation");
            return apiMessageDto;
        }

        nationRepository.deleteById(id);
        apiMessageDto.setMessage("Delete nation successfully");

        return apiMessageDto;
    }
}
