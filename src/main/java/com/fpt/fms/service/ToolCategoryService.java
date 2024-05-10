package com.fpt.fms.service;

import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.Location;
import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.ToolCategoryRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.ToolCategorySpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IToolCategoryService;
import com.fpt.fms.service.dto.ToolCategoryDTO;
import com.fpt.fms.service.search.SearchToolCategoryDTO;
import com.fpt.fms.web.rest.errors.BaseException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ToolCategoryService implements IToolCategoryService {

    private final ToolCategoryRepository toolCategoryRepository;

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public ToolCategoryService(ToolCategoryRepository toolCategoryRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.toolCategoryRepository = toolCategoryRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ToolCategoryDTO> getToolCategory(Long toolCategoryId) {
        return toolCategoryRepository
            .findById(toolCategoryId)
            .map(
                toolCategory -> {
                    ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
                    toolCategoryDTO.setId((toolCategory.getId()));
                    toolCategoryDTO.setName(toolCategory.getName());
                    toolCategoryDTO.setDescription(toolCategory.getDescription());
                    toolCategoryDTO.setStatus(toolCategory.getStatus());
                    toolCategoryDTO.setCreatedDate(Date.from(toolCategory.getCreatedDate()));
                    toolCategoryDTO.setLastModifiedDate(Date.from(toolCategory.getLastModifiedDate()));
                    toolCategoryDTO.setCreateBy(toolCategory.getCreatedBy());
                    toolCategoryDTO.setLastModifiedBy(toolCategory.getLastModifiedBy());
                    return toolCategoryDTO;
                }
            );
    }

    @Override
    public Page<ToolCategoryDTO> getToolCategorys(Pageable pageable) {
        List<ToolCategoryDTO> toolCategoryDTOS = toolCategoryRepository
            .findAllByCreatedBy(getCur(), pageable)
            .stream()
            .filter(toolgcate -> !toolgcate.getDeleted())
            .map(
                toolcategory -> {
                    ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
                    toolCategoryDTO.setId((toolcategory.getId()));
                    toolCategoryDTO.setName(toolcategory.getName());
                    toolCategoryDTO.setDescription(toolcategory.getDescription());
                    toolCategoryDTO.setStatus(toolcategory.getStatus());
                    toolCategoryDTO.setCreatedDate(Date.from(toolcategory.getCreatedDate()));
                    return toolCategoryDTO;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(toolCategoryDTOS, pageable, toolCategoryDTOS.size());
    }

    @Override
    public Page<ToolCategoryDTO> searchToolCategory(SearchToolCategoryDTO searchDTO, Pageable pageable) {
        Specification<ToolCategory> specification = ToolCategorySpectificationBuilder.buildQuery(searchDTO);
        List<ToolCategoryDTO> toolCategoryDTOS = toolCategoryRepository
            .findAll(specification.and((root, query, cb) -> cb.equal(root.get("createdBy"), getCur())), pageable)
            .stream()
            .filter(toolgcate -> !toolgcate.getDeleted())
            .map(
                toolcategory -> {
                    ToolCategoryDTO toolCategoryDTO = new ToolCategoryDTO();
                    toolCategoryDTO.setId((toolcategory.getId()));
                    toolCategoryDTO.setName(toolcategory.getName());
                    toolCategoryDTO.setDescription(toolcategory.getDescription());
                    toolCategoryDTO.setStatus(toolcategory.getStatus());
                    toolCategoryDTO.setCreatedDate(Date.from(toolcategory.getCreatedDate()));
                    return toolCategoryDTO;
                }
            )
            .collect(Collectors.toList());

        return new PageImpl<>(toolCategoryDTOS, pageable, toolCategoryDTOS.size());
    }

    @Override
    public void registerToolCategory(ToolCategoryDTO toolCategoryDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            ToolCategory existingToolCate = toolCategoryRepository.findByName(toolCategoryDTO.getName());
            if (existingToolCate != null) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên loại công đã tồn tại!");
            }
            ToolCategory blogCategory = modelMapper.map(toolCategoryDTO, ToolCategory.class);
            blogCategory.setCreatedBy(getCur());
            toolCategoryRepository.save(blogCategory);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void updateToolCategory(ToolCategoryDTO toolCategoryDTO) {
        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {
            ToolCategory toolCategory = getToolCategoryByIdAndCreateBy(toolCategoryDTO.getId(),getCur());
            ToolCategory existingToolCate = toolCategoryRepository.findByName(toolCategoryDTO.getName());
            if (existingToolCate != null && !Objects.equals(existingToolCate.getId(), toolCategoryDTO.getId())) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên loại công đã tồn tại!");
            }
            toolCategory.setName(toolCategoryDTO.getName());
            toolCategory.setDescription(toolCategoryDTO.getDescription());
            toolCategory.setLastModifiedDate(Instant.now());
            toolCategory.setStatus(toolCategoryDTO.getStatus());
            toolCategoryRepository.save(toolCategory);
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
    }

    @Override
    public void deleteToolCategory(Long toolCategoryId) {

        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {

        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
        ToolCategory blogCategory = getToolCategoryByIdAndCreateBy(toolCategoryId, getCur());
        checkToolCategoryStatus(blogCategory);
        blogCategory.setDeleted(Boolean.TRUE);
        toolCategoryRepository.save(blogCategory);
    }

    @Override
    public void deleteToolCategorys(Set<Long> ids) {

        if (getUser().getFarmRole().equals(FarmRole.OWNER) || getUser().getFarmRole().equals(FarmRole.MANAGER)) {

        }else{
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Người dùng không đủ quyền hạn!");
        }
        List<ToolCategory> toolCategories = toolCategoryRepository.findToolCategoryByIdInAndCreatedBy(ids,getCur());
        if (toolCategories.isEmpty()) {
            throw new BaseException(400, "Không tìm thấy danh mục công cụ");
        }
        toolCategories.forEach(
            toolCategory -> {
                checkToolCategoryStatus(toolCategory);
                toolCategory.setDeleted(Boolean.TRUE);
            }
        );
        toolCategoryRepository.saveAll(toolCategories);
    }

    @Override
    public List<ToolCategoryDTO> listToolCateWithStatus() {
        return toolCategoryRepository
            .findAll()
            .stream()
            .filter(ToolCategory::getStatus)
            .map(
                tool -> {
                    ToolCategoryDTO a = new ToolCategoryDTO();
                    a.setId(tool.getId());
                    a.setName(tool.getName());
                    return a;
                }
            )
            .collect(Collectors.toList());
    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }

    private String getCur() {
        String createBy = getUser().getCreatedBy();
        if (createBy.equals("anonymousUser")) {
            createBy = getUser().getEmail();
        }
        return createBy;
    }


    private ToolCategory getToolCategoryByIdAndCreateBy(Long id , String email){
        ToolCategory toolCate = toolCategoryRepository.findByIdAndCreatedBy(id, email);
        if (toolCate == null) {
            throw new EmailAlreadyUsedException("Không tìm thấy loại công cụ");
        }
        return toolCate;
    }
    private static void checkToolCategoryStatus(ToolCategory toolCategory) {
        if (Boolean.TRUE.equals(toolCategory.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa loại công cụ đang hoạt động");
        }
    }
}
