package cn.master.hub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.master.hub.mapper")
public class FullStackTestHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FullStackTestHubApplication.class, args);
    }

}
