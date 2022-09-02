package com.data.strategy.sources;

import com.data.dao.ProjectDataTransferMapper;
import com.data.entity.DataSourceModel;
import com.data.entity.WareHouseMapper;
import com.data.entity.WareHouseEntity;
import com.data.enums.TemplateEnum;
import com.data.enums.TemplateFlagEnum;
import com.data.enums.Log;
import com.data.service.DataTransfer;
import com.data.strategy.structure.TableConfig;
import com.data.utils.ComUtils;
import com.data.utils.SpringUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.*;


@Data
@Log4j
@Component
public class DBSourceConfig {

    //初始化返回数据
    private List<WareHouseEntity> sourceList = new ArrayList<>();

    //初始化查询条件
    private WareHouseMapper wareHouseConfig = new WareHouseMapper();


    public void DB(DataSourceModel config) {
        //日志标题
        StringBuilder title = new StringBuilder(Log.LOG.getPrefix() + "开始查询" + config.getConnName() + "库的所有表" + Log.LOG.getSuffix());
        //获取查询表结构模板
        String template = TableConfig.templateMap.get(TemplateEnum.SCHEMA.getTemplateName());
        if (template.contains(TemplateFlagEnum.TABLE_SCHEMA.getFlag())) {
            //替换库名
            String schema = ComUtils.replace(template.replace(TemplateFlagEnum.TABLE_SCHEMA.getFlag(), config.getConnName()));
            //设置查询
            wareHouseConfig = new WareHouseMapper(schema, title.toString());
        }
        //设置查询
        wareHouseConfig = new WareHouseMapper(template, title.toString());
    }


    /**
     * 创建同步表结构
     *
     * @param config
     */
    public void createTable(DataSourceModel config) {
        log.info(Log.LOG.getPrefix() + "开始创建同步目标数据不存在的表" + Log.LOG.getSuffix());
        //获取缺失的表结构
        Map<String, String> tableMap = DataTransfer.tableMap;
        //开始创建同步目标数据不存在的表
        if (!tableMap.isEmpty()) {
            tableMap.entrySet().forEach(data -> {
                try {
                    log.info(Log.LOG.getPrefix() + "开始创建同步缺失表：" + data.getKey() + "表：" + data.getValue() + Log.LOG.getSuffix());
                    //日志提示标题
                    StringBuilder title = new StringBuilder(Log.LOG.getPrefix() + "开始创建同步目标数据库[" + config.getConnName() + "].[" + data.getKey() + "]表缺失" + Log.LOG.getSuffix());
                    //开始执行同步sql
                    this.getMapper().executeCreateSql(config, new WareHouseMapper(data.getValue(), title.toString()));
                } catch (Exception e) {
                    log.info(Log.LOG.getPrefix() + "创建同步缺失表失败：" + data.getKey() + "表：" + data.getValue() + Log.LOG.getSuffix());
                }

            });
        }
        log.info(Log.LOG.getPrefix() + "开始创建同步目标数据不存在的字段" + Log.LOG.getSuffix());
        //获取缺失字段
        Map<String, List<String>> columnMap = DataTransfer.columnMap;
        //开始创建同步目标数据不存在的字段
        columnMap.entrySet().forEach(data -> {
            List<String> columnList = data.getValue();
            //日志提示标题
            StringBuilder title = new StringBuilder(Log.LOG.getPrefix() + "开始创建同步目标数据库[" + config.getConnName() + "].[" + data.getKey() + "]表缺失字段：" + Log.LOG.getSuffix());
            columnList.stream().forEach(sql -> {
                try {
                    //获取创建表结构sql
                    log.info(Log.LOG.getPrefix() + "开始创建同步：" + data.getKey() + "表：[" + sql + "]" + Log.LOG.getSuffix());
                    this.getMapper().executeCreateSql(config, new WareHouseMapper(sql, title.toString()));
                } catch (Exception e) {
                    log.info(Log.LOG.getPrefix() + "[ERROR]创建同步失败：" + data.getKey() + "表：[" + sql + "]" + Log.LOG.getSuffix());
                }

            });
        });
    }


    /**
     * 查询迁移数据
     *
     * @param config
     */
    public void selectData(DataSourceModel config) {
        Map<String, String> selectMap = new HashMap<>();
        //获取查询数据模板
        String select = ComUtils.replace(TableConfig.templateMap.get(TemplateEnum.SELECT.getTemplateName()));
        log.info(Log.LOG.getPrefix() + "开始设置查询字段列" + Log.LOG.getSuffix());
        //开始替换查询数据语句模板
        DataTransfer.sourceList.forEach(data -> {
            StringBuilder sql = new StringBuilder(select.replace(TemplateFlagEnum.TABLE_NAME.getFlag(), data.getTable_name()));
            //判断是否存在自定义查询列
            if (sql.indexOf(TemplateFlagEnum.COLUMN_LIST.getFlag()) >= 0) {
                //替换查询列
                String sqlTemplate = sql.toString().replace(TemplateFlagEnum.COLUMN_LIST.getFlag(), DataTransfer.selectColumn.get(data.getTable_name()));
                selectMap.put(data.getTable_name(), sqlTemplate);
            } else {
                selectMap.put(data.getTable_name(), sql.toString());
            }
        });

        log.info(Log.LOG.getPrefix() + "开始查询数据" + Log.LOG.getSuffix());
        //开始将数据转换成insert语句
        DataTransfer.insertColumn.entrySet().forEach(sql -> {
            try {
                //日志输出操作标题
                StringBuilder title = new StringBuilder(Log.LOG.getPrefix() + "开始查询" + sql.getKey() + "表数据" + Log.LOG.getSuffix());
                //查询数据
                log.info(Log.LOG.getPrefix() + "开始查询数据SQL：" + sql.getValue() + Log.LOG.getSuffix());
                //开始查询数据
                List<Map<String, Object>> sourceData = this.getMapper().getSourceData(config, new WareHouseMapper(selectMap.get(sql.getKey()), title.toString()));
                //创建insert语句
                DataTransfer.insertMap.put(sql.getKey(), TableConfig.toInsert(sql.getKey(), sourceData));
            }catch (Exception e){
                log.info(Log.LOG.getPrefix() + "[ERROR]创建同步新增数据语句失败：" + sql.getKey() + "表：[" + sql + "]" + Log.LOG.getSuffix());
            }

        });
    }

    /**
     * 查询数据并开始同步数据
     *
     * @param config
     */
    public void insertData(DataSourceModel config) {
        //获取新增数据
        Map<String, List<String>> insertMap = DataTransfer.insertMap;
        //开始迁移数据
        insertMap.entrySet().forEach(entry -> {
            //获取sql
            List<String> dataList = entry.getValue();
            //开始执行迁移
            dataList.stream().forEach(data -> {
                int index = data.indexOf(":");
                String sql = data.substring(index + 1);
                String dataId = data.substring(0, index);
                this.getMapper().executeSql(config, new WareHouseMapper(sql, dataId, entry.getKey(), ","));
            });
        });

    }


    public ProjectDataTransferMapper getMapper() {
        return SpringUtil.getBean(ProjectDataTransferMapper.class);
    }
}
