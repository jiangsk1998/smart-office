CREATE TABLE tab_user_info
(
    user_id       BIGINT   NOT null comment '用户Id'
        primary key,
    user_account  INT      null comment '用户账号',
    user_password INT      null comment '用户密码',
    create_time   DATETIME null comment '创建时间',
    update_time   DATETIME null comment '更新时间',
    is_delete     INT      null
) comment '用户信息表';

CREATE TABLE tab_project_info
(
    project_id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目唯一ID',
    project_number        VARCHAR(50)  NOT NULL COMMENT '项目工号',
    project_name          VARCHAR(255) NOT NULL COMMENT '项目名称',
    department            VARCHAR(50)  NOT NULL COMMENT '所属科室',
    start_date            DATE         NOT NULL COMMENT '立项时间',
    end_date              DATE         NOT NULL COMMENT '结束时间',
    responsible_leader_id BIGINT COMMENT '分管领导ID',
    technical_leader_id   BIGINT COMMENT '技术负责人ID',
    plan_supervisor_id    BIGINT COMMENT '计划主管ID',
    responsible_leader    VARCHAR(50) COMMENT '分管领导姓名',
    technical_leader      VARCHAR(50) COMMENT '技术负责人姓名',
    plan_supervisor       VARCHAR(50) COMMENT '计划主管姓名',
    status                VARCHAR(50) COMMENT '项目状态',
    current_phase         VARCHAR(50) COMMENT '项目当前阶段',
    creator_id            BIGINT COMMENT '创建人ID',
    creator_name          VARCHAR(50) COMMENT '创建人姓名',
    create_time           DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater_id            BIGINT COMMENT '最后修改人ID',
    updater_name          VARCHAR(50) COMMENT '最后修改人姓名',
    update_time           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT ='项目信息表';


CREATE TABLE tab_project_document
(
    project_document_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文档唯一ID',
    project_id          BIGINT       NOT NULL COMMENT '关联项目ID',
    document_type       ENUM (
        '项目计划',
        '图纸目录',
        '生产会材料',
        '汇报材料',
        '二次统计',
        '合并文档',
        '其他'
        )                            NOT NULL COMMENT '文档类型',
    document_name       VARCHAR(255) NOT NULL COMMENT '文档名称',
    file_path           VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    uploader_id         BIGINT COMMENT '上传人ID',
    uploader_name       VARCHAR(50) COMMENT '上传人姓名',
    upload_time         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    version             VARCHAR(50) COMMENT '文档版本',
    description         TEXT COMMENT '文档描述',
    is_latest           BOOLEAN  DEFAULT TRUE COMMENT '是否最新版本',

    CONSTRAINT fk_project_doc FOREIGN KEY (project_id)
        REFERENCES tab_project_info (project_id) ON DELETE CASCADE
) COMMENT ='项目文档存储表';

CREATE TABLE tab_project_phase
(
    phase_id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '阶段唯一ID',
    project_id         BIGINT       NOT NULL COMMENT '关联项目ID',
    phase_name         VARCHAR(100) NOT NULL COMMENT '阶段名称',
    phase_status       VARCHAR(50)  NOT NULL DEFAULT '未开始' COMMENT '阶段状态（未开始/进行中/已完成/已延期/已取消）',
    start_date         DATE         NOT NULL COMMENT '开始时间',
    end_date           DATE         NOT NULL COMMENT '结束时间',
    responsible_person VARCHAR(50)  NOT NULL COMMENT '负责人姓名',
    deliverable        VARCHAR(255) COMMENT '阶段整体成果描述',
    deliverable_type   VARCHAR(50) COMMENT '成果类型',
    create_time        DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

    CONSTRAINT fk_phase_project FOREIGN KEY (project_id)
        REFERENCES tab_project_info (project_id) ON DELETE CASCADE
) COMMENT '项目阶段表';

CREATE TABLE tab_project_plan
(
    project_plan_id    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '计划项唯一ID',
    project_id         BIGINT       NOT NULL COMMENT '关联项目ID',
    task_order         INT          NOT NULL COMMENT '任务序号',
    task_package       VARCHAR(255) NOT NULL COMMENT '任务包',
    task_description   TEXT         NOT NULL COMMENT '任务内容',
    start_date         DATE         NOT NULL COMMENT '开始时间',
    end_date           DATE         NOT NULL COMMENT '结束时间',
    responsible_person VARCHAR(50)  NOT NULL COMMENT '责任人',
    department         VARCHAR(50)  NOT NULL COMMENT '科室',
    deliverable        VARCHAR(255) NOT NULL COMMENT '成果',
    deliverable_type   VARCHAR(50)  NOT NULL COMMENT '成果类型',
    is_milestone       BOOLEAN  DEFAULT FALSE COMMENT '是否里程碑任务',
    create_time        DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

    CONSTRAINT fk_plan_project FOREIGN KEY (project_id)
        REFERENCES tab_project_info (project_id) ON DELETE CASCADE

) COMMENT '项目计划表';

CREATE TABLE tab_drawing_plan
(
    drawing_plan_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图纸唯一ID',
    project_id      BIGINT       NOT NULL COMMENT '关联项目ID',
    drawing_number  VARCHAR(50)  NOT NULL COMMENT '图号',
    drawing_name    VARCHAR(255) NOT NULL COMMENT '图纸名称',
    approval_flow   VARCHAR(255) NOT NULL COMMENT '审签流程',
    completion_date DATE         NOT NULL COMMENT '完成时间',
    department      VARCHAR(50)  NOT NULL COMMENT '部门',
    security_level  VARCHAR(50)  NOT NULL COMMENT '密级',

    -- 关联约束
    CONSTRAINT fk_drawing_project FOREIGN KEY (project_id)
        REFERENCES tab_project_info (project_id) ON DELETE CASCADE

) COMMENT '项目图纸计划表';

CREATE TABLE `tab_message_info` (
                                    `message_id` VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
                                    `sender_id` VARCHAR(32) NOT NULL COMMENT '发送者用户ID（字符串）',
                                    `receiver_id` VARCHAR(32) NOT NULL COMMENT '接收者用户ID（字符串）',
                                    `title` VARCHAR(200) DEFAULT NULL COMMENT '消息标题',
                                    `content` JSON NOT NULL COMMENT '消息内容（JSON结构）',
                                    `message_type` TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型：0=附件通知，1=变更通知，2=即将到期通知，3=延期通知,4=延期反馈，5=延期风险告警',
                                    `read_status` TINYINT NOT NULL DEFAULT 0 COMMENT '阅读状态：0=未读，1=已读',
                                    `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
                                    `has_attachment` TINYINT NOT NULL DEFAULT 0 COMMENT '是否有附件：0=无，1=有',
                                    `attachment` JSON DEFAULT NULL COMMENT '附件',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0=正常，1=删除'
) COMMENT='消息表';

-- 添加全文索引
ALTER TABLE tab_message_info ADD FULLTEXT(title, content);

