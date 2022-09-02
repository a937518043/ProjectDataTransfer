package com.data.enums;


public enum TemplateEnum {
    TABLE("table", "#:table_#", "#:table_$"),
    COLUMN("column", "#:column_#", "#:column_$"),
    COLUMN_CORRECTION("column_correction", "#:column_", "#:column_"),
    ALTER("alter", "#:alter_#", "#:alter_$"),
    INSERT("insert", "#:insert_#", "#:insert_$"),
    LOG("log", "#:log_&", "#:log_$"),
    LOG_EXECUTE("log_execute", "#:log_#", "#:log_$"),
    SCHEMA("schema", "#:schema_#", "#schema_$"),
    SELECT("select", "#:select_#", "#:select_$"),
    TABLE_KEY("table_key", "", ""),
    COLUMN_CORRECT("column_correct", "", ""),
    COLUMN_LENGTH_EXCLUDE("column_length_exclude", "", ""),
    ;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 后缀
     */
    private String suffix;

    TemplateEnum(String templateName, String prefix, String suffix) {
        this.templateName = templateName;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
