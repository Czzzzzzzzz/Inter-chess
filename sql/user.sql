/*
Navicat MySQL Data Transfer

Source Server         : 18 tomcat
Source Server Version : 50723
Source Host           : 172.16.71.172:3306
Source Database       : user

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-12-22 15:34:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(20) NOT NULL,
  `password` varchar(20) DEFAULT NULL,
  `level` varchar(20) DEFAULT '1' COMMENT '用户等级',
  `score` varchar(20) DEFAULT '0' COMMENT '用户的积分',
  `status` varchar(1) DEFAULT '0' COMMENT '用户状态',
  `vip` varchar(1) DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
