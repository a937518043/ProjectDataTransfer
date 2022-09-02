package com.data.config.strategy;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-10-19 21:51
 **/
@Component
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "strategy.config")
public class StrategyAspectConfig {

    /**
     * 获取配置
     */
    Map<String, StrategyAspectClassConfig> execute = new HashMap<>();


    public Map<String, StrategyAspectClassConfig> getExecute() {
        return execute;
    }

    public void setExecute(Map<String, StrategyAspectClassConfig> execute) {
        this.execute = execute;
    }
}
