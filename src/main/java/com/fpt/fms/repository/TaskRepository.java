package com.fpt.fms.repository;

import com.fpt.fms.domain.Plant;
import com.fpt.fms.domain.Task;
import com.fpt.fms.domain.Tool;
import com.fpt.fms.domain.User;

import java.util.*;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByUserId(Long userId);

    List<Task> findTaskByIdIn(Set<Long> ids);

    List<Task> findAllByUserIn(List<User> users);

    List<Task> findAllByUser(User user);

    List<Task> findByCreatedBy(String email);
    Optional<Task> findByIdAndCreatedBy(Long aLong,String email);

    List<Task> findTaskByIdInAndCreatedBy(Set<Long> ids,String email);


}
