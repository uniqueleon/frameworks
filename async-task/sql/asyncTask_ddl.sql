USE bms_async_task;


DROP TABLE IF EXISTS async_task;

CREATE TABLE IF NOT EXISTS async_task(
`id` BIGINT PRIMARY KEY AUTO_INCREMENT,
`seq_no` VARCHAR(100) NOT NULL COMMENT '流水号',
`task_type` INT NOT NULL COMMENT '1.导入.2.导出',
`warehouse_id` BIGINT NOT NULL COMMENT '仓库id',
`user_id` BIGINT NOT NULL COMMENT '用户id',
`name` VARCHAR(50) NOT NULL COMMENT '任务名称',
`status` INT NOT NULL COMMENT '状态',
`err_msg` VARCHAR(500) NULL COMMENT '错误消息',
`retry_time` INT NOT NULL DEFAULT 3 COMMENT '重试次数',
`oss_file_url` VARCHAR(500) NULL COMMENT '对象存储url',
`executor` TEXT NULL COMMENT '任务执行代码',
`exc_param` TEXT NULL COMMENT '执行参数',
`lang` VARCHAR(50) NULL COMMENT '语言',
`module` VARCHAR(50) NULL COMMENT '所属模块',
`file_suffix` VARCHAR(50) NULL COMMENT '文件后缀',
`file_desc` VARCHAR(3000) NULL COMMENT '文件描述',
`session_data` TEXT NULL COMMENT '会语数据',
`user_agent` VARCHAR(300) NULL COMMENT '浏览器agent',
`remote_ip` VARCHAR(20) NULL DEFAULT '0.0.0.0' COMMENT '远程IP',
`user_com_info` VARCHAR(300) NULL COMMENT '用户电脑信息',
`sub_task_num` BIGINT NULL COMMENT '子任务数',
`create_time` DATETIME NOT NULL COMMENT '创建时间',
`update_time` DATETIME NOT NULL COMMENT '更新时间'
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='异步任务信息表';

DROP TABLE IF EXISTS async_task_detail;

CREATE TABLE IF NOT EXISTS async_task_detail(
`id` BIGINT PRIMARY KEY AUTO_INCREMENT,
`task_id` BIGINT NOT NULL COMMENT '主任务id',
`seq_no` INT NOT NULL COMMENT '子任务序号',
`current_data_no` BIGINT NOT NULL COMMENT '当前记录数',
`data_size` BIGINT NOT NULL COMMENT '数据数',
`precent` FLOAT NOT NULL COMMENT '百分数',
`result_type` INT NULL COMMENT '结果类型.1、无输出.2、文件',
`result_data` text NULL COMMENT '结果数据',
`status` INT NOT NULL COMMENT '是否成功.0.未执行,1. 已执行.2.成功.99.失败',
`result_msg` VARCHAR(500) COMMENT '结果信息',
`start_time` DATETIME NOT NULL COMMENT '创建时间',
`server` varchar(200) COMMENT '服务器信息',
`create_time` DATETIME NOT NULL COMMENT '创建时间',
`update_time` DATETIME NOT NULL COMMENT '更新时间'
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='异步任务明细表';


alter table async_task add column invoke_module VARCHAR(50) NULL COMMENT '调用方模块';
