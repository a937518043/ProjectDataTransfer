package com.data.service;

import com.data.config.data.DataSourceConfig;
import com.data.entity.DataSourceModel;
import com.data.entity.ExpressionConfig;
import com.data.entity.WareHouseEntity;
import com.data.utils.ComUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j(topic = "同步表结构：")
public class DataTransfer {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private ComUtils comUtils;

    /**
     * 源数据 表结构查询字段
     */
    public static Map<String, String> selectColumn;

    /**
     * 源数据 表结构插入字段
     */
    public static Map<String, String> insertColumn;

    /**
     * 源数据表结构配置
     */
    public static List<WareHouseEntity> sourceList;

    /**
     * 目标数据表结构配置
     */
    public static List<WareHouseEntity> targetList;

    /**
     * 初始化需要新增的表字段
     */
    public static Map<String, List<String>> columnMap = new HashMap<>();

    /**
     * 初始化需要新增的表
     */
    public static Map<String, String> tableMap = new HashMap<>();

    /**
     * 源数据源库表同步插入数据
     */
    public static Map<String, List<String>> insertMap = new HashMap<>();

    /**
     * 已经加载过的数据
     */
    public static Map<String, List<String>> dataMap = new HashMap<>();


    public void execute() {
        //断点续接功能预加载
        dataMap = comUtils.continueTo(dataSourceConfig.getContinueFlag());
        log.info("》》》》》》获取数据库配置信息《《《《《《");
        //获取数据库配置信息
        List<DataSourceModel> jdbcList = dataSourceConfig.getJdbc();
        log.info("》》》》》》查询源库数据结构《《《《《《");
        //查询源库数据结构
        sourceList = comUtils.getDB(jdbcList.get(0));
        log.info("》》》》》》查询目标库数据结构《《《《《《");
        //查询目标库数据结构
        targetList = comUtils.getDB(jdbcList.get(1));
        log.info("》》》》》》开始创建查询字段《《《《《《");
        //开始创建源数据连接所有表的查询字段
        selectColumn = comUtils.createTableColumn(false);
        //开始创建源数据连接所有表的插入字段
        insertColumn = comUtils.createTableColumn(true);
        log.info("》》》》》》开始比对同步表结构《《《《《《");
        //开始比对同步表结构并创建目标数据连接所有的表结构和字段
        comUtils.synchronousStructure(jdbcList.get(1));
        log.info("》》》》》》开始创建同步表结构《《《《《《");
        //开始创建同步表结构 获取目标源的数据库配置
        comUtils.createTable(jdbcList.get(1));
        //查询源库迁移数据
        comUtils.selectData(jdbcList.get(0));
        //开始迁移数据
        comUtils.insertData(jdbcList.get(1));
    }

}
