package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.User;

/**
 * 用户服务接口 —— 定义用户信息的查询和修改操作
 *
 * 实现类：UserServiceImpl
 *
 * 方法说明：
 * - getUserById(id): 根据 ID 获取用户信息，不存在则抛异常
 * - updateUser(id, updatedUser): 修改用户信息（只更新非空字段，避免覆盖已有数据）
 */
public interface UserService {
    // 根据ID获取用户信息
    User getUserById(Long id);
    // 修改用户信息（如修改姓名、家属电话等）
    User updateUser(Long id, User updatedUser);
}
