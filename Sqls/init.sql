create table project_manager.tab_user_info
(
    user_id       bigint   not null comment '用户Id'
        primary key,
    user_account  varchar(50)       null comment '用户账号',
    user_name  varchar(50)       null comment '用户名称',
    user_password varchar(50)       null comment '用户密码',
    create_time   datetime null comment '创建时间',
    update_time   datetime null comment '更新时间',
    is_delete     int      null
)
    comment '用户信息表';



create table project_manager.tab_project_info
(
    project_id            bigint auto_increment comment '项目唯一ID'
        primary key,
    project_number        varchar(50)                        not null comment '项目工号',
    project_name          varchar(255)                       not null comment '项目名称',
    department            varchar(50)                        not null comment '所属科室',
    start_date            date                               not null comment '立项时间',
    end_date              date                               not null comment '结束时间',
    responsible_leader_id bigint                             null comment '分管领导ID',
    technical_leader_id   bigint                             null comment '技术负责人ID',
    plan_supervisor_id    bigint                             null comment '计划主管ID',
    responsible_leader    varchar(50)                        null comment '分管领导姓名',
    technical_leader      varchar(50)                        null comment '技术负责人姓名',
    plan_supervisors      json                        null comment '计划主管列表',
    project_participants  VARCHAR(50) COMMENT '项目参与人',
    status                varchar(50)                        null comment '项目状态',
    current_phase         varchar(50)                        null comment '项目当前阶段',
    is_favorite           BOOLEAN DEFAULT FALSE COMMENT '是否被收藏',
    creator_id            bigint                             null comment '创建人ID',
    creator_name          varchar(50)                        null comment '创建人姓名',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updater_id            bigint                             null comment '最后修改人ID',
    updater_name          varchar(50)                        null comment '最后修改人姓名',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间'
)
    comment '项目信息表';




create table project_manager.tab_project_document
(
    project_document_id bigint auto_increment comment '文档唯一ID'
        primary key,
    project_id          bigint                                                                                  not null comment '关联项目ID',
    document_type       enum ('项目计划', '图纸目录', '生产会材料', '汇报材料', '二次统计', '合并文档', '其他') not null comment '文档类型',
    document_name       varchar(255)                                                                            not null comment '文档名称',
    file_path           varchar(500)                                                                            not null comment '文件存储路径',
    uploader_id         bigint                                                                                  null comment '上传人ID',
    uploader_name       varchar(50)                                                                             null comment '上传人姓名',
    upload_time         datetime   default CURRENT_TIMESTAMP                                                    null comment '上传时间',
    version             varchar(50)                                                                             null comment '文档版本',
    description         text                                                                                    null comment '文档描述',
    is_latest           tinyint(1) default 1                                                                    null comment '是否最新版本',
    constraint fk_project_doc
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade
)
    comment '项目文档存储表';



create table project_manager.tab_project_phase
(
    phase_id           bigint auto_increment comment '阶段唯一ID'
        primary key,
    project_id         bigint                                not null comment '关联项目ID',
    phase_name         varchar(100)                          not null comment '阶段名称',
    phase_status       varchar(50) default '未开始'          not null comment '阶段状态（未开始/进行中/已完成/已延期/已取消）',
    start_date         date                                  not null comment '开始时间',
    end_date           date                                  not null comment '结束时间',
    responsible_person varchar(50)                           not null comment '负责人姓名',
    deliverable        varchar(255)                          null comment '阶段整体成果描述',
    deliverable_type   varchar(50)                           null comment '成果类型',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint fk_phase_project
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade
)
    comment '项目阶段表';



CREATE TABLE `tab_project_plan` (
                                    `project_plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划项唯一ID',
                                    `project_id` bigint NOT NULL COMMENT '关联项目ID',
                                    `task_package` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务包',
                                    `task_description` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务内容',
                                    `start_date` date NOT NULL COMMENT '开始时间',
                                    `end_date` date NOT NULL COMMENT '结束时间',
                                    `responsible_person` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '责任人',
                                    `department` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '科室',
                                    `deliverable` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '成果',
                                    `deliverable_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '成果类型',
                                    `is_milestone` tinyint(1) DEFAULT '0' COMMENT '是否里程碑任务',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `task_status` varchar(16) COLLATE utf8mb4_general_ci DEFAULT '未开始' COMMENT '任务状态',
                                    `real_start_date` date DEFAULT NULL COMMENT '实际开始时间',
                                    `real_end_date` date DEFAULT NULL COMMENT '实际结束时间',
                                    PRIMARY KEY (`project_plan_id`),
                                    KEY `fk_plan_project` (`project_id`),
                                    CONSTRAINT `fk_plan_project` FOREIGN KEY (`project_id`) REFERENCES `tab_project_info` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目计划表';




create table project_manager.tab_drawing_plan
(
    drawing_plan_id bigint auto_increment comment '图纸唯一ID'
        primary key,
    project_id      bigint       not null comment '关联项目ID',
    drawing_number  varchar(50)  not null comment '图号',
    drawing_name    varchar(255) not null comment '图纸名称',
    approval_flow   varchar(255) not null comment '审签流程',
    completion_date date         not null comment '完成时间',
    department      varchar(50)  not null comment '部门',
    security_level  varchar(50)  not null comment '密级',
    constraint fk_drawing_project
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade
)
    comment '项目图纸计划表';


CREATE TABLE tab_project_favorite
(
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏唯一ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    project_id  BIGINT NOT NULL COMMENT '项目ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    order_index INT      DEFAULT 0 COMMENT '排序序号（用于自定义排序）',

    UNIQUE KEY uk_user_project (user_id, project_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id)
        REFERENCES tab_user_info (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_project FOREIGN KEY (project_id)
        REFERENCES tab_project_info (project_id) ON DELETE CASCADE
) COMMENT '项目收藏表';


