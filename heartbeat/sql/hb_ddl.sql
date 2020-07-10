drop table `hb_server_node`;

create table `hb_server_node`(
`id` bigint not null primary key auto_increment,
`app_name` varchar(100) not null comment '应用名称',
`host` varchar(100) not null comment '机器名',
`port` int not null comment '端口号',
`cpu` float not null comment 'cpu占用率',
`memory` bigint not null comment '剩余内存',
`memory_ratio` float not null comment '内存使用率',
`disk` bigint not null comment '剩余硬盘',
`disk_ratio` float not null comment '硬盘使用率',
`latency` bigint null comment '平均请求处理延时',
`payload` int not null comment '服务器负载',
`status` int not null comment '状态',
`next` bigint null comment '节点环中下一个结点',
`start_time` datetime not null comment '服务启动时间',
`ring_lock` int default 0 comment '并发锁',
`create_time` datetime not null comment '创建时间',
`update_time` datetime not null comment '更新时间'
)engine=InnoDB charset=utf8 comment='服务器结点信息表';

alter table `hb_server_node` add unique(`host`,`port`);