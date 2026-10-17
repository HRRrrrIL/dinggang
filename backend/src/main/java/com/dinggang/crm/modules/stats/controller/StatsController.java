package com.dinggang.crm.modules.stats.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.customer.entity.Customer;
import com.dinggang.crm.modules.customer.mapper.CustomerMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 数据统计/报表导出控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final CustomerMapper customerMapper;

    /**
     * 导出客户列表Excel
     */
    @GetMapping("/export/customers")
    public void exportCustomers(HttpServletResponse response) throws IOException {
        List<Customer> customers = customerMapper.selectList(
                new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getCreateTime)
        );

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户列表", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Customer.class)
                .sheet("客户列表")
                .doWrite(customers);
    }

    /**
     * 统计概览数据
     */
    @GetMapping("/overview")
    public Result<java.util.Map<String, Object>> overview() {
        java.util.Map<String, Object> data = new java.util.HashMap<>();

        // 客户总数
        long customerCount = customerMapper.selectCount(null);
        data.put("customerCount", customerCount);

        // 各层级客户数
        long potential = customerMapper.selectCount(new LambdaQueryWrapper<Customer>().eq(Customer::getLevel, 1));
        long intention = customerMapper.selectCount(new LambdaQueryWrapper<Customer>().eq(Customer::getLevel, 2));
        long deal = customerMapper.selectCount(new LambdaQueryWrapper<Customer>().eq(Customer::getLevel, 3));
        long repurchase = customerMapper.selectCount(new LambdaQueryWrapper<Customer>().eq(Customer::getLevel, 4));

        data.put("levelStats", java.util.Map.of(
                "潜在", potential,
                "意向", intention,
                "成交", deal,
                "复购", repurchase
        ));

        return Result.success(data);
    }
}
