/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : localhost:3306
 Source Schema         : elderly_care

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 29/04/2026 16:00:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert_log
-- ----------------------------
DROP TABLE IF EXISTS `alert_log`;
CREATE TABLE `alert_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `alert_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING',
  `ai_analysis_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alert_log
-- ----------------------------
INSERT INTO `alert_log` VALUES (5, '1112', 7, 'SITTING', 'RESOLVED', 'normal', '2026-04-28 11:03:00.184000');
INSERT INTO `alert_log` VALUES (6, '111', 7, 'FALL', 'RESOLVED', '```json\n{\n  \"riskLevel\": \"HIGH\",\n  \"conclusion\": \"跌倒\",\n  \"reason\": \"根据跌倒风险判定标准，速度为10.00 m/s，超过高风险判定阈值3.0 m/s。虽然Y轴轨迹变化为0.00，没有低于垂直位移判定阈值-3.0，但X轴轨迹变化10.00表明可能发生了不正常的快速移动，这可能伴随着跌倒。\",\n  \"suggestion\": \"建议对这位老人的活动进行更加密切的监控，并在必要时提供辅助措施，以防万一发生跌倒。\"\n}\n```', '2026-04-29 02:42:10.460000');
INSERT INTO `alert_log` VALUES (7, '111', 7, 'FALL', 'RESOLVED', '```json\n{\n  \"riskLevel\": \"MEDIUM\",\n  \"conclusion\": \"需要关注\",\n  \"reason\": \"虽然速度没有达到高风险的标准，但Y轴轨迹变化为0.00，没有显示垂直位移，这可能导致跌倒检测的误判。需要进一步观察。\",\n  \"suggestion\": \"建议进行进一步观察和数据分析，结合其他传感器数据或视频监控以确认是否存在跌倒情况。同时，对于这位用户的日常活动应增加关注，尤其是在可能存在跌倒风险的情境下。\"\n}\n```', '2026-04-29 02:43:21.464000');
INSERT INTO `alert_log` VALUES (8, '1111', 7, 'FALL', 'RESOLVED', '```json\n{\n  \"riskLevel\": \"HIGH\",\n  \"conclusion\": \"跌倒\",\n  \"reason\": \"根据判断标准，速度为10.00 m/s，超过高风险阈值3.0 m/s，且Y轴轨迹变化为0.00，未达到垂直位移跌倒判定标准，但考虑到速度极高，可能存在跌倒风险。\",\n  \"suggestion\": \"建议加强监控，确保安全，必要时提供辅助设备或人员支持。\"\n}\n```', '2026-04-29 02:43:40.448000');
INSERT INTO `alert_log` VALUES (9, '111', 7, 'FALL', 'RESOLVED', '```json\n{\n  \"riskLevel\": \"MEDIUM\",\n  \"conclusion\": \"需要关注\",\n  \"reason\": \"根据提供的速度数据，10.00 m/s的速度属于高风险范围，表明移动速度很快。尽管Y轴轨迹变化为0.00，未达到垂直位移跌倒判定标准（< -3.0），但由于速度过快，存在跌倒的可能性。\",\n  \"suggestion\": \"建议监测此个体的活动情况，并考虑是否需要提供额外的安全保障措施，如扶手、坐椅或其他辅助设备，以降低跌倒风险。\"\n}\n```', '2026-04-29 02:49:32.097000');
INSERT INTO `alert_log` VALUES (10, '111', 7, 'FALL', 'RESOLVED', '```json\n{\n  \"riskLevel\": \"HIGH\",\n  \"conclusion\": \"跌倒\",\n  \"reason\": \"速度大于3.0 m/s，且Y轴轨迹变化为0.00，虽然Y轴变化没有达到跌倒标准，但高速度移动本身就是一个跌倒风险信号。\",\n  \"suggestion\": \"需要关注此人的活动情况，确保其安全。如果可能，应提供帮助或确保环境安全，避免跌倒事故发生。\"\n}\n```', '2026-04-29 02:50:13.680000');
INSERT INTO `alert_log` VALUES (11, '111', 7, 'FALL', 'RESOLVED', '{\"riskLevel\":\"HIGH\",\"conclusion\":\"跌倒\",\"reason\":\"速度(4.10 m/s)超过3.0 m/s阈值，或垂直位移(10.00 m)超过-3.0 m阈值，符合跌倒特征。\",\"suggestion\":\"立即通知家属和医护人员前往查看老人情况，检查是否受伤，并及时采取必要的医疗措施。\"}', '2026-04-29 04:32:31.082000');

-- ----------------------------
-- Table structure for edge_device
-- ----------------------------
DROP TABLE IF EXISTS `edge_device`;
CREATE TABLE `edge_device`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '位置',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'offline',
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_device_id`(`device_id` ASC) USING BTREE,
  UNIQUE INDEX `UK1kl1kh7t9wb1kr4fd9ex07fm8`(`device_id` ASC) USING BTREE,
  INDEX `fk_edge_device_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_edge_device_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edge_device
-- ----------------------------
INSERT INTO `edge_device` VALUES (15, '1112', '沙发', 'online', 7);
INSERT INTO `edge_device` VALUES (16, '1111', '卧室', 'online', 7);
INSERT INTO `edge_device` VALUES (18, '11123', '客厅', 'online', 7);
INSERT INTO `edge_device` VALUES (24, '111', '客厅', 'online', 7);

-- ----------------------------
-- Table structure for fall_event
-- ----------------------------
DROP TABLE IF EXISTS `fall_event`;
CREATE TABLE `fall_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `edge_device_id` bigint NOT NULL,
  `event_time` datetime NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_fall_event_device`(`edge_device_id` ASC) USING BTREE,
  CONSTRAINT `fk_fall_event_device` FOREIGN KEY (`edge_device_id`) REFERENCES `edge_device` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fall_event
-- ----------------------------

-- ----------------------------
-- Table structure for radar_data
-- ----------------------------
DROP TABLE IF EXISTS `radar_data`;
CREATE TABLE `radar_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` bigint NOT NULL,
  `timestamp` datetime NOT NULL,
  `raw_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `processed_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `speed` float NOT NULL,
  `trajectoryx` float NOT NULL,
  `trajectoryy` float NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_radar_data_device`(`device_id` ASC) USING BTREE,
  CONSTRAINT `fk_radar_data_device` FOREIGN KEY (`device_id`) REFERENCES `edge_device` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of radar_data
-- ----------------------------
INSERT INTO `radar_data` VALUES (12, 15, '2026-04-28 10:51:44', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 1, 0, 0);
INSERT INTO `radar_data` VALUES (13, 15, '2026-04-28 10:51:56', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 5, 0, 0);
INSERT INTO `radar_data` VALUES (14, 16, '2026-04-28 10:59:35', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 1, 0, 0);
INSERT INTO `radar_data` VALUES (15, 16, '2026-04-28 10:59:51', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 5, 10, 10);
INSERT INTO `radar_data` VALUES (16, 16, '2026-04-28 11:00:07', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 0, 10, 10);
INSERT INTO `radar_data` VALUES (17, 16, '2026-04-28 11:00:18', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 2.1, 10, 10);
INSERT INTO `radar_data` VALUES (18, 16, '2026-04-28 11:00:34', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 5, 10, 10);
INSERT INTO `radar_data` VALUES (19, 15, '2026-04-28 12:08:42', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 1, 0, 0);
INSERT INTO `radar_data` VALUES (21, 15, '2026-04-29 00:53:41', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 10, 0, 0);
INSERT INTO `radar_data` VALUES (44, 16, '2026-04-29 02:43:37', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 10, 10, 0);
INSERT INTO `radar_data` VALUES (52, 24, '2026-04-29 04:54:18', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 2, 0, 0);
INSERT INTO `radar_data` VALUES (53, 24, '2026-04-29 04:54:52', '{\"sensor\":\"radar\",\"data\":[1,2,3,4,5]}', NULL, 10, 10, 5);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户姓名',
  `family_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '家属电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `age` int NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'elderly/family/admin',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (7, 'csr', '18865479374', 'asdf', 66, NULL, '男', '$2a$10$09JKZqYIBh786T7tbWR8jOBq1bUNDiezEcG/lgjxfgtUU1lMzG1s6', '18865479374', 'ELDERLY', 'testuser', NULL);

SET FOREIGN_KEY_CHECKS = 1;
