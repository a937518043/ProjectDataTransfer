package com.data.utils;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data.annotations.StrategyMehod;
import com.data.config.log.LogConfig;
import com.data.entity.DataSourceModel;
import com.data.entity.FileConfig;
import com.data.entity.WareHouseEntity;
import com.data.enums.Log;
import com.data.service.DataTransfer;
import com.data.strategy.structure.TableConfig;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.data.strategy.structure.TableConfig.setTableKyeCorrectConfig;

@Log4j
@Component
public class ComUtils {

    @Autowired
    private LogConfig logConfig;

    /**
     * 动态获取数据库连接源
     *
     * @param config
     * @return
     */
    @StrategyMehod(StrategyExecute = "SwitchingDataSources")
    public List<WareHouseEntity> getDB(DataSourceModel config) {
        return null;
    }


    /**
     * 比对同步表结构
     */
    public void synchronousStructure(DataSourceModel config) {
        ComUtils comUtils = SpringUtil.getBean(ComUtils.class);
        //根据表名进行数据分组 源
        Map<String, List<WareHouseEntity>> sourceMap = DataTransfer.sourceList.stream().collect(Collectors.groupingBy(WareHouseEntity::getTable_name));
        //根据表名进行数据分组 目标
        Map<String, List<WareHouseEntity>> targetMap = DataTransfer.targetList.stream().collect(Collectors.groupingBy(WareHouseEntity::getTable_name));
        //开始处理
        sourceMap.entrySet().stream().forEach(entry -> {
            //获取表名
            String tableName = entry.getKey();
            //注入传递参数表名
            config.setTableName(tableName);
            //获取表结构
            List<WareHouseEntity> sourceList = entry.getValue();
            //判断目标库是否存在这个表
            if (targetMap.containsKey(tableName)) {
                //如果存在表结构则比对表字段
                List<WareHouseEntity> targetList = targetMap.get(tableName);
                //开始比对表结构字段
                comUtils.createColumn(config, sourceList, targetList);
            } else {
                //开始创建表结构
                comUtils.createTableStructure(config, sourceList);
            }
        });
    }

    /**
     * 开始创建同步表结构和新增表字段
     *
     * @param config
     */
    @StrategyMehod(StrategyExecute = "CreateTable")
    public void createTable(DataSourceModel config) {
    }

    /**
     * 开始同步插入数据
     *
     * @param config
     */
    @StrategyMehod(StrategyExecute = "InsertData")
    public void insertData(DataSourceModel config) {

    }

    /**
     * 开始查询数据
     *
     * @param config
     */
    @StrategyMehod(StrategyExecute = "SelectData")
    public void selectData(DataSourceModel config) {
    }


    /**
     * 创建表结构SQL
     *
     * @param config 匹配参数
     * @param source 源数据
     * @return
     */
    @StrategyMehod(StrategyExecute = "CreateTableStructure")
    public String createTableStructure(DataSourceModel config, List<WareHouseEntity> source) {
        return null;
    }

    /**
     * 创建表字段SQL
     *
     * @param source
     * @param target
     */
    @StrategyMehod(StrategyExecute = "CreateColumn")
    public void createColumn(DataSourceModel config, List<WareHouseEntity> source, List<WareHouseEntity> target) {
    }


