package com.archy.texasholder.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archy.texasholder.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

    Optional<User> findByAccount(String account);

} 
