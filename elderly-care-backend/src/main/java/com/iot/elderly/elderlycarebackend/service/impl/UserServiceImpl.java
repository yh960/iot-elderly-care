package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现 —— 用户信息的查询和部分字段更新
 *
 * updateUser 方法使用了"部分更新"策略：
 * 只更新前端传入的非空字段，避免把已有数据覆盖为 null。
 * 例如：用户只修改了 familyPhone，其他字段传 null，则只更新 familyPhone。
 *
 * @Transactional: 开启数据库事务，保证 updateUser 中的查询和更新在同一事务内，
 * 避免并发场景下出现数据不一致的问题。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getFamilyPhone() != null) {
            existingUser.setFamilyPhone(updatedUser.getFamilyPhone());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getAge() != null) {
            existingUser.setAge(updatedUser.getAge());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getGender() != null) {
            existingUser.setGender(updatedUser.getGender());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }

        return userRepository.save(existingUser);
    }
}
