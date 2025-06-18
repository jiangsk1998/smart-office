CREATE TABLE `tab_role` (
                            `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` varchar(50) NOT NULL COMMENT '角色名称',
                            `role_code` varchar(50) NOT NULL UNIQUE COMMENT '角色编码（唯一标识）',
                            `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- 插入预置角色
INSERT INTO `tab_role` (`role_name`, `role_code`, `description`) VALUES
                                                                     ('系统管理员', 'SYS_ADMIN', '拥有系统最高权限'),
                                                                     ('组织管理员', 'ORG_ADMIN', '管理其所属组织的资源'),
                                                                     ('普通用户', 'NORMAL_USER', '普通用户权限');