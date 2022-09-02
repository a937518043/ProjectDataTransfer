package com.data.strategy.structure.execute;


import com.data.entity.DataSourceModel;
import com.data.entity.WareHouseEntity;
import com.data.strategy.structure.TableConfig;
import lombok.extern.log4j.Log4j;
import java.util.List;


@Log4j
public class DBSourceTable extends TableConfig {

    @Override
    public void createTableStructure(DataSourceModel config, List<WareHouseEntity> source) {
        super.createTableStructure(config, source);
    }

    @Override
    public void createColumn(DataSourceModel config, List<WareHouseEntity> source, List<WareHouseEntity> target) {
        super.createColumn(config, source, target);
    }
}
