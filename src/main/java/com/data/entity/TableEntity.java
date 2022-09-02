package com.data.entity;

import lombok.Data;

@Data
public class TableEntity {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 字段注释
     */
    private String columnCommen;
}
