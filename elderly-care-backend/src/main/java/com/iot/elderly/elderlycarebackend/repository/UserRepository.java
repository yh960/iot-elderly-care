package com.iot.elderly.elderlycarebackend.repository;

import com.iot.elderly.elderlycarebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 用户仓库 —— 操作 user 表的数据访问层
 *
 * 方法说明：
 * - existsByUsername: 判断用户名是否已存在（注册时做重复校验）
 * - findByUsername: 根据用户名查询用户（登录时用）
 * - findByOpenid: 【废案】根据微信 openid 查询用户，返回 Optional<User>
 *   这是微信小程序登录时用的方法，项目放弃微信小程序后不再使用
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    Optional<User> findByOpenid(String openid);  // 返回 Optional<User>，支持orElseGet
}
