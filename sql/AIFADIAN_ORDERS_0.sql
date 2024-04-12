/*
Navicat MySQL Data Transfer

Source Server         : hhhblog
Source Server Version : 80030
Source Host           : 175.178.152.24:3306
Source Database       : minecraft

Target Server Type    : MYSQL
Target Server Version : 80030
File Encoding         : 65001

Date: 2024-01-05 17:44:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for AIFADIAN_ORDERS_0
-- ----------------------------
DROP TABLE IF EXISTS `AIFADIAN_ORDERS_0`;
CREATE TABLE `AIFADIAN_ORDERS_0` (
  `out_trade_no` varchar(40) NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `user_id` varchar(40) DEFAULT NULL,
  `plan_title` varchar(40) DEFAULT NULL,
  `redeem_id` varchar(40) DEFAULT NULL,
  `price` varchar(40) DEFAULT NULL,
  `insert_time` bigint DEFAULT NULL,
  PRIMARY KEY (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
