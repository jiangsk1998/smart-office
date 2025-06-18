CREATE TABLE `tab_user_role` (
                                 `user_role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色关系ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`user_role_id`),
                                 UNIQUE KEY `idx_user_role_unique` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关系表';