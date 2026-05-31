package com.archy.texasholder.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archy.texasholder.entity.GameActionDB;

public interface GameActionDBRepository extends JpaRepository<GameActionDB, Integer>{
    
}
