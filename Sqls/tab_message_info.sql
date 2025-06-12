CREATE TABLE `tab_message_info` (
                                    `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `sender_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送者用户ID（字符串）',
                                    `receiver_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收者用户ID（字符串）',
                                    `title` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息标题',
                                    `content` json NOT NULL COMMENT '消息内容（JSON结构）',
                                    `message_type` tinyint NOT NULL DEFAULT '1' COMMENT '消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警',
                                    `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0=未读，1=已读',
                                    `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0=否，1=是',
                                    `has_attachment` tinyint NOT NULL DEFAULT '0' COMMENT '是否有附件：0=无，1=有',
                                    `attachment` json DEFAULT NULL COMMENT '附件',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0=正常，1=删除',
                                    PRIMARY KEY (`message_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息表';