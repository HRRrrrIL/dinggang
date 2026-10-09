package com.dinggang.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 初创公司客户关系与合同管理系统 - 启动类
 *
 * @author dinggang
 */
@SpringBootApplication
@MapperScan("com.dinggang.crm.modules.**.mapper")
@EnableAsync        // 开启异步线程池（PDF生成、知识库入库等）
@EnableScheduling   // 开启定时任务（履约提醒、超时扫描）
public class DinggangCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(DinggangCrmApplication.class, args);
        System.out.println("""

            ============================================================
              顶钢 CRM 系统启动成功!
              接口文档: http://localhost:8080/api/doc.html
              默认账号: admin / admin123
            ============================================================
            """);
    }
}
