package org.hrds.rdupm;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableChoerodonResourceServer
@EnableDiscoveryClient
@EnableFeignClients("org.hrds")
@EnableAsync
@SpringBootApplication
public class RdupmApplication {


    public static void main(String[] args) {
        SpringApplication.run(RdupmApplication.class, args);
    }
}


