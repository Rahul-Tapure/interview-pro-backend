package com.interviewpro.interviewpro.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewpro.interviewpro.auth.entity.UserTable;

public interface UserRepository extends JpaRepository<UserTable, String> {

    Optional<UserTable> findByEmail(String email);

}