package com.data.strategy.log.execute;

import com.data.enums.TemplateEnum;
import com.data.enums.TemplateFlagEnum;
import com.data.strategy.log.Log;
import com.data.utils.ComUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class TableLog extends Log {
    @Autowired
    private ComUtils comUtils;

    @Override
    public void dataLog(TemplateEnum template, String log, Object[] args) {
        String sql = (String) args[0];
        //替换日志时间
        log.replace(TemplateFlagEnum.DATE_TIME.getFlag(), LocalDateTime.now().toString());
        log.replace(TemplateFlagEnum.DATA_SQL.getFlag(), sql);
        //输出日志
        comUtils.log(template.getTemplateName(), log);
    }
}
