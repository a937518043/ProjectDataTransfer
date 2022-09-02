package com.data.enums;


public enum DataConfigEnum {
    MYSQL_TABLE_SELECT("mysql", 1, "SELECT TABLE_NAME, COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA =  "),
    ORACLE_TABLE_SELECT("oracle", 2, "");


    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 数据库 查询表结构sql
     */
    private String tableSql;


    DataConfigEnum(String name, Integer type, String tableSql) {
        this.name = name;
        this.type = type;
        this.tableSql = tableSql;
    }

    DataConfigEnum() {
    }

    public static String getDBSelectSql(DataTypeEnum dataTypeEnum) {
        //获取所有的配置
        DataConfigEnum[] values = DataConfigEnum.values();
        //开始查找
        for (int i = 0; i < values.length; i++) {
            //获取配置
            DataConfigEnum value = values[i];
            //开始创建sql
            createDBSelectSql(value, dataTypeEnum);
        }
        return null;
    }

    public static String getTableSelectSql(DataTypeEnum dataTypeEnum) {
        //获取所有的配置
        DataConfigEnum[] values = DataConfigEnum.values();
        //开始查找
        for (int i = 0; i < values.length; i++) {
            //获取配置
            DataConfigEnum value = values[i];
            //开始创建sql
            createTableSelectSql(value, dataTypeEnum);
        }
        return null;
    }

    private static String createTableSelectSql(DataConfigEnum config, DataTypeEnum dataType) {
        StringBuilder sql = new StringBuilder();
        switch (config) {
            case MYSQL_TABLE_SELECT:
                sql.append(config.tableSql).append("'").append(dataType.getDBName()).append("'").append(" AND TABLE_NAME = '").append(dataType.getTableName()).append("'");
                return sql.toString();
            case ORACLE_TABLE_SELECT:
                return sql.toString();
            default:
                return null;
        }
    }

    private static String createDBSelectSql(DataConfigEnum config, DataTypeEnum dataType) {
        StringBuilder sql = new StringBuilder();
        switch (config) {
            case MYSQL_TABLE_SELECT:
                sql.append(config.tableSql).append("'").append(dataType.getDBName()).append("'");
                return sql.toString();
            case ORACLE_TABLE_SELECT:
                return sql.toString();
            default:
                return null;
        }
    }


    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public String getTableSql() {
        return tableSql;
    }

}
