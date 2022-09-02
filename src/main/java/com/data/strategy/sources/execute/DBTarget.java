package com.data.strategy.sources.execute;

import com.data.entity.DataSourceModel;
import com.data.entity.WareHouseEntity;
import com.data.strategy.sources.DBSourceConfig;
import com.data.strategy.structure.TableConfig;
import com.data.utils.SpringUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j
@Component
public class DBTarget extends DBSourceConfig {

    /**
     * ORACLE 动态数据源
     *
     * @param config 数据源配置
     * @return
     */
    public List<WareHouseEntity> getDB(DataSourceModel config) {
        //获取模板配置
        TableConfig tableConfig = SpringUtil.getBean(TableConfig.class);
        tableConfig.init(config.getType());
        //初始化数据源参数
        this.DB(config);
        //查询源库数据结构
        List<WareHouseEntity> sourceList = this.getMapper().getDB(config, this.getWareHouseConfig());
        return sourceList;
    }

    /**
     * 创建同步表结构
     *
     * @param config
     */
    public void createTable(DataSourceModel config) {
        super.createTable(config);
    }

    /**
     * 查询迁移数据
     *
     * @param config
     */
    @Override
    public void selectData(DataSourceModel config) {
        super.selectData(config);
    }

    /**
     * 查询数据并开始同步数据
     *
     * @param config
     */
    public void insertData(DataSourceModel config) {
        super.insertData(config);
    }
}
