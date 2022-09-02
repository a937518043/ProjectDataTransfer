package com.data.entity;

import lombok.Data;

@Data
public class TableMapper {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 库类型 1 mysql  2 oracle
     */
    private String type;


}
