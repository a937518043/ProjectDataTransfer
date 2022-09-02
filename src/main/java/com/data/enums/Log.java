package com.data.enums;

public enum Log {
    LOG("》》》》》》", "《《《《《《");


    /**
     * 日志前缀
     */
    private String prefix;

    /**
     * 日志后缀
     */
    private String suffix;

    Log(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
