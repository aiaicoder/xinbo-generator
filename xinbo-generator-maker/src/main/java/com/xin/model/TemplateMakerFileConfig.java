package com.xin.model;

import com.xin.maker.meta.Meta;
import lombok.Data;

import java.util.List;

/**
 * @author 15712
 */
@Data
public class TemplateMakerFileConfig {
    private List<FileInfoConfig> fileInfoConfig;

    private FileGroupConfig fileGroupConfig;

    @Data
    public static class FileInfoConfig {
        private String path;
        private List<FileFilterConfig> filterConfigList;
    }


    @Data
    public static class FileGroupConfig {
        private String condition;
        private String groupKey;
        private String groupName;
    }


}
