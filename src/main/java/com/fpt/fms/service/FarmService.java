package com.fpt.fms.service;

import com.fpt.fms.config.Constants;
import com.fpt.fms.domain.ApproveStatus;
import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import com.fpt.fms.repository.FarmRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.repository.spec.FarmSpectificationBuilder;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.baseservice.IFarmService;
import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.search.SearchDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import java.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmService implements IFarmService {

    private final ModelMapper modelMapper;
    private final FarmRepository farmRepository;

    private final UserRepository userRepository;

    public FarmService(ModelMapper modelMapper, FarmRepository farmRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerFarm(FarmDTO farmDTO) {
        if (
            farmRepository
                .findFarmByCreatedByAndStatusNot(
                    SecurityUtils
                        .getCurrentUserLogin()
                        .orElseThrow(
                            () -> {
                                throw new BaseException(HttpStatus.FORBIDDEN.value(), "không tìm thấy user hiện tại");
                            }
                        ),
                    ApproveStatus.REJECT
                )
                .isPresent()
        ) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "bạn không thể tạo thêm nông trại");
        }

        Farm farm = modelMapper.map(farmDTO, Farm.class);
        if (SecurityUtils.getAuthorities() == null || SecurityUtils.getAuthorities().stream().anyMatch(s -> s.contains("EMPLOYEE"))) {
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "nhân viên không thể tạo trang trại");
        }
        String curUser = SecurityUtils.getCurrentUserLogin().get();

        farm.setStatus(ApproveStatus.REQUEST);
        farm.setUser(userRepository.findOneByLogin(curUser).get());
        farmRepository.save(farm);
    }

    @Override
    public FarmDTO getFarmOfUserCurrent() {
        Optional<User> user = userRepository.findOneByLogin(
            SecurityUtils
                .getCurrentUserLogin()
                .orElseThrow(
                    () -> {
                        throw new BaseException(HttpStatus.FORBIDDEN.value(), "không tìm thấy user hiện tại");
                    }
                )
        );
        if (!user.isPresent()) {
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "không tìm thấy người dùng trong hệ thống");
        }

        String createdUser = user.get().getEmail();
        if (!user.get().getFarmRole().equals(FarmRole.OWNER)) {
            createdUser = user.get().getCreatedBy();
        }

        Optional<Farm> farm = farmRepository.findFarmByCreatedByAndStatusNot(createdUser, ApproveStatus.REJECT);
        if (!farm.isPresent()) {
            throw new BaseException(HttpStatus.NO_CONTENT.value(), "bạn chưa có trang trại nào!");
        }
        FarmDTO farmDTO = modelMapper.map(farm.get(), FarmDTO.class);

        farmDTO.setOwner(createdUser);

        return farmDTO;
    }

    @Override
    @Transactional
    public FarmDTO updateFarm(Long farmId, FarmDTO farmDTO) {
        String currentUser = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy thông tin người dùng trên hệ thống"));
        User user = userRepository.findOneByLogin(currentUser).get();

        if (!Arrays.asList(FarmRole.MANAGER, FarmRole.OWNER).stream().anyMatch(f -> f.equals(user.getFarmRole()))) {
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "Bạn không thể thay đổi thông tin trâng trại!");
        }
        String createdUser = user.getEmail();
        if (!user.getFarmRole().equals(FarmRole.OWNER)) {
            createdUser = user.getCreatedBy();
        }

        Optional<Farm> farm = farmRepository.findFarmByCreatedByAndStatusNot(createdUser, ApproveStatus.REJECT);
        if (!farm.isPresent() || farm.get().getId() != farmId) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy trang trại nào!");
        }
        farmRepository.save(
            farm
                .map(
                    farm1 -> {
                        farm1.setName(farmDTO.getName());
                        return farm1;
                    }
                )
                .get()
        );

        farmDTO.setId(farmId);
        farmDTO.setOwner(createdUser);

        return farmDTO;
    }

    @Override
    public Page<FarmDTO> getFarms(Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        return farmRepository
            .findAll(pageable)
            .map(
                farm -> {
                    FarmDTO newFarm = new FarmDTO();
                    newFarm.setId((farm.getId()));
                    newFarm.setName(farm.getName());
                    newFarm.setOwner(farm.getUser().getFirstName() + " " + farm.getUser().getLastName());
                    newFarm.setEmail(farm.getUser().getEmail());
                    newFarm.setStatus(farm.getStatus());
                    newFarm.setCreatedDate(Date.from(farm.getCreatedDate()));
                    return newFarm;
                }
            );
    }

    @Override
    public Page<FarmDTO> search(SearchDTO searchDTO, Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        Specification<Farm> specification = FarmSpectificationBuilder.buildQuery(searchDTO);
        return farmRepository
            .findAll(specification, pageable)
            .map(
                farm -> {
                    FarmDTO newFarm = new FarmDTO();
                    newFarm.setId((farm.getId()));
                    newFarm.setName(farm.getName());
                    newFarm.setOwner(farm.getUser().getFirstName() + " " + farm.getUser().getLastName());
                    newFarm.setEmail(farm.getUser().getEmail());
                    newFarm.setCreatedDate(Date.from(farm.getCreatedDate()));
                    newFarm.setStatus(farm.getStatus());
                    return newFarm;
                }
            );
    }

    @Override
    public Optional<FarmDTO> getFarm(Long farmId) {
        Farm farm = getFarmById(farmId);
        return Optional.of(new FarmDTO(farm));
    }

    @Override
    @Transactional
    public void updateFarm(FarmDTO farmDTO) {
        if (farmDTO.getId() == null) {
            throw new IllegalArgumentException("Farm ID cannot be null");
        }
        Farm farm = getFarmById(farmDTO.getId());
        farm.setName(farmDTO.getName() != null ? farmDTO.getName() : farm.getName());
        farm.setStatus(farmDTO.getStatus());
        farmRepository.save(farm);
    }

    @Override
    @Transactional
    public void deleteFarm(Long farmId) {
        Farm farm = farmRepository.findById(farmId).orElseThrow(() -> new BaseException(400, "Không tìm thấy trang trại"));

        checkFarmStatus(farm);

        farm.setDeleted(true);
        farmRepository.save(farm);
    }

    @Override
    public void deleteFarms(Set<Long> ids) {
        List<Farm> farms = farmRepository.findFarmByIdIn(ids);
        if (farms.isEmpty()) {
            throw new BaseException(400, "Không tìm thấy trang trại");
        }
        farms.forEach(
            farm -> {
                checkFarmStatus(farm);
                farm.setDeleted(Boolean.TRUE);
            }
        );
        farmRepository.saveAll(farms);
    }

    private static void checkFarmStatus(Farm farm) {
        if (farm.getStatus().equals(ApproveStatus.APPROVED)) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "Không thể xóa trang trại đang hoạt động");
        }
    }

    private Farm getFarmById(Long id) {
        return farmRepository.findById(id).orElseThrow(() -> new EmailAlreadyUsedException("Không tìm thấy trang trại"));
    }
}
