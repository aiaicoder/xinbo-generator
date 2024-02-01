package com.xin.model;

import lombok.Data;

/**
 * @author 15712
 */
@Data
public class MainTemplateConfig {

    //作者名称
    private String authorName = "新";

    //是否需要循环
    private Boolean loop = false;

    //输出文本
    private String outputText = "sum:";

}
