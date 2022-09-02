package com.data.aspects;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;

import com.data.annotations.StrategyMehod;
import com.data.config.strategy.StrategyAspectClassConfig;
import com.data.config.strategy.StrategyAspectConfig;
import com.data.config.strategy.StrategyAspectMethodConfig;
import com.data.strategy.sources.DBSourceConfig;
import com.data.utils.SpringUtil;
import org.apache.commons.jexl3.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @program: crm3-price
 * @description:
 * @author:
 * @create: 2021-08-20 18:09
 **/

@Aspect
@Component
public class StrategyAspect<T> {

    private Object returnObject;

    @Resource
    private StrategyAspectConfig strategyAspectConfig;

    private Map<String, Object> parameterMap = new LinkedHashMap<>();

    /**
     * 设置切入点
     */
    @Pointcut("@annotation(com.data.annotations.StrategyMehod)")
    public void annotation() {
    }

    @Before("annotation()")
    public void doBefore(JoinPoint joinPoint) {
        //获取所有类动态策略配置
        Map<String, StrategyAspectClassConfig> execute = strategyAspectConfig.getExecute();
        //获取注解所在签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法名
        Method exceteMethod = signature.getMethod();
        boolean hasAnnotation = exceteMethod.isAnnotationPresent(StrategyMehod.class);
        if (hasAnnotation) {
            /*获取参数的值数组*/
            Object[] args = joinPoint.getArgs();
            //提取OrderClass注解对象
            StrategyMehod strategyMehod = exceteMethod.getAnnotation(StrategyMehod.class);
            //获取执行器类
            String simpleName = strategyMehod.StrategyExecute();
            //获取指定类的所有动态策略配置
            StrategyAspectClassConfig executeClassConfig = execute.get(simpleName);
            //获取配置子执行器和对应执行方法
            Map<String, List<StrategyAspectMethodConfig>> childExecute = executeClassConfig.getChildExecute();
            //获取所有子执行器路径集合
            childExecute.entrySet().stream().forEach(entry -> {
                //获取子执行器类路径
                String childClass = entry.getKey();
                //获取子执行器对应的执行方法
                List<StrategyAspectMethodConfig> methodConfigList = entry.getValue();
                //获取所有的执行器方法名
                List<String> methodNameList = methodConfigList.stream().map(StrategyAspectMethodConfig::getMethodName).collect(Collectors.toList());
                //获取类加载器
                ClassLoader classLoader = ClassUtil.getClassLoader();
                try {
                    Class childClassExecute = classLoader.loadClass(childClass);
                    if (childClassExecute != null) {
                        //获取子执行器类的所有方法
                        Method[] publicMethods = ClassUtil.getPublicMethods(childClassExecute);
                        //转换成list
                        List<Method> methodList = Arrays.asList(publicMethods);
                        //开始运行方法
                        for (Method executeMethod : methodList) {
                            //获取方法名称
                            String methodName = executeMethod.getName();
                            //判断是否存在配置的方法
                            if (methodNameList.contains(methodName)) {
                                //获取参数实际值并存放到全局map中
                                toParameter(executeMethod, args);
                                //执行运算公式
                                String flag = toDispatcher(executeClassConfig.getDispatcher());
                                //转成map
                                Map<String, StrategyAspectMethodConfig> strateyAspectMethodConfigMap = methodConfigList.stream().collect(Collectors.toMap(StrategyAspectMethodConfig::getReturnFlag, obj -> obj));
                                //如何标识统一
                                if (strateyAspectMethodConfigMap.containsKey(flag)) {
                                    String executeMethodName = executeMethod.getDeclaringClass().getName() + "." + executeMethod.getName();
                                    //获取方法返回值
                                    returnObject = ClassUtil.invoke(executeMethodName, args);
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Around("annotation()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (ObjectUtil.isNotNull(returnObject)) {
            return returnObject;
        } else {
            Object[] args = pjp.getArgs();
            pjp.proceed(args);
            Object obj = returnObject;
            returnObject = null;
            return obj;
        }
    }

    private void toParameter(Method excecuteMethod, Object[] args) {
        //初始化参数
        parameterMap = new LinkedHashMap<>();
        //转成LIst 进行后续操作
        List<Object> objects = Arrays.asList(args);

        //获取方法的参数类型集合
        Parameter[] parameters = excecuteMethod.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            //获取方法名称
            String name = parameter.getName();
            String typeName = parameter.getParameterizedType().getTypeName();
            int index = typeName.lastIndexOf(".");
            String param = typeName.substring(index + 1, typeName.length());
            parameterMap.put(name, objects.get(i));
        }

    }


    private String toDispatcher(List<StrateyDispatcher> dispatcherList) {
        //入参判断并赋值
        if (!parameterMap.isEmpty() && !dispatcherList.isEmpty()) {
            // 创建 JexlBuilder
            JexlBuilder jexlB = new JexlBuilder();
            // 创建 JexlEngine
            JexlEngine jexl = jexlB.create();
            // 创建context，用于传递参数
            JexlContext jc = new MapContext();
            parameterMap.entrySet().stream().forEach(entry -> {
                jc.set(entry.getKey(), entry.getValue());
            });

            for (StrateyDispatcher dispatcher : dispatcherList) {
                // 创建表达式对象
                JexlExpression e = jexl.createExpression(dispatcher.getExpression());
                // 执行表达式
                Object o = e.evaluate(jc);
                Boolean flag = Boolean.valueOf(o.toString());
                if (flag) {
                    return dispatcher.getFlag();
                }
            }
        }
        return null;
    }

    /**
     * 获取数据类型
     *
     * @param object
     * @return
     */
    public static String getType(Object object) {
        String typeName = object.getClass().getName();
        int length = typeName.lastIndexOf(".");
        String type = typeName.substring(length + 1);
        return type;
    }


}
