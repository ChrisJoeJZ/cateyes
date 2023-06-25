package cateye.chris;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cateye.chris.mapper")
public class Application {
    //主启动类
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
