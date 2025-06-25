create table project_manager.tab_user_info
(
    user_id       bigint auto_increment comment '用户Id'
        primary key,
    user_account  varchar(50)   null comment '用户账号',
    user_name     varchar(50)   null comment '用户名称',
    user_password varchar(1024) null comment '用户密码',
    create_time   datetime      null comment '创建时间',
    update_time   datetime      null comment '更新时间',
    is_delete     int           null,
    department_id bigint        null comment '部门ID'
)
    comment '用户信息表';



create table project_manager.tab_project_info
(
    project_id            bigint auto_increment comment '项目唯一ID'
        primary key,
    project_number        varchar(50)                           not null comment '项目工号',
    project_name          varchar(255)                          not null comment '项目名称',
    department            varchar(50)                           not null comment '所属科室',
    start_date            date                                  not null comment '立项时间',
    end_date              date                                  not null comment '结束时间',
    responsible_leader_id bigint                                null comment '分管领导ID',
    technical_leader_id   bigint                                null comment '技术负责人ID',
    plan_supervisor_id    bigint                                null comment '计划主管ID',
    responsible_leader    varchar(50)                           null comment '分管领导姓名',
    technical_leader      varchar(50)                           null comment '技术负责人姓名',
    plan_supervisors      json                                  null comment '计划主管列表',
    project_participants  varchar(50)                           null comment '项目参与人',
    status                varchar(50) default 'NOT_STARTED'     null comment '项目状态（NOT_STARTED/IN_PROGRESS/COMPLETED/OVERDUE）',
    current_phase         varchar(50)                           null comment '项目当前阶段',
    is_favorite           tinyint(1)  default 0                 null comment '是否被收藏',
    creator_id            bigint                                null comment '创建人ID',
    creator_name          varchar(50)                           null comment '创建人姓名',
    create_time           datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    updater_id            bigint                                null comment '最后修改人ID',
    updater_name          varchar(50)                           null comment '最后修改人姓名',
    update_time           datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间'
)
    comment '项目信息表';


CREATE TABLE project_manager.tab_contract_node
(
    node_id              BIGINT AUTO_INCREMENT COMMENT '合同节点唯一ID' PRIMARY KEY,
    project_id           BIGINT       NOT NULL COMMENT '关联项目ID',
    contract_name        VARCHAR(255) NOT NULL COMMENT '合同名称',
    project_number       VARCHAR(50)  NOT NULL COMMENT '项目工号',
    contract_party       VARCHAR(100) NOT NULL COMMENT '合同甲方',
    planned_payment_date DATE         NOT NULL COMMENT '计划到款日期',
    payment_node_name    VARCHAR(255) NOT NULL COMMENT '到款节点名称',
    create_time          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    CONSTRAINT fk_contract_node_project FOREIGN KEY (project_id)
        REFERENCES project_manager.tab_project_info (project_id)
        ON DELETE CASCADE
) COMMENT '合同节点表';

CREATE TABLE project_manager.tab_payment_node
(
    payment_id          BIGINT AUTO_INCREMENT COMMENT '付款节点唯一ID' PRIMARY KEY,
    project_id          BIGINT         NOT NULL COMMENT '关联项目ID',
    project_number        VARCHAR(50)    NOT NULL COMMENT '项目工号',
    payment_node_name   VARCHAR(255)   NOT NULL COMMENT '到款节点名称',
    receivable          DECIMAL(18, 2) NOT NULL COMMENT '应收款（万元）',
    department_director VARCHAR(50) COMMENT '部室主管',
    invoice_status      VARCHAR(20) DEFAULT 'PENDING' COMMENT '开票状态（PENDING/WARNING/PROCESSING/COMPLETED）',
    payment_status      VARCHAR(20) DEFAULT 'PENDING' COMMENT '到款状态（PENDING/WARNING/PROCESSING/COMPLETED）',
    section_chief       VARCHAR(50) COMMENT '分管科长',
    department_leader   VARCHAR(50) COMMENT '分管部领导',
    client_stakeholder  VARCHAR(50) COMMENT '甲方干系人',
    contact_info        VARCHAR(20) COMMENT '联系方式',
    create_time         DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    CONSTRAINT fk_payment_node_project FOREIGN KEY (project_id)
        REFERENCES project_manager.tab_project_info (project_id)
        ON DELETE CASCADE
) COMMENT '项目付款节点表';

create table project_manager.tab_project_document
(
    project_document_id bigint auto_increment comment '文档唯一ID'
        primary key,
    project_id          bigint                                                                                                          not null comment '关联项目ID',
    document_type       enum ('项目款项', '项目合同', '项目计划', '图纸目录', '生产会材料', '汇报材料', '二次统计', '合并文档', '其他') not null comment '文档类型',
    document_name       varchar(255)                                                                                                    not null comment '文档名称',
    file_path           varchar(500)                                                                                                    not null comment '文件存储路径',
    file_size           bigint                                                                                                          not null comment '文件大小',
    uploader_id         bigint                                                                                                          null comment '上传人ID',
    uploader_name       varchar(50)                                                                                                     null comment '上传人姓名',
    upload_time         datetime   default CURRENT_TIMESTAMP                                                                            null comment '上传时间',
    version             varchar(50)                                                                                                     null comment '文档版本',
    description         text                                                                                                            null comment '文档描述',
    is_latest           tinyint(1) default 1                                                                                            null comment '是否最新版本',
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
    phase_status       varchar(50) default 'NOT_STARTED'     not null comment '阶段状态（NOT_STARTED/IN_PROGRESS/COMPLETED/STOP）',
    start_date         date                                  not null comment '开始时间',
    end_date           date                                  not null comment '结束时间',
    department         varchar(50)                           null comment '科室',
    responsible_person varchar(50)                           not null comment '负责人姓名',
    deliverable        varchar(255)                          null comment '阶段整体成果描述',
    deliverable_type   varchar(50)                           null comment '成果类型',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    sort               int                                   null comment '顺序码',
    constraint fk_phase_project
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade
)
    comment '项目阶段表';



