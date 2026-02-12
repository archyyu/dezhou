package com.archy.texasholder.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.archy.texasholder.entity.RoomDB;

@Repository
public interface RoomDBMapper extends JpaRepository<RoomDB, Integer> {

    Optional<RoomDB> findByName(String name);

    default int insertSelective(RoomDB record) {
        return save(record) != null ? 1 : 0;
    }

    default RoomDB selectByPrimaryKey(Integer id) {
        return findById(id).orElse(null);
    }

    default List<RoomDB> selectAllRooms() {
        return findAll();
    }

    default RoomDB selectByName(String name) {
        return findByName(name).orElse(null);
    }

    default int updateByPrimaryKeySelective(RoomDB record) {
        return save(record) != null ? 1 : 0;
    }

    default int deleteByPrimaryKey(Integer id) {
        if (existsById(id)) {
            deleteById(id);
            return 1;
        }
        return 0;
    }
}
