drop table if exists `member`;
create table `member` (
                          `id` bigint not null auto_increment comment 'id',
                          `mobile` varchar(11) default null comment '手机号',
                          primary key (`id`),
                          unique key `mobile_unique` (`mobile`)
) engine = innodb default charset = utf8mb4 comment = '会员表';