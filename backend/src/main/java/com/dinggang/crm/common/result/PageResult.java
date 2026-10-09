package com.dinggang.crm.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果封装
 *
 * @param <T> 列表元素类型
 * @author dinggang
 */
@Data
public class PageResult<T> implements Serializable {

    /**
     * 列表数据
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }
}
