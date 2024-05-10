package com.fpt.fms.web.rest;

import com.fpt.fms.domain.User;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.UserService;
import com.fpt.fms.service.dto.UserDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api/user")
public class UserResource {
    private final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final UserService userService;

    @Autowired
    ResourceLoader getResourceLoader;
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.EMPLOYEE + "\", \"" + AuthoritiesConstants.USER + "\")")
    public UserDTO getUser() {
        log.debug("REST request to get User : {}");
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> {
            throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
        });
        return userService.getUserDetail(curUser);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.EMPLOYEE + "\", \"" + AuthoritiesConstants.USER + "\")")
    public void saveImageUser(@RequestParam("file") MultipartFile file) {
        log.debug("REST request to get User : {}");
        String curUser = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> {
            throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "không tìm thấy thông tin người dùng trong hệ thống");
        });
        userService.saveImageCurUser(curUser, file);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.EMPLOYEE + "\", \"" + AuthoritiesConstants.USER + "\")")
    public void updateCurrentUser(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        userService.updateCurUser(userId, userDTO);
    }

    @PatchMapping("/upload-image")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.EMPLOYEE + "\", \"" + AuthoritiesConstants.USER + "\")")
    public void uploadImageUser(@RequestParam MultipartFile file){
//        file.
    }
}
