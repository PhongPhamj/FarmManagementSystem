package com.fpt.fms.repository;

import com.fpt.fms.domain.Farm;
import com.fpt.fms.domain.FarmRole;
import com.fpt.fms.domain.User;
import com.fpt.fms.security.AuthoritiesConstants;
import io.grpc.netty.shaded.io.netty.util.AsyncMapping;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findAllByIdCard(String idCard);

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @Query(value = "select * from users where email = :login", nativeQuery = true)
    Optional<User> findOneByLogin(@Param("login") String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    @Query(
        value = "select u.* from users u, user_authority a where u.activation_key is null and a.authority_name = :roleName",
        nativeQuery = true
    )
    Page<User> findAllUsers(String roleName, Pageable pageable);

    @Query(value = "select u.* from users u where u.created_by = :createdBy and activation_key is null", nativeQuery = true)
    Page<User> findAllEmployeeByFarmId(@Param("createdBy") String createdBy, Pageable pageable);

    List<User> findUserByIdIn(Set<Long> ids);

    User findByFullNameAndCreatedBy(String fullName,String eString);

    @Query(value = "SELECT u FROM User u WHERE u.createdBy = :createdBy or u.id =:id")
    List<User> findAllByCreatedByOrId(@Param("createdBy") String createdBy,@Param("id") Long id);

    Optional<User> findUserByIdAndCreatedBy(Long id, String email);

    List<User> findAllByIdInAndCreatedBy(Set<Long> ids, String email);

    List<User> findAllByIdIn(Set<Long> ids);

    List<User> findAllByCreatedByAndActivatedTrue(String createdBy);

    @Query(value = "SELECT u FROM User u WHERE u.createdBy = :createdBy and u.farmRole = :farmRole")
    Page<User> findAllByCreatedBy(@Param("createdBy") String createdBy, @Param("farmRole") FarmRole farmRole, Pageable pageable);
}
