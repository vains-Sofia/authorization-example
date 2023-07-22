/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : authorization-example

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 05/07/2023 21:42:11
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth2_basic_user
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_basic_user`;
CREATE TABLE `oauth2_basic_user`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名、昵称',
    `account`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账号',
    `password`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
    `mobile`      varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
    `email`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
    `avatar_url`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '头像地址',
    `deleted`     tinyint(1) NULL DEFAULT NULL COMMENT '是否已删除',
    `source_from` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户来源',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '基础用户信息表';

-- ----------------------------
-- Records of oauth2_basic_user
-- ----------------------------
BEGIN;
INSERT INTO `oauth2_basic_user`
VALUES (1, '云逸', 'admin', '$2a$10$K7nVcC.75YZSZU1Fq6G6buYujG.dolGYGPboh7eQbtkdFmB0EfN5K', '17683906001',
        '17683906001@163.com', NULL, 0, 'system', '2023-06-20 15:20:42', '2023-06-20 15:20:42');
COMMIT;

-- ----------------------------
-- Table structure for sys_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单自增ID',
    `name`           varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '菜单名称',
    `menu_pid`       int(11) NOT NULL COMMENT '父菜单ID',
    `url`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '跳转URL',
    `authority`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '所需权限',
    `sort`           tinyint(4) NULL DEFAULT NULL COMMENT '排序',
    `type`           tinyint(4) NOT NULL COMMENT '0:菜单,1:接口',
    `deleted`        tinyint(1) NOT NULL COMMENT '0:启用,1:删除',
    `create_time`    datetime(0) NOT NULL COMMENT '创建时间',
    `create_user_id` int(11) NOT NULL COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统菜单表';

-- ----------------------------
-- Records of sys_authority
-- ----------------------------
BEGIN;
INSERT INTO `sys_authority`
VALUES (1, '系统管理', 0, '/system', 'system', 0, 0, 0, '2022-03-25 23:52:03', 1),
       (2, 'app', 0, '/**', 'app', 1, 1, 0, '2023-06-20 15:18:49', 1),
       (3, 'web', 0, '/**', 'web', 2, 1, 0, '2023-06-20 15:19:12', 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT COMMENT '角色自增ID',
    `role_name`      varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
    `deleted`        tinyint(1) NULL DEFAULT NULL COMMENT '0:启用,1:删除',
    `sort`           int(11) NULL DEFAULT NULL COMMENT '排序',
    `create_time`    datetime(0) NOT NULL COMMENT '创建时间',
    `create_user_id` int(11) NOT NULL COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role`
VALUES (1, '管理员', 0, 0, '2022-03-25 23:51:04', 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_authority`;
CREATE TABLE `sys_role_authority`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '角色菜单关联表自增ID',
    `role_id`      int(16) NOT NULL COMMENT '角色ID',
    `authority_id` int(11) NOT NULL COMMENT '权限菜单ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色菜单多对多关联表';

-- ----------------------------
-- Records of sys_role_authority
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_authority`
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT,
    `role_id` int(16) NULL DEFAULT NULL COMMENT '角色ID',
    `user_id` int(18) NULL DEFAULT NULL COMMENT '用户ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role`
VALUES (1, 1, 1);
COMMIT;

SET
FOREIGN_KEY_CHECKS = 1;
