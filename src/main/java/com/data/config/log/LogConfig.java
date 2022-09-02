package com.data.config.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("db.log.config")
public class LogConfig {
    /**
     * 日志文件输出路径
     */
    private String path;
}
