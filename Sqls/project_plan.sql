CREATE TABLE `tab_project_plan` (
                                    `project_plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划项唯一ID',
                                    `project_id` bigint NOT NULL COMMENT '关联项目ID',
                                    `task_package` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务包',
                                    `task_description` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务内容',
                                    `start_date` date NOT NULL COMMENT '开始时间',
                                    `end_date` date NOT NULL COMMENT '结束时间',
                                    `responsible_person` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '责任人',
                                    `department` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '科室',
                                    `deliverable` varchar(255) COLLATE utf8mb4_general_ci NULL COMMENT '成果', -- 修改为NULLABLE
                                    `deliverable_type` varchar(50) COLLATE utf8mb4_general_ci NULL COMMENT '成果类型', -- 修改为NULLABLE
                                    `is_milestone` tinyint(1) DEFAULT '0' COMMENT '是否里程碑任务',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `task_status` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务状态',
                                    `real_start_date` date DEFAULT NULL COMMENT '实际开始时间',
                                    `real_end_date` date DEFAULT NULL COMMENT '实际结束时间',
                                    `phase_id` bigint DEFAULT NULL COMMENT '项目阶段ID',
                                    `is_top` tinyint(1) DEFAULT '0' NULL COMMENT '1是 0否', -- 添加DEFAULT '0'
                                    PRIMARY KEY (`project_plan_id`),
                                    KEY `fk_plan_project` (`project_id`),
                                    CONSTRAINT `fk_plan_project` FOREIGN KEY (`project_id`) REFERENCES `tab_project_info` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目计划表';