    /**
     * 创建日志文件
     */
    private void createFolder(FileConfig config, String content) {
        try {
            File file = new File(config.getPath() + File.separator + config.getFileName());
            //返回的是File类型,可以调用exsit()等方法
            File fileParent = file.getParentFile();
            //返回的是String类型
            String fileParentPath = file.getParent();
            log.info(Log.LOG.getPrefix() + "fileParent:" + fileParent + Log.LOG.getSuffix() + fileParentPath);

            //判断文件路径文件夹是否存在
            if (!fileParent.exists()) {
                //创建文件，能创建多级目录
                fileParent.mkdirs();
            }
            //判断文件是否存在
            if (!file.exists()) {
                //有路径才能创建文件
                file.createNewFile();
            }
            //输出日志内容
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            //换行
            //bw.newLine();
            bw.write(content);
            bw.close();
            log.info(Log.LOG.getPrefix() + "file:" + file + Log.LOG.getSuffix());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始断点续接
     *
     * @param continueFlag true 续接 false 不续接
     */
    public Map<String, List<String>> continueTo(Boolean continueFlag) {
        StringBuffer content = new StringBuffer();
        if (continueFlag) {
            try {
                File file = new File(logConfig.getPath() + File.separator + "table_sql_execute.log");
                //判断文件是否存在
                if (file.exists()) {
                    //获取文件流对象
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    //读取行内容
                    String lineContent;
                    //开始读取内容
                    while ((lineContent = reader.readLine()) != null) {
                        //使用readLine方法，一次读一行
                        content.append(lineContent.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (content.length() > 0) {
                //去除最后一个逗号标识符获取完整内容
                StringBuilder dataContent = new StringBuilder("[" + content.substring(0, content.length() - 1) + "]");
                JSONArray array = JSONArray.parseArray(dataContent.toString());
                List<String> list = JSONObject.parseArray(array.toJSONString(), String.class);
                Map<String, List<String>> dataMap = list.stream().collect(Collectors.groupingBy(data -> data.substring(0, data.indexOf(":"))));
                return dataMap;
            }
        }

        return new HashMap<>();
    }

    public void log(String fileName, String content) {
        FileConfig config = new FileConfig();
        config.setPath(logConfig.getPath());
        config.setFileName(fileName);
        log.info(Log.LOG.getPrefix() + "开始创建" + fileName + "表的同步数据日志" + Log.LOG.getSuffix());
        createFolder(config, content);
    }

    /**
     * 创建表查询字段
     *
     * @param flag true：修正字段 false：不修正字段
     * @return
     */
    public Map<String, String> createTableColumn(Boolean flag) {
        //初始化存储表的查询字段
        Map<String, String> columnMap = new HashMap<>();
        //根据表名进行数据分组 源
        Map<String, List<WareHouseEntity>> sourceMap = DataTransfer.sourceList.stream().collect(Collectors.groupingBy(WareHouseEntity::getTable_name));
        sourceMap.entrySet().forEach(data -> {
            //获取所有字段
            String column = data.getValue().stream().map(WareHouseEntity::getColumn_name).collect(Collectors.joining(","));
            if (flag) {
                List<String> newFieldList = new LinkedList<>();
                data.getValue().stream().forEach(field -> {
                    String newField = TableConfig.setColumnCorrectConfig(field);
                    newFieldList.add(newField);
                });
                columnMap.put(data.getKey(), newFieldList.stream().collect(Collectors.joining(",")));
            } else {
                columnMap.put(data.getKey(), column);
            }
        });
        return columnMap;
    }


    /**
     * 替换模板中的特殊符号
     *
     * @param sql
     * @return
     */
    public static String replace(String sql) {
        String returnSrt = sql.replaceAll("\n", "").replace("\r", "").trim();
        return returnSrt;
    }

    public static <T> String toListStr(List<T> list) {
        StringBuilder builder = new StringBuilder(" (");
        for (int i = 0; i < list.size(); i++) {
            builder.append("'").append(list.get(i)).append("'");
            //判断是否末尾
            if (i != list.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(") ");
        return builder.toString();
    }

    public static List<String> continueTo(String tableName) {
        //获取数据
        List<String> dataList = DataTransfer.dataMap.get(tableName);
        if (dataList != null && !dataList.isEmpty()) {
            //获取所有数据id
            List<String> dataIdList = dataList.stream().map(data -> data.substring((data.indexOf("^")) + 1, data.indexOf("="))).collect(Collectors.toList());
            //降序排序
            Collections.sort(dataIdList, Comparator.comparing(String::toString).reversed());
            return dataIdList;
        }
        return new ArrayList<>();
    }
}
