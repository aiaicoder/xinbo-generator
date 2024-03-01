package com.xin.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author 15712
 */
@Data
@Builder
public class FileFilterConfig {
    /**
     * 规则的范围
     */
    private String range;

    /**
     * 过滤规则
     */
    private String rule;

    /**
     * 值
     */
    private String value;

}
