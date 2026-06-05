package com.lims;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@MapperScan("com.lims.**.mapper")
@SpringBootApplication
public class LimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimsApplication.class, args);
        System.out.println("================================================");
        System.out.println("  环境检测LIMS系统启动成功！");
        System.out.println("  接口文档: http://localhost:8080/api/doc.html");
        System.out.println("================================================");
    }
}
