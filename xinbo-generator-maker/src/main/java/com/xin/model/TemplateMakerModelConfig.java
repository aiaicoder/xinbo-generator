package com.xin.model;

import com.xin.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 15712
 */
@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {
        private String fieldName;

        private String type;

        private String description;
        // 默认值应该为一个Object接受不一定是Boolean
        private Object defaultValue;

        private String abbr;
        // 用于替换哪些文本
        private String replaceText;
    }


    @Data
    public static class ModelGroupConfig {
        private String condition;

        private String type;

        private String description;

        private String groupKey;

        private String groupName;
    }


}
