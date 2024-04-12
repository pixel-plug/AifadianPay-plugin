/*
Navicat MySQL Data Transfer

Source Server         : hhhblog
Source Server Version : 80030
Source Host           : 175.178.152.24:3306
Source Database       : minecraft

Target Server Type    : MYSQL
Target Server Version : 80030
File Encoding         : 65001

Date: 2024-01-05 17:44:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for AIFADIAN_SKUDETAIL_0
-- ----------------------------
DROP TABLE IF EXISTS `AIFADIAN_SKUDETAIL_0`;
CREATE TABLE `AIFADIAN_SKUDETAIL_0` (
  `out_trade_no` varchar(40) DEFAULT NULL,
  `sku_id` varchar(40) DEFAULT NULL,
  `price` varchar(40) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `count` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
