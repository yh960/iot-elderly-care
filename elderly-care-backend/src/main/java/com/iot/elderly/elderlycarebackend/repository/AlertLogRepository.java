package com.iot.elderly.elderlycarebackend.repository;

import com.iot.elderly.elderlycarebackend.entity.AlertLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 告警日志仓库 —— 操作 alert_log 表的数据访问层
 *
 * 继承 JpaRepository<AlertLog, Long>，自动获得 CRUD 基本操作（save、findById、findAll、delete 等）。
 * 自定义查询方法通过 Spring Data JPA 的方法命名规则自动生成 SQL，不需要写 SQL 语句。
 *
 * 方法说明：
 * - findByUserIdOrderByCreateTimeDesc: 按用户 ID 查询，按创建时间倒序（最新的在前），支持分页
 * - findAllByOrderByCreateTimeDesc: 查询所有告警，按创建时间倒序，支持分页
 */
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {

    // 根据老人ID分页倒序查询报警历史
    Page<AlertLog> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    // 查询所有告警历史（分页倒序）
    Page<AlertLog> findAllByOrderByCreateTimeDesc(Pageable pageable);

}
