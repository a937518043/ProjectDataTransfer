package com.data.config.strategy;

import com.data.aspects.StrateyDispatcher;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-10-19 21:51
 **/
@Data
public class StrategyAspectClassConfig {

    /**
     * 子执行器的执行方法 key 子执行器全路径 value 执行方法和执行返回标识
     */
    private Map<String, List<StrategyAspectMethodConfig>> childExecute;

    /**
     * 执行器的执行逻辑
     */
    private List<StrateyDispatcher> dispatcher;
}
