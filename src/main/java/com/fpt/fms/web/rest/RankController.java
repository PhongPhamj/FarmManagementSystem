package com.fpt.fms.web.rest;

import com.fpt.fms.config.Constants;
import com.fpt.fms.service.baseservice.IRankService;
import com.fpt.fms.service.dto.RankDTO;
import com.fpt.fms.service.search.SearchRankDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    @Value("fms")
    private String applicationName;

    private final IRankService rankService;

    public RankController(IRankService rankService) {
        this.rankService = rankService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<RankDTO>> getListRank() {
        List<RankDTO> rankDtoList = rankService.getAllRank();
        return new ResponseEntity<>(rankDtoList, HttpStatus.OK);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<RankDTO>> getListRankWithStatus() {
        List<RankDTO> rankDtoList = rankService.getAllRankWithSratusTrue();
        return new ResponseEntity<>(rankDtoList, HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<List<RankDTO>> getSearchRanks(@RequestBody(required = false) SearchRankDTO searchDTO) {
        List<RankDTO> rankDTOList = rankService.getSearchRank(searchDTO);
        return new ResponseEntity<>(rankDTOList, HttpStatus.OK);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<String> registerRank(@Valid @RequestBody RankDTO rankDTO) {
        rankService.registerRank(rankDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Đăng kí xếp hạng thành công!");
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> updateTask(@Valid @RequestBody RankDTO rankDTO) {
        rankService.updateRank(rankDTO);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Cập nhập thành công", applicationName).build();
    }

    @GetMapping("/{rankId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<RankDTO> getRank(@PathVariable(name = "rankId") Long rankId) {
        Optional<RankDTO> rankDTOOptional = Optional.ofNullable(rankService.getRank(rankId));
        return rankDTOOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{rankId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> deleteRank(@PathVariable(name = "rankId") Long rankId) {
        rankService.deleteRank(rankId);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_USER')")
    public ResponseEntity<Void> deleteRanks(@RequestParam(name = "ids") String idInString) {
        Set<Long> ids = Arrays
            .stream(idInString.split(Constants.SYMBOL_COMMA))
            .map(String::trim)
            .filter(NumberUtils::isCreatable)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        rankService.deleteRanks(ids);
        return ResponseEntity.status(HttpStatus.OK).header(applicationName, "Xóa thành công", applicationName).build();
    }
}
