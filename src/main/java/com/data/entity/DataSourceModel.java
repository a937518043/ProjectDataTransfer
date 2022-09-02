package com.data.entity;

import lombok.Data;

import java.util.Map;

@Data
public class DataSourceModel {

    /**
     * 数据库驱动
     */
    private String driverClassName;

    /**
     * 数据库连接
     */
    private String jdbcUrl;

    /**
     * 用户
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 库名
     */
    private String connName;

    /**
     * 数据源类型 也充当下标使用
     */
    private String type;

    /**
     * 数据源匹配执行下标使用
     */
    private String typeFlag;

    /**
     * 用于传递操作的临时参数表名
     */
    private String tableName;

    /**
     * 数据库之间数据类型差异化修正方言
     */
    private String dialectConfig;
}
