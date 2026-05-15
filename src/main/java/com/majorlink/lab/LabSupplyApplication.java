package com.majorlink.lab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 医疗实验室耗材管理系统 - 启动类
 * Major-Link Project
 *
 * @author ZhengYi
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.majorlink.lab.mapper")
public class LabSupplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabSupplyApplication.class, args);
        System.out.println("""
                \n
                ╔══════════════════════════════════════════════════╗
                ║    医疗实验室耗材管理系统启动成功                    ║
                ║    Major-Link Project  v1.0.0                    ║
                ║    接口文档: http://localhost:8080/api/           ║
                ╚══════════════════════════════════════════════════╝
                """);
    }
}
