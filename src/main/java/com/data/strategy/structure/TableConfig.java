package com.data.strategy.structure;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data.dao.ProjectDataTransferMapper;
import com.data.entity.DataSourceModel;
import com.data.entity.WareHouseEntity;
import com.data.enums.TemplateEnum;
import com.data.enums.TemplateFlagEnum;
import com.data.service.DataTransfer;
import com.data.utils.ComUtils;
import com.data.utils.SpringUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Log4j
@Component
public class TableConfig {

    public static Map<String, String> templateMap = new HashMap<>();

    public static Map<String, Object> configData = new LinkedHashMap<>();

    private String content;

    public TableConfig() {
        //加载查询输出日志模板配置
        intiLogTableTemplate(TemplateEnum.LOG.getTemplateName());
        //加载执行sql输出日志模板配置
        intiLogExecuteTemplate(TemplateEnum.LOG_EXECUTE.getTemplateName());
        //加载字段修正配置
        intiColumnCorrect(TemplateEnum.COLUMN_CORRECT.getTemplateName());
        //加载表的数据标识key修正配置
        intiTableKey(TemplateEnum.TABLE_KEY.getTemplateName());
        //加载表的字段类型长度（去除某些不需要加的长度）
        initColumnLengthExclude(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName());
    }

    public void init(String type) {
        //加载数据库操作模板
        initDataTemplate(type);
    }

