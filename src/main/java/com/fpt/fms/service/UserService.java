package com.fpt.fms.service;

import com.fpt.fms.domain.*;
import com.fpt.fms.fileUtils.FileUtils;
import com.fpt.fms.fileUtils.StringUtils;
import com.fpt.fms.repository.AuthorityRepository;
import com.fpt.fms.repository.FarmRepository;
import com.fpt.fms.repository.UserRepository;
import com.fpt.fms.security.AuthoritiesConstants;
import com.fpt.fms.security.SecurityUtils;
import com.fpt.fms.service.dto.AdminUserDTO;
import com.fpt.fms.service.dto.FarmDTO;
import com.fpt.fms.service.dto.UserDTO;
import com.fpt.fms.web.rest.errors.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.security.RandomUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final FileUtils fileUtils;
    private final FarmRepository farmRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserService(FileUtils fileUtils, FarmRepository farmRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.fileUtils = fileUtils;
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(
                user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    log.debug("Activated user: {}", user);
                    return user;
                }
            );
    }

    @Transactional
    public void activateRegistrationEmployee(String key, String password) {
        log.debug("Activating user for activation key {}", key);
        Optional<User> user = userRepository.findOneByActivationKey(key);
        if (!user.isPresent()) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "No employee was found for this activation key");
        }
        user.map(
            u -> {
                // activate given user for the registration key.
                u.setActivated(true);
                u.setActivationKey(null);
                u.setPassword(passwordEncoder.encode(password));
                log.debug("Activated user: {}", user);
                return user;
            }
        );
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(
                user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    return user;
                }
            );
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveImageCurUser(String currentUser, MultipartFile file){
       User user = userRepository.findOneByLogin(currentUser).orElseThrow(() -> {throw new BaseException(HttpStatus.FORBIDDEN.value(), "UnAuthorized");});
        try {
            File directory = resourceLoader.getResource("classpath:static/images/").getFile();
            String fileName = UUID.randomUUID().toString();
            File newFile = new File(directory, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            FileCopyUtils.copy(file.getInputStream(), fileOutputStream);
            fileOutputStream.close();

            user.setImageUrl(fileName);
            userRepository.save(user);
            return "Image saved to resources: " + newFile.getName();
        } catch (Exception e) {
            log.error("error save image " + e.getMessage());
            return "Failed to save the image";
        }
    }
    public UserDTO getUserDetail(String currentUser) {
        return userRepository.findOneByLogin(currentUser).map(user -> {
            UserDTO userDTO = new UserDTO(user);

            FarmRole farmRole = user.getFarmRole();

            if (farmRole != null) {
                Optional<Farm> farm = null;
                String owner = null;
                if (farmRole.equals(FarmRole.OWNER)) {
                    farm = farmRepository.findFarmByUserId(user.getId());
                } else {
                    farm = farmRepository.findFarmByCreatedBy(user.getCreatedBy());
                }

                if (farm.isPresent()) {
                    FarmDTO farmDTO = new FarmDTO();
                    farmDTO.setId(farm.get().getId());
                    farmDTO.setName(farm.get().getName());
                    farmDTO.setOwner(farm.get().getUser().getEmail());

                    userDTO.setFarmDTO(farmDTO);
                }
            }

            return userDTO;
        }).orElseThrow(() -> {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy thông tin người dùng hiện tại");
        });
    }


    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(
                user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    return user;
                }
            );
    }

    public User registerUser(AdminUserDTO userDTO, String password, boolean isEmployee) {
        List<User> users = userRepository.findAllByIdCard(userDTO.getIdCard());
        if (users != null && users.size() > 1) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "mã chứng minh đã tồn tại");
        }
        userRepository
            .findOneByLogin(userDTO.getEmail().toLowerCase())
            .ifPresent(
                existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                }
            );
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(
                existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                }
            );
        User newUser = new User();
        String encryptedPassword = null;
        String owner = null;
        if (isEmployee) {
            owner = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không tìm thấy chủ trang trại cho tài khoản này");
            });

            newUser.setOwnerId(userRepository.findOneByLogin(owner).get().getId());
            newUser.setFarmRole(FarmRole.EMPLOYEE);

            encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        } else {
            newUser.setFarmRole(FarmRole.OWNER);
            encryptedPassword = passwordEncoder.encode(password);
        }
        newUser.setImageUrl("static/images/user/default.png");
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setIdCard(userDTO.getIdCard());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());

        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        newUser.setFullName(StringUtils.isEmpty(userDTO.getFullName()) ? userDTO.getLastName() + " " + userDTO.getFirstName() : userDTO.getFullName());


        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(isEmployee ? AuthoritiesConstants.EMPLOYEE
            : AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        newUser.setOwner(owner);

        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);

        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }
