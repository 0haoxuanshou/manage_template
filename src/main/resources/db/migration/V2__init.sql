-- 初始化数据库表结构
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(100) NOT NULL COMMENT '配置键',
    `config_value` varchar(500) NOT NULL COMMENT '配置值',
    `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0未删除，1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- 初始化系统配置数据
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('system.name', 'RBAC权限管理系统', '系统名称'),
('system.version', '1.0.0', '系统版本号'),
('system.admin.email', 'admin@example.com', '系统管理员邮箱');