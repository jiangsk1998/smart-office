create table tab_user_info
(
    user_id       int      NOT null comment '用户Id'
        primary key,
    user_account  int       null comment '用户账号',
    user_password int       null comment '用户密码',
    create_time   timestamp null comment '创建时间',
    update_time   timestamp null comment '更新时间',
    is_delete     int       null
)
    comment '用户信息表';


