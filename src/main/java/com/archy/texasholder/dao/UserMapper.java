package com.archy.texasholder.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.archy.texasholder.entity.User;

@Repository
public interface UserMapper extends JpaRepository<User, Integer> {

    Optional<User> findByAccount(String account);

    Optional<User> findByMobile(String mobile);

    default int insertSelective(User record) {
        return save(record) != null ? 1 : 0;
    }

    default User selectByPrimaryKey(Integer uid) {
        return findById(uid).orElse(null);
    }

    default User selectByAccount(String account) {
        return findByAccount(account).orElse(null);
    }

    default User selectByPhone(String phone) {
        return findByMobile(phone).orElse(null);
    }

    default int updateByPrimaryKeySelective(User record) {
        return save(record) != null ? 1 : 0;
    }
}
