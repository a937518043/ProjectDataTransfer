package com.data.application;

import com.data.service.DataTransfer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Order(value = 1)
public class ProgramEntranceConfig implements ApplicationRunner {

    @Resource
    private DataTransfer dataTransfer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dataTransfer.execute();
    }
}
