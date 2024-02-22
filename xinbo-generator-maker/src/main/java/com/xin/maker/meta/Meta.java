package com.xin.maker.meta;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 15712
 */
@NoArgsConstructor
@Data
public class Meta {
    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;
    private boolean gitInit;

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private List<FileInfo> files;
        private String sourceRootPath;

        @NoArgsConstructor
        @Data
        public static class FileInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;
            private String groupKey;
            private String groupName;
            private List<FileInfo> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<ModelInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelInfo {
            private String fieldName;
            private String type;
            private String description;
            // 默认值应该为一个Object接受不一定是Boolean
            private Object defaultValue;
            private String abbr;
            //组
            private String groupKey;
            //组名
            private String groupName;
            //分组后的数据模型
            private List<ModelInfo> models;
            private String condition;
            // 中间参数
            // 该分组下所有参数拼接字符串
            private String allArgsStr;

        }
    }
}
