package com.data.aspects;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import com.data.annotations.DataLog;
import com.data.annotations.StrategyMehod;
import com.data.enums.TemplateEnum;
import com.data.enums.TemplateFlagEnum;
import com.data.strategy.structure.TableConfig;
import com.data.utils.ComUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-08-20 18:09
 **/

@Aspect
@Component
public class DataLogAspect<T> {

    @Autowired
    private ComUtils comUtils;


    /**
     * 设置切入点
     */
    @Pointcut("@annotation(com.data.annotations.DataLog)")
    public void annotation() {

    }

    @Before("annotation()")
    public void doBefore(JoinPoint joinPoint) {
        //获取注解所在签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法名
        Method exceteMethod = signature.getMethod();
        boolean hasAnnotation = exceteMethod.isAnnotationPresent(DataLog.class);
        if (hasAnnotation) {
            //获取参数的值数组
            Object[] args = joinPoint.getArgs();
            //提取OrderClass注解对象
            DataLog dataLog = exceteMethod.getAnnotation(DataLog.class);
            //获取执行器类
            String fileName = dataLog.fileName();
            //获取log日志内容替换模板
            TemplateEnum template = dataLog.Template();
            String log = TableConfig.templateMap.get(template.getTemplateName());
            //使用log日志输出请保持第二位参数是WareHouseMapper的sql配置对象
            Object arg = args[1];
            BeanDesc beanDesc = BeanUtil.getBeanDesc(arg.getClass());
            //获取sql字段值
            String sql = String.valueOf(beanDesc.getProp("sql").getValue(arg));
            //获取日志标题
            String title = String.valueOf(beanDesc.getProp("title").getValue(arg));
            //数据标识id
            String dataId = String.valueOf(beanDesc.getProp("dataId").getValue(arg));
            //表名
            String tableName = String.valueOf(beanDesc.getProp("tableName").getValue(arg));
            //后缀
            String suffix = String.valueOf(beanDesc.getProp("suffix").getValue(arg));
            //替换时间和数据标识
            String logTemplate = log.replace(TemplateFlagEnum.DATE_TIME.getFlag(), LocalDateTime.now().toString())
                    .replace(TemplateFlagEnum.DATA_ID.getFlag(), dataId)
                    .replace(TemplateFlagEnum.TABLE_NAME.getFlag(), tableName);
            //开始替换输出日志模板
            logTemplate = logTemplate.replace(TemplateFlagEnum.DATA_SQL.getFlag(), sql);
            //输出日志
            comUtils.log(fileName, title + "\n" + logTemplate + suffix);
        }
    }

    /**
     * 输出日志
     *
     * @param template 日志模板
     * @param log      日志替换内容
     */
    @StrategyMehod(StrategyExecute = "dataLog")
    private void dataLog(TemplateEnum template, String log, Object[] args) {

    }


}
