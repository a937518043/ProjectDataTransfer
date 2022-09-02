package com.data.aspects;

import lombok.Data;


/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-10-19 21:51
 **/

@Data
public class StrateyDispatcher {

    /**
     * 表达式
     */
    private String expression;

    /**
     * 执行标识
     */
    private String flag;
}
