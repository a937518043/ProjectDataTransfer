package com.data.config.strategy;
import lombok.Data;

/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-10-19 21:51
 **/

@Data
public class StrategyAspectMethodConfig {

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 处理标识
     */
    private String returnFlag;
}
