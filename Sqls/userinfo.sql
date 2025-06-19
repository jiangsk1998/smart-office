CREATE TABLE `tab_user_info` (
                                 `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户Id',
                                 `user_account` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户账号',
                                 `user_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名称',
                                 `user_password` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户密码',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `is_delete` int DEFAULT NULL,
                                 `department_id` bigint DEFAULT NULL COMMENT '部门ID',
                                 PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';