create table project_manager.tab_project_plan
(
    project_plan_id    bigint auto_increment comment '计划项唯一ID'
        primary key,
    project_id         bigint                                not null comment '关联项目ID',
    task_package       varchar(255)                          not null comment '任务包',
    task_description   text                                  not null comment '任务内容',
    start_date         date                                  not null comment '开始时间',
    end_date           date                                  not null comment '结束时间',
    responsible_person varchar(50)                           not null comment '责任人',
    department         varchar(50)                           not null comment '科室',
    deliverable        varchar(255)                          null comment '成果',
    deliverable_type   varchar(50)                           null comment '成果类型',
    is_milestone       tinyint(1)  default 0                 null comment '是否里程碑任务',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    task_status        varchar(16) default 'NOT_STARTED'     null comment '任务状态（NOT_STARTED/IN_PROGRESS/COMPLETED/STOP）',
    real_start_date    date                                  null comment '实际开始时间',
    real_end_date      date                                  null comment '实际结束时间',
    phase_id           bigint                                null comment '项目阶段ID',
    is_top             tinyint(1)                            null comment '1是 0否',
    sort               int                                   null comment '顺序码',
    constraint fk_plan_project
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade
)
    comment '项目计划表';



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



create table project_manager.tab_project_favorite
(
    favorite_id bigint auto_increment comment '收藏唯一ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    project_id  bigint                             not null comment '项目ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '收藏时间',
    order_index int      default 0                 null comment '排序序号（用于自定义排序）',
    constraint uk_user_project
        unique (user_id, project_id),
    constraint fk_favorite_project
        foreign key (project_id) references project_manager.tab_project_info (project_id)
            on delete cascade,
    constraint fk_favorite_user
        foreign key (user_id) references project_manager.tab_user_info (user_id)
            on delete cascade
)
    comment '项目收藏表';



create table project_manager.tab_department
(
    id         bigint auto_increment comment '部门ID',
    name       varchar(100)                        not null comment '部门名称',
    parent_id  bigint                              null comment '上级部门ID，NULL表示根节点',
    dept_type  varchar(50)                         null comment '部门类型 (如: 分部, 部门分类, 职能科室, 专业科室)',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    primary key (id, name)
)
    comment '组织架构表' collate = utf8mb4_unicode_ci;

create index idx_parent_id
    on project_manager.tab_department (parent_id);


create table project_manager.tab_message_info
(
    message_id     bigint auto_increment comment '主键ID'
        primary key,
    sender_id      varchar(32)                        not null comment '发送者用户ID（字符串）',
    receiver_id    varchar(32)                        not null comment '接收者用户ID（字符串）',
    title          varchar(200)                       null comment '消息标题',
    content        json                               not null comment '消息内容（JSON结构）',
    message_type   tinyint  default 1                 not null comment '消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警',
    read_status    tinyint  default 0                 not null comment '阅读状态：0=未读，1=已读',
    is_top         tinyint  default 0                 not null comment '是否置顶：0=否，1=是',
    has_attachment tinyint  default 0                 not null comment '是否有附件：0=无，1=有',
    attachment     json                               null comment '附件',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    read_time      datetime                           null comment '阅读时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间',
    is_deleted     tinyint  default 0                 not null comment '逻辑删除标志：0=正常，1=删除'
)
    comment '消息表';

create table project_manager.tab_operation_log
(
    id             bigint unsigned auto_increment comment '主键ID'
        primary key,
    operator_id    bigint       not null comment '操作人ID',
    operator_name  varchar(50)  not null comment '操作人姓名',
    operate_time   datetime     not null comment '操作时间',
    operate_type   varchar(20)  not null comment '操作类型',
    operate_target varchar(50)  not null comment '操作对象',
    target_id      bigint       null comment '对象ID',
    operate_detail varchar(500) not null comment '操作详情',
    project_id     bigint       not null comment '关联项目ID',
    original_data  json         null comment '原始数据',
    new_data       json         null comment '新数据',
    ip_address     varchar(45)  null comment '操作IP'
)
    comment '操作日志表';

create index idx_project
    on project_manager.tab_operation_log (project_id);

create index idx_target
    on project_manager.tab_operation_log (operate_target, target_id);

create table project_manager.tab_old_drawing_plan
(
    old_drawing_plan_id int auto_increment
        primary key,
    drawing_plan_name   varchar(256) null comment '图纸计划名称',
    file_hash           varchar(120) null comment '图纸计划Hash',
    plan_start_date     date         null comment '计划开始日期',
    plan_end_date       date         null comment '计划结束日期'
)
    comment '历史图纸计划';



