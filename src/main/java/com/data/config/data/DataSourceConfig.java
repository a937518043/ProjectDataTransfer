package com.data.config.data;

import com.data.entity.DataSourceModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("spring.datasource")
public class DataSourceConfig {
    private List<DataSourceModel> jdbc;

    private Boolean continueFlag = false;

    public Map<Object, Object> getDataSourceMap() {
        Map<Object, Object> map = new HashMap<>();
        if (jdbc != null && jdbc.size() > 0) {
            for (int i = 0; i < jdbc.size(); i++) {
                DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
                dataSourceBuilder.driverClassName(jdbc.get(i).getDriverClassName());
                dataSourceBuilder.password(jdbc.get(i).getPassword());
                dataSourceBuilder.username(jdbc.get(i).getUserName());
                dataSourceBuilder.url(jdbc.get(i).getJdbcUrl());
                map.put(jdbc.get(i).getConnName(), dataSourceBuilder.build());
            }
        }
        return map;
    }

    @Bean
    public DynamicDataSource csgDataSource() {
        //创建动态数据库连接源配置
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //获取配置的数据源
        Map<Object, Object> dataSourceMap = getDataSourceMap();
        //切换配置
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        //获取第一个默认配置
        Object object = dataSourceMap.values().toArray()[0];
        //设置默认数据源配置
        dynamicDataSource.setDefaultTargetDataSource(object);
        return dynamicDataSource;
    }

    public void setJdbc(List<DataSourceModel> jdbc) {
        this.jdbc = jdbc;
    }

    public List<DataSourceModel> getJdbc() {
        return this.jdbc;
    }

    public Boolean getContinueFlag() {
        return continueFlag;
    }

    public void setContinueFlag(Boolean continueFlag) {
        this.continueFlag = continueFlag;
    }
}
