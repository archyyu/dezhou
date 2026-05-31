package com.archy.texasholder.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archy.texasholder.entity.RoomDB;

public interface RoomDBRepository extends JpaRepository<RoomDB, Integer> {
    
}
