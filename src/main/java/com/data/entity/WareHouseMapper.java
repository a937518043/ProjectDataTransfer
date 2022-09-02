package com.data.entity;

import lombok.Data;

@Data
public class WareHouseMapper {

    /**
     * 数据库表查询SQL
     */
    private String sql = "";

    /**
     * 操作标题
     */
    private String title = "";

    /**
     * 操作数据id
     */
    private String dataId = "";

    /**
     * 操作表名
     */
    private String tableName = "";

    /**
     * 后缀
     */
    private String suffix = "";

    public WareHouseMapper(String sql) {
        this.sql = sql;
    }

    public WareHouseMapper(String sql, String title) {
        this.sql = sql;
        this.title = title;
    }

    public WareHouseMapper(String sql, String dataId, String tableName, String suffix) {
        this.sql = sql;
        this.dataId = dataId;
        this.tableName = tableName;
        this.suffix = suffix;
    }

    public WareHouseMapper() {
    }
}
