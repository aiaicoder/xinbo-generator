package com.xin.maker.template.model;

import lombok.Data;

/**
 * @author 15712
 */
@Data
public class TemplateMakerOutputConfig {
    //从未分组文件中移除组内同名文件
    private boolean removeGroupFilesFromRoot = true;
}