//    public User createUser(AdminUserDTO userDTO) {
//        User user = new User();
//        user.setLogin(userDTO.getLogin().toLowerCase());
//        user.setFirstName(userDTO.getFirstName());
//        user.setLastName(userDTO.getLastName());
//        if (userDTO.getEmail() != null) {
//            user.setEmail(userDTO.getEmail().toLowerCase());
//        }
//        user.setImageUrl(userDTO.getImageUrl());
//        if (userDTO.getLangKey() == null) {
//            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
//        } else {
//            user.setLangKey(userDTO.getLangKey());
//        }
//        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
//        user.setPassword(encryptedPassword);
//        user.setResetKey(RandomUtil.generateResetKey());
//        user.setResetDate(Instant.now());
//        user.setActivated(true);
//        if (userDTO.getAuthorities() != null) {
//            Set<Authority> authorities = userDTO
//                .getAuthorities()
//                .stream()
//                .map(authorityRepository::findById)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toSet());
//            user.setAuthorities(authorities);
//        }
//        userRepository.save(user);
//        log.debug("Created Information for User: {}", user);
//        return user;
//    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
//    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
//        return Optional
//            .of(userRepository.findById(userDTO.getId()))
//            .filter(Optional::isPresent)
//            .map(Optional::get)
//            .map(
//                user -> {
//                    user.setLogin(userDTO.getLogin().toLowerCase());
//                    user.setFirstName(userDTO.getFirstName());
//                    user.setLastName(userDTO.getLastName());
//                    if (userDTO.getEmail() != null) {
//                        user.setEmail(userDTO.getEmail().toLowerCase());
//                    }
//                    user.setImageUrl(userDTO.getImageUrl());
//                    user.setActivated(userDTO.isActivated());
//                    user.setLangKey(userDTO.getLangKey());
//                    Set<Authority> managedAuthorities = user.getAuthorities();
//                    managedAuthorities.clear();
//                    userDTO
//                        .getAuthorities()
//                        .stream()
//                        .map(authorityRepository::findById)
//                        .filter(Optional::isPresent)
//                        .map(Optional::get)
//                        .forEach(managedAuthorities::add);
//                    log.debug("Changed Information for User: {}", user);
//                    return user;
//                }
//            )
//            .map(AdminUserDTO::new);
//    }

//    public void deleteUser(String login) {
//        userRepository
//            .findOneByLogin(login)
//            .ifPresent(
//                user -> {
//                    userRepository.delete(user);
//                    log.debug("Deleted User: {}", user);
//                }
//            );
//    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    if (email != null) {
                        user.setEmail(email.toLowerCase());
                    }
                    user.setImageUrl(imageUrl);
                    log.debug("Changed Information for User: {}", user);
                }
            );
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    log.debug("Changed password for User: {}", user);
                }
            );
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllUsers(AuthoritiesConstants.USER, pageable).map(UserDTO::new);
    }

    //    @Transactional(readOnly = true)
//    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
//        return userRepository.findOneWithAuthoritiesByLogin(login);
//    }
//
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByEmail);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(
                user -> {
                    log.debug("Deleting not activated user {}", user.getEmail());
                    userRepository.delete(user);
                }
            );
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void updateCurUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "không tìm thấy thông tin người dùng");
        });
        List<User> users = userRepository.findAllByIdCard(userDTO.getIdCard());
        if(users != null && users.size() > 1 ){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "căn cước công dân đã tồn tại");
        }
        user.setFirstName(userDTO.getFirstName());
        user.setFullName(userDTO.getFullName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setIdCard(userDTO.getIdCard());
    }
}