    /**
     * 初始化表语句
     */
    public void initTemplate(String type) {
        try {

            //设置配置地址
            StringBuilder path = new StringBuilder("config/data/" + type + "_table");
            ClassPathResource classPathResource = new ClassPathResource(path.toString());
            //获取读取流
            InputStream inputStream = classPathResource.getInputStream();
            //将文件内容
            content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取数据库操作内容
     */
    private void initDataTemplate(String type) {
        initTemplate(type);
        //获取创建表模板
        int tablePrefixIndex = content.indexOf(TemplateEnum.TABLE.getPrefix());
        int tableSuffixIndex = content.indexOf(TemplateEnum.TABLE.getSuffix());
        //获取表模板
        String table = content.substring(index(tablePrefixIndex, TemplateEnum.TABLE), tableSuffixIndex);
        templateMap.put(TemplateEnum.TABLE.getTemplateName(), table);

        //获取创建表的字段模板
        int columnPrefixIndex = content.indexOf(TemplateEnum.COLUMN.getPrefix());
        int columnSuffixIndex = content.indexOf(TemplateEnum.COLUMN.getSuffix());
        //获取表模板
        String column = content.substring(index(columnPrefixIndex, TemplateEnum.COLUMN), columnSuffixIndex);
        templateMap.put(TemplateEnum.COLUMN.getTemplateName(), column);

        //获取新增字段模板
        int alterPrefixIndex = content.indexOf(TemplateEnum.ALTER.getPrefix());
        int alterSuffixIndex = content.indexOf(TemplateEnum.ALTER.getSuffix());
        //获取表模板
        String alter = content.substring(index(alterPrefixIndex, TemplateEnum.ALTER), alterSuffixIndex);
        templateMap.put(TemplateEnum.ALTER.getTemplateName(), alter);

        //获取新增数据模板
        int insertPrefixIndex = content.indexOf(TemplateEnum.INSERT.getPrefix());
        int insertSuffixIndex = content.indexOf(TemplateEnum.INSERT.getSuffix());
        //获取表模板
        String insert = content.substring(index(insertPrefixIndex, TemplateEnum.INSERT), insertSuffixIndex);
        templateMap.put(TemplateEnum.INSERT.getTemplateName(), insert);

        //获取查询字段模板
        int selectPrefixIndex = content.indexOf(TemplateEnum.SELECT.getPrefix());
        int selectSuffixIndex = content.indexOf(TemplateEnum.SELECT.getSuffix());
        //获取表模板
        String select = content.substring(index(selectPrefixIndex, TemplateEnum.SELECT), selectSuffixIndex);
        templateMap.put(TemplateEnum.SELECT.getTemplateName(), select);

        //获取查询表结构模板
        int schemaPrefixIndex = content.indexOf(TemplateEnum.SCHEMA.getPrefix());
        int schemaSuffixIndex = content.indexOf(TemplateEnum.SCHEMA.getSuffix());
        //获取表模板
        String schema = content.substring(index(schemaPrefixIndex, TemplateEnum.SCHEMA), schemaSuffixIndex);
        templateMap.put(TemplateEnum.SCHEMA.getTemplateName(), schema);
    }

    private void intiLogTableTemplate(String type) {
        initTemplate(type);
        int logPrefixIndex = content.indexOf(TemplateEnum.LOG.getPrefix());
        int logSuffixIndex = content.indexOf(TemplateEnum.LOG.getSuffix());
        //获取表模板
        String log = content.substring(index(logPrefixIndex, TemplateEnum.LOG), logSuffixIndex);
        templateMap.put(TemplateEnum.LOG.getTemplateName(), log);
    }

    private void intiLogExecuteTemplate(String type) {
        initTemplate(type);
        int logPrefixIndex = content.indexOf(TemplateEnum.LOG_EXECUTE.getPrefix());
        int logSuffixIndex = content.indexOf(TemplateEnum.LOG_EXECUTE.getSuffix());
        //获取表模板
        String log = content.substring(index(logPrefixIndex, TemplateEnum.LOG_EXECUTE), logSuffixIndex);
        templateMap.put(TemplateEnum.LOG_EXECUTE.getTemplateName(), log);
    }

    private void intiColumnCorrect(String type) {
        initTemplate(type);
        //获取字段修正配置
        templateMap.put(TemplateEnum.COLUMN_CORRECT.getTemplateName(), content);
    }

    private void intiTableKey(String type) {
        initTemplate(type);
        //获取字段修正配置
        templateMap.put(TemplateEnum.TABLE_KEY.getTemplateName(), content);
    }

    private void initColumnLengthExclude(String type) {
        initTemplate(type);
        //获取字段修正配置
        templateMap.put(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName(), content);
    }


    /**
     * 创建表结构
     *
     * @param config 数据结构
     * @return
     */
    public void createTableStructure(DataSourceModel config, List<WareHouseEntity> source) {
        //获取表模板
        String table = ComUtils.replace(templateMap.get(TemplateEnum.TABLE.getTemplateName()));
        //获取字段模板
        String column = ComUtils.replace(templateMap.get(TemplateEnum.COLUMN.getTemplateName()));
        table = table
                //替换表名
                .replace(TemplateFlagEnum.TABLE_NAME.getFlag(), source.get(0).getTable_name())
                //替换表注释
                .replace(TemplateFlagEnum.COMMENT.getFlag(), source.get(0).getTable_comment());
        //初始化拼接后的创建字段
        StringBuilder columnStr = new StringBuilder();
        ArrayList<WareHouseEntity> fieldList = source.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(WareHouseEntity::getColumn_name))), ArrayList::new));

        //开始创建表
        fieldList.stream().forEach(data -> {
            //新增赋值
            StringBuilder newColumn = new StringBuilder(column);
            //获取修正字段类型
            String type = modifiedFieldType(config, data.getData_type());
            //修正字段模板
            String fieldTemplate = modifiedFieldTemplate(config, type);
            if (fieldTemplate != null) {
                newColumn = new StringBuilder(fieldTemplate);
            }
            String columnSql = newColumn.toString()
                    //替换字段名
                    .replace(TemplateFlagEnum.COLUMN_NAME.getFlag(), setColumnCorrectConfig(data))
                    //替换字段类型
                    .replace(TemplateFlagEnum.COLUMN_TYPE.getFlag(), type)
                    //替换字段类型长度
                    .replace(TemplateFlagEnum.COLUMN_LENGTH.getFlag(), isTypeLength(type) ? "(" + data.getColumn_length() + ")" : "")
                    //替换是否为空状态
                    .replace(TemplateFlagEnum.IS_NULLABLE.getFlag(), isNullable(data.getIs_nullable()))
                    //替换字段注释类型
                    .replace(TemplateFlagEnum.COMMENT.getFlag(), data.getColumn_comment());
            //拼接语句
            columnStr.append(" ").append(columnSql).append(", ");
        });
        //获取最后一个逗号位置
        int index = columnStr.lastIndexOf(",");
        //替换创建表的字段列
        String tableSql = table.replace(TemplateFlagEnum.COLUMN_LIST.getFlag(), columnStr.substring(0, index));
        //存储到公共表结构池
        DataTransfer.tableMap.put(config.getTableName(), tableSql);
    }

    private boolean isTypeLength(String type) {
        JSONArray list = new JSONArray();
        if (configData.containsKey(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName())) {
            list = (JSONArray) configData.get(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName());
        }
        String columnLengthExclude = templateMap.get(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName());
        list = JSONArray.parseArray(columnLengthExclude);
        configData.put(TemplateEnum.COLUMN_LENGTH_EXCLUDE.getTemplateName(), list);
        if (list.contains(type)) {
            return false;
        }
        return true;
    }

    private String isNullable(String status) {
        //暂时写死后期改为动态表达式计算
        Map<String, String> statusMap = new LinkedHashMap<>();
        statusMap.put("Yes", "NULL");
        statusMap.put("YES", "NULL");
        statusMap.put("yes", "NULL");
        statusMap.put("y", "NULL");
        statusMap.put("Y", "NULL");
        statusMap.put("no", "NOT NULL");
        statusMap.put("No", "NOT NULL");
        statusMap.put("NO", "NOT NULL");
        statusMap.put("n", "NOT NULL");
        statusMap.put("N", "NOT NULL");
        if (statusMap.containsKey(status)) {
            return statusMap.get(status);
        }
        return "null";
    }

    public String modifiedFieldTemplate(DataSourceModel config, String type) {
        if (!templateMap.containsKey(type)) {
            //初始化存储数据库方言
            initTemplate(config.getType());
            //获取修正创建字段模板
            int fieldPrefixIndex = content.indexOf(TemplateEnum.COLUMN_CORRECTION.getPrefix() + type + "#");
            int fieldSuffixIndex = content.indexOf(TemplateEnum.COLUMN_CORRECTION.getSuffix() + type + "$");
            if (fieldSuffixIndex == -1 && fieldSuffixIndex == -1) {
                return null;
            }
            //获取表模板
            String field = content.substring(index(fieldPrefixIndex, TemplateEnum.COLUMN_CORRECTION), fieldSuffixIndex);
            templateMap.put(type, field);
            return field;
        } else {
            return templateMap.get(type);
        }
    }

    /**
     * 创建表字段
     *
     * @param source 源表字段
     * @param target 目标表字段
     */
    public void createColumn(DataSourceModel config, List<WareHouseEntity> source, List<WareHouseEntity> target) {
        //按照字段名称进行分组
        Map<String, List<WareHouseEntity>> sourceColumnMap = source.stream().collect(Collectors.groupingBy(WareHouseEntity::getColumn_name));
        Map<String, List<WareHouseEntity>> targetColumnMap = target.stream().collect(Collectors.groupingBy(WareHouseEntity::getColumn_name));
        //获取字段模板
        String alter = ComUtils.replace(templateMap.get("alter"));
        //存储替换新增字段
        List<String> alterList = new ArrayList<>();
        //开始对比字段
        sourceColumnMap.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            //获取源表结构字段
            WareHouseEntity table = entry.getValue().get(0);
            //判断是否存在这个字段
            if (!targetColumnMap.containsKey(key)) {
                //新增赋值
                StringBuilder newAlter = new StringBuilder(alter);

                String alterTemplate = newAlter.toString()
                        //替换表名
                        .replace(TemplateFlagEnum.TABLE_NAME.getFlag(), table.getTable_name())
                        //替换字段名
                        .replace(TemplateFlagEnum.COLUMN_NAME.getFlag(), setColumnCorrectConfig(table))
                        //替换字段类型
                        .replace(TemplateFlagEnum.COLUMN_TYPE.getFlag(), modifiedFieldType(config, table.getColumn_type()))
                        //替换字段注释
                        .replace(TemplateFlagEnum.COMMENT.getFlag(), table.getColumn_comment());
                //加入新增字段集合
                alterList.add(alterTemplate);
                //将数据存储到公共字段池
                DataTransfer.columnMap.put(config.getTableName(), alterList);
            }
        });

    }

    public static List<String> toInsert(String tableName, List<Map<String, Object>> sourceData) {
        //初始化存储表数据
        List<String> list = new ArrayList<>();
        //获取新增数据模板
        String insert = ComUtils.replace(templateMap.get(TemplateEnum.INSERT.getTemplateName())
                .replace(TemplateFlagEnum.TABLE_NAME.getFlag(), tableName)
                .replace(TemplateFlagEnum.COLUMN_LIST.getFlag(), DataTransfer.insertColumn.get(tableName)));
        //获取最后的数据id 断点续联功能
        List<String> dataIdList = ComUtils.continueTo(tableName);
        //开始处理数据
        sourceData.stream().forEach(data -> {
            if (data != null) {
                //表的数据标识key修正设置
                String key = setTableKyeCorrectConfig(tableName);
                String id = String.valueOf(data.get(key));
                if (!dataIdList.contains(id)) {
                    //开始拼接数据
                    StringBuilder value = new StringBuilder(id + ":");
                    //获取所有value值
                    List<Object> valueList = data.values().stream().collect(Collectors.toList());
                    //转成插入语句
                    String sql = ComUtils.toListStr(valueList);
                    //将数据替换到模板中
                    value.append(insert.replace(TemplateFlagEnum.VALUE_LIST.getFlag(), sql));
                    list.add(value.toString());
                }
            }
        });

        return list;
    }


    /**
     * 字段修正配置
     *
     * @param table
     * @return
     */
    public static String setColumnCorrectConfig(WareHouseEntity table) {
        //获取字段修正配置
        String columnCorrect = templateMap.get(TemplateEnum.COLUMN_CORRECT.getTemplateName());
        //初始化字段修正配置对象
        Map<String, Map<String, String>> columnCorrectMap = new HashMap<>();
        //转成字段修正配置对象
        columnCorrectMap = JSONUtil.toBean(columnCorrect, columnCorrectMap.getClass());
        //开始设置配置
        if (columnCorrectMap != null && !columnCorrectMap.isEmpty()) {
            //判断是否存在这个表
            if (columnCorrectMap.containsKey(table.getTable_name())) {
                //获取该表的所有修正字段配置
                Map<String, String> correctMap = columnCorrectMap.get(table.getTable_name());
                //获取字段名称
                String columnName = table.getColumn_name();
                //判断是否存在这个字段修正配置
                if (correctMap.containsKey(columnName)) {
                    //获取修正的内容
                    String column = correctMap.get(columnName);
                    return column;
                }
            }
        }
        //返回默认
        return table.getColumn_name();
    }

    /**
     * 表的数据标识key修正配置
     *
     * @param tableName
     * @return
     */
    public static String setTableKyeCorrectConfig(String tableName) {
        //获取所有的表的数据标识key
        String tableKey = templateMap.get(TemplateEnum.TABLE_KEY.getTemplateName());
        //转成map
        Map<String, String> tableKeyMap = JSONUtil.toBean(tableKey, Map.class);
        //判断是否存在该表
        if (tableKeyMap != null && !tableKeyMap.isEmpty()) {
            //开始设置配置
            if (tableKeyMap != null && !tableKeyMap.isEmpty()) {
                //判断是否存在这个表
                if (tableKeyMap.containsKey(tableName)) {
                    //获取该表的所有修正字段配置
                    String key = tableKeyMap.get(tableName);
                    return key;
                }
            }
        }

        //返回默认
        return "id";
    }


    public ProjectDataTransferMapper getMapper() {
        return SpringUtil.getBean(ProjectDataTransferMapper.class);
    }

    private Integer index(Integer prefixIndex, TemplateEnum templateEnum) {
        return prefixIndex + templateEnum.getPrefix().length();
    }

    /**
     * 修正方言类型
     *
     * @param config
     * @param columnType
     * @return
     */
    public String modifiedFieldType(DataSourceModel config, String columnType) {
        if (config.getDialectConfig() != null && !"".equals(config.getDialectConfig())) {
            //初始化存储数据库方言
            initTemplate(config.getDialectConfig());
            //获取数据库方言
            JSONObject dialectConfig = JSONObject.parseObject(content);
            if (dialectConfig.containsKey(columnType)) {
                return String.valueOf(dialectConfig.get(columnType));
            }
        }
        return columnType;
    }

}
