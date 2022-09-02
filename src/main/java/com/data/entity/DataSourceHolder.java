package com.data.entity;

public class DataSourceHolder {
    /**
     * 线程本地环境
     */
    private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();

    /**
     * 设置数据源
     */
    public static void setDataSources(String connName) {
        dataSources.set(connName);
    }

    /**
     * 获取数据源
     */
    public static String getDataSource() {
        return dataSources.get();
    }

    /**
     * 清楚数据源
     */
    public static void clearDataSource() {
        dataSources.remove();
    }
}
