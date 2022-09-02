package com.data.dao;

import com.data.annotations.DataLog;
import com.data.annotations.TargetDataSource;
import com.data.entity.DataSourceModel;
import com.data.entity.WareHouseEntity;
import com.data.entity.WareHouseMapper;
import com.data.enums.TemplateEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectDataTransferMapper {

    /**
     * 查询指定库下 ORACLE
     *
     * @return
     */
    @TargetDataSource
    @DataLog(fileName = "table_query.log", Template = TemplateEnum.LOG)
    List<WareHouseEntity> getDB(DataSourceModel dataSource, @Param("config") WareHouseMapper config);

    /**
     * 查询指定库下
     *
     * @return
     */
    @TargetDataSource
    @DataLog(fileName = "table_query.log", Template = TemplateEnum.LOG)
    List<Map<String, Object>> getSourceData(DataSourceModel dataSource, @Param("config") WareHouseMapper config);

    /**
     * 执行sql
     *
     * @return
     */
    @TargetDataSource
    @DataLog(fileName = "table_sql_execute_create.log", Template = TemplateEnum.LOG_EXECUTE)
    void executeCreateSql(DataSourceModel dataSource, @Param("config") WareHouseMapper config);

    /**
     * 执行sql
     *
     * @return
     */
    @TargetDataSource
    @DataLog(fileName = "table_sql_execute.log", Template = TemplateEnum.LOG_EXECUTE)
    void executeSql(DataSourceModel dataSource, @Param("config") WareHouseMapper config);


}
