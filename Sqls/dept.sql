-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `departments` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                               `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
                               `parent_id` BIGINT NULL DEFAULT NULL COMMENT '上级部门ID，NULL表示根节点',
                               `dept_type` VARCHAR(50) NULL DEFAULT NULL COMMENT '部门类型 (如: 分部, 部门分类, 职能科室, 专业科室)',
                               `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织架构表';

-- ----------------------------
-- Records of departments
-- ----------------------------
-- 清空旧数据
DELETE FROM `department`;

-- 重置自增ID
ALTER TABLE `department` AUTO_INCREMENT = 1;

-- 插入数据
INSERT INTO `department` (`id`, `name`, `parent_id`, `dept_type`) VALUES
-- 根节点 (L0)
(1, '上海分部', NULL, '分部'),

-- 一级部门 (L1)
(2, '职能部门', 1, '部门分类'),
(3, '专业科', 1, '部门分类'),

-- 二级部门 - 职能部门下属 (L2)
(4, '办公室', 2, '职能科室'),
(5, '计划经营科', 2, '职能科室'),
(6, '技术质量科', 2, '职能科室'),
(7, '信息档案科', 2, '职能科室'),

-- 二级部门 - 专业科下属 (L2)
(8, '总体科', 3, '专业科室'),
(9, '船体舾装科', 3, '专业科室'),
(10, '动力科', 3, '专业科室'),
(11, '系统科', 3, '专业科室'),
(12, '电气科', 3, '专业科室'),
(13, '作战系统科', 3, '专业科室'),
(14, '综合保障科', 3, '专业科室'),
(15, '智能系统科', 3, '专业科室');