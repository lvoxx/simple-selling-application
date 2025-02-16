package com.shitcode.demo1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.SpringUser;

public interface SpringUserRepository extends JpaRepository<SpringUser, Long> {
    @Query("SELECT u FROM SpringUser u WHERE u.email = :email")
    Optional<SpringUser> findByEmail(@Param("email") String email);
}
