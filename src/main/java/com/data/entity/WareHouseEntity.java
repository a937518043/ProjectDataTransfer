package com.data.entity;

import lombok.Data;

@Data
public class WareHouseEntity {

    /**
     * 数据库名称
     */
    private String table_schema = "";

    /**
     * 数据库引擎
     */
    private String engine = "";

    /**
     * 表注释
     */
    private String table_comment = "";

    /**
     * 表名
     */
    private String table_name = "";

    /**
     * 字段名称
     */
    private String column_name = "";

    /**
     * 数据类型
     */
    private String data_type = "";

    /**
     * 字段类型
     */
    private String column_type = "";

    /**
     * 字段类型长度
     */
    private String column_length = "";

    /**
     * 字段非空状态
     */
    private String is_nullable;

    /**
     * 字段注释
     */
    private String column_comment = "";

    /**
     * 数据源连接类型
     */
    private String DBType = "";

    /**
     * 数据源连接类型下标
     */
    private String typeFlag = "";

    public WareHouseEntity(String table_name, String column_name) {
        this.table_name = table_name;
        this.column_name = column_name;
    }
}
