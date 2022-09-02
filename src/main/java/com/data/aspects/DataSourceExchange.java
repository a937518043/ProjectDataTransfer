package com.data.aspects;

import com.data.annotations.TargetDataSource;
import com.data.config.data.DataSourceConfig;
import com.data.entity.DataSourceHolder;
import com.data.entity.DataSourceModel;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Data
@Aspect
@Component
public class DataSourceExchange {
    @Resource
    private DataSourceConfig dataSourceConfig;

    private static DataSourceModel dataSourceModel;


    @Before("@annotation(com.data.annotations.TargetDataSource)")
    public void before(JoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Method method = sign.getMethod();
        boolean isMethodAop = method.isAnnotationPresent(TargetDataSource.class);
        //获取方法参数
        Object[] args = joinPoint.getArgs();
        //获取数据源配置
        if (isMethodAop) {
            //获取数据源配置信息
            DataSourceModel dataSourceModel = (DataSourceModel) args[0];
            //设置数据源 确保使用该注解的第一个参数是DataSourceModel配置
            DataSourceHolder.setDataSources(dataSourceModel.getConnName());
            this.dataSourceModel = dataSourceModel;
        } else {
            if (joinPoint.getTarget().getClass().isAnnotationPresent(TargetDataSource.class)) {
                DataSourceModel dataSourceModel = (DataSourceModel) args[0];
                DataSourceHolder.setDataSources(dataSourceModel.getConnName());
                this.dataSourceModel = dataSourceModel;
            }
        }
    }

    @After("@annotation(com.data.annotations.TargetDataSource)")
    public void after() {
        DataSourceHolder.clearDataSource();
    }
}
