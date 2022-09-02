package com.data.enums;

public enum TemplateFlagEnum {
    //表名替换标识
    TABLE_NAME("tableName", "[tableName]"),
    //创建表的字段和新增字段替换标识
    COLUMN_LIST("columnList", "[columnList]"),
    //字段名的替换标识
    COLUMN_NAME("columnName", "[columnName]"),
    //字段类型的替换标识
    COLUMN_TYPE("columnType", "[columnType]"),
    //字段类型的替换标识
    COLUMN_LENGTH("columnLength", "[columnLength]"),
    //字段是否运行为空
    IS_NULLABLE("isNullable", "[isNullable]"),
    //字段说明的替换标识
    COMMENT("comment", "[comment]"),
    //新增数据的value值替换标识
    VALUE_LIST("valueList", "[valueList]"),
    //日志时间替换标识
    DATE_TIME("dateTime", "[dateTime]"),
    //sql语句替换标识
    DATA_SQL("dataSql", "[dataSql]"),
    //数据标识id替换标识
    DATA_ID("dataId", "[dataId]"),
    //查询表结构库名替换标识
    TABLE_SCHEMA("tableSchema", "[tableSchema]"),
    //查询表结构库名替换标识
    WHERE("where", "[where]"),
    ;


    private String flagName;

    private String flag;

    TemplateFlagEnum(String flagName, String flag) {
        this.flagName = flagName;
        this.flag = flag;
    }

    public String getFlagName() {
        return flagName;
    }

    public String getFlag() {
        return flag;
    }
}
