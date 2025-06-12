CREATE TABLE `tab_operation_log` (
                                     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `operator_id` bigint NOT NULL COMMENT '操作人ID',
                                     `operator_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作人姓名',
                                     `operate_time` datetime NOT NULL COMMENT '操作时间',
                                     `operate_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
                                     `operate_target` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作对象',
                                     `target_id` bigint NOT NULL COMMENT '对象ID',
                                     `operate_detail` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作详情',
                                     `project_id` bigint NOT NULL COMMENT '关联项目ID',
                                     `original_data` json DEFAULT NULL COMMENT '原始数据',
                                     `new_data` json DEFAULT NULL COMMENT '新数据',
                                     `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作IP',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_project` (`project_id`),
                                     KEY `idx_target` (`operate_target`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';