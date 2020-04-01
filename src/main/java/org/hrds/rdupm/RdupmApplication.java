package org.hrds.rdupm;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

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


