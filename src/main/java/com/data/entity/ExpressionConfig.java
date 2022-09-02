package com.data.entity;

import lombok.Data;


@Data
public class ExpressionConfig {

    /**
     * 数据源执行匹配传递标识
     */
    private String typeFlag;

    public ExpressionConfig(String typeFlag) {
        this.typeFlag = typeFlag;
    }
}
