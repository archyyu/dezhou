package com.archy.texasholder.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archy.texasholder.entity.GameRoomDB;

public interface GameRoomDBRepository extends JpaRepository<GameRoomDB, Integer>{
    
}
