package com.archy.texasholder.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archy.texasholder.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    


} 
