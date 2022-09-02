package com.data.enums;


public enum DataTypeEnum {
    MYSQL("mysql", "mysql"), ORACLE("oracle", "oracle");


    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 库名
     */
    private String DBName;

    /**
     * 表名
     */
    private String tableName;

    DataTypeEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static DataTypeEnum getDBType(String type) {
        //获取所有的配置
        DataTypeEnum[] values = DataTypeEnum.values();
        //开始查找
        for (int i = 0; i < values.length; i++) {
            //获取配置
            DataTypeEnum value = values[i];
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setDBName(String DBName) {
        this.DBName = DBName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDBName() {
        return DBName;
    }

    public String getTableName() {
        return tableName;
    }
}
