package com.fpt.fms.service;

import com.fpt.fms.domain.Tool;
import com.fpt.fms.domain.ToolCategory;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.ToolCategoryRepository;
import com.fpt.fms.repository.ToolRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.ToolSpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IToolService;
import com.fpt.fms.service.dto.BlogCategoryDTO;
import com.fpt.fms.service.dto.BlogDTO;
import com.fpt.fms.service.dto.ToolDTO;
import com.fpt.fms.service.search.SearchToolDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ToolService implements IToolService {

    private final ToolRepository toolRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ToolCategoryRepository toolCategoryRepository;

    public ToolService(
        ToolRepository toolRepository,
        UserRepository userRepository,
        ModelMapper modelMapper,
        ToolCategoryRepository toolCategoryRepository
    ) {
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.toolCategoryRepository = toolCategoryRepository;
    }

    @Override
    public Page<ToolDTO> getTools(Pageable pageable) {
        List<ToolDTO> toolDTOList = toolRepository
            .findToolByToolCategoryInAndAndCreatedBy(pageable, getToolCategorys(),getCur())
            .stream()
            .filter(blog -> blog.getToolCategory() != null && !blog.getToolCategory().getDeleted() && blog.getToolCategory().getStatus())
            .distinct()
            .map(
                tool -> {
                    ToolDTO toolList = new ToolDTO();
                    toolList.setId((tool.getId()));
                    toolList.setName(tool.getName());
                    toolList.setSize(tool.getSize());
                    toolList.setSource(tool.getSource());
                    toolList.setStatus(tool.getStatus());
                    toolList.setToolCategory(tool.getToolCategory().getName());
                    toolList.setCreatedDate(Date.from(tool.getCreatedDate()));
                    return toolList;
                }
            )
            .collect(Collectors.toList());
        return new PageImpl<>(toolDTOList, pageable, toolDTOList.size());
    }

    @Override
    public Page<ToolDTO> searchTool(SearchToolDTO searchDTO, Pageable pageable) {
        Specification<Tool> specification = ToolSpectificationBuilder.buildQuery(searchDTO,getToolCategorys());
        List<ToolDTO> toolDTOList = toolRepository
            .findAll(specification.and((root, query, cb) -> cb.equal(root.get("createdBy"), getCur())), pageable)
            .stream()
            .filter(tool -> tool.getToolCategory() != null && !tool.getToolCategory().getDeleted() && tool.getToolCategory().getStatus())
            .distinct()
            .map(
                tool -> {
                    ToolDTO toolList = new ToolDTO();
                    toolList.setId((tool.getId()));
                    toolList.setName(tool.getName());
                    toolList.setSize(tool.getSize());
                    toolList.setSource(tool.getSource());
                    toolList.setStatus(tool.getStatus());
                    toolList.setToolCategory(tool.getToolCategory().getName());
                    toolList.setCreatedDate(Date.from(tool.getCreatedDate()));
                    return toolList;
                }
            )
            .collect(Collectors.toList());
        return new PageImpl<>(toolDTOList, pageable, toolDTOList.size());
    }

    @Override
    public void registerTool(ToolDTO toolDTO) {
        Tool existingTool= toolRepository.findByName(toolDTO.getName());
        if(existingTool != null ){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên công đã tồn tại!");
        }
        Tool tool = modelMapper.map(toolDTO, Tool.class);
        tool.setCreatedBy(getUser().getEmail());
        tool.setToolCategory(getToolByName(toolDTO.getToolCategory()));
        tool.setStatus(true);
        toolRepository.save(tool);
    }

    @Override
    public Optional<ToolDTO> getTool(Long id) {
        return toolRepository
            .findToolByIdAndCreatedBy(id,getCur())
            .map(
                tool -> {
                    ToolDTO detail = new ToolDTO();
                    detail.setId((tool.getId()));
                    detail.setName(tool.getName());
                    detail.setDescription(tool.getDescription());
                    detail.setSource(tool.getSource());
                    detail.setSize(tool.getSize());
                    detail.setToolCategory(tool.getToolCategory().getName());
                    detail.setStatus(tool.getStatus());
                    detail.setCreateBy(tool.getCreatedBy());
                    detail.setLastModifiedBy(tool.getLastModifiedBy());
                    detail.setCreatedDate(Date.from(tool.getCreatedDate()));
                    detail.setLastModifiedDate(Date.from(tool.getLastModifiedDate()));
                    return detail;
                }
            );
    }

    @Override
    public void updateTool(ToolDTO toolDTO) {
        Tool tool = getToolById(toolDTO.getId());
        Tool existingTool= toolRepository.findByName(toolDTO.getName());
        if(existingTool != null  && !Objects.equals(existingTool.getId(), toolDTO.getId())){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Tên công đã tồn tại!");
        }
        tool.setName(toolDTO.getName());
        tool.setDescription(toolDTO.getDescription());
        tool.setSize(toolDTO.getSize());
        tool.setLastModifiedDate(Instant.now());
        tool.setStatus(toolDTO.getStatus());
        tool.setToolCategory(getToolByName(toolDTO.getToolCategory()));
        toolRepository.save(tool);
    }

    @Override
    public void deleteTool(Long id) {
        Tool tool = getToolById(id);
        checkToolStatus(tool);
        tool.setDeleted(Boolean.TRUE);
        toolRepository.save(tool);
    }

    @Override
    public void deleteTools(Set<Long> ids) {
        List<Tool> tools = toolRepository.findToolByIdInAndCreatedBy(ids,getCur());
        if (tools.isEmpty()) {
            throw new BaseException(400,"Không tìm thấy công cụ");
        }
        tools.forEach(
            tool -> {
                checkToolStatus(tool);
                tool.setDeleted(Boolean.TRUE);
            }
        );
        toolRepository.saveAll(tools);
    }

    @Override
    public List<ToolDTO> getListTool() {
        return toolRepository
            .findAllByCreatedBy(getCur())
            .stream()
            .map(
                tool -> {
                    ToolDTO toolDTO = new ToolDTO();
                    toolDTO.setId(tool.getId());
                    toolDTO.setName(tool.getName());
                    return toolDTO;
                }
            )
            .collect(Collectors.toList());
    }

    private User getUser() {
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
        return userRepository.findOneByLogin(curUser).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy người dùng"));
    }

    private Tool getToolById(Long id) {
        return toolRepository.findToolByIdAndCreatedBy(id,getCur()).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy công cụ"));
    }

    private ToolCategory getToolByName(String name) {
        return toolCategoryRepository.findByName(name);
    }

    private static void checkToolStatus(Tool toolCategory) {
        if (Boolean.TRUE.equals(toolCategory.getStatus())) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa công cụ đang hoạt động");
        }
    }
    private List<ToolCategory> getToolCategorys(){
        return toolCategoryRepository.findByCreatedBy(getCur());
    }

    private String getCur() {
        String createBy = getUser().getCreatedBy();
        if (createBy.equals("anonymousUser")) {
            createBy = getUser().getEmail();
        }
        return createBy;
    }
}
