package com.xin.maker.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.xin.maker.template.enums.FileFilterRangeEnum;
import com.xin.maker.template.enums.FileFilterRuleEnum;
import com.xin.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 15712
 */
public class FileFilter {


    /**
     * 对某个文件或目录进行过滤，返回文件列表
     *
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> files = FileUtil.loopFiles(filePath);
        //通过filter方法来过滤出符合过滤条件的文件
        return files.stream().filter(file -> doSingleFileFilter(fileFilterConfigList, file)).collect(Collectors.toList());
    }


    /**
     * 单个文件过滤
     *
     * @param fileFilterConfigList 过滤规则
     * @param file 单个文件
     * @return 是否保留
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file) {
        //先拿到文件名称
        String fileName = file.getName();
        //再拿到文件内容
        String fileContent = FileUtil.readUtf8String(file);

        // 所有过滤器校验结束的结果
        boolean result = true;

        //如果过滤规则为空就无需过滤
        if (CollectionUtil.isEmpty(fileFilterConfigList)){
            return true;
        }
        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            //先拿到过滤的范围
            String range = fileFilterConfig.getRange();
            //拿到过滤的规则
            String rule = fileFilterConfig.getRule();
            //拿到过滤的值
            String value = fileFilterConfig.getValue();
            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (fileFilterRangeEnum == null){
                continue;
            }
            //要过滤的内容
            String content = null;
            switch (fileFilterRangeEnum){
                //如果过滤的范围是文件名称
                case FILE_NAME:
                    //过滤内容就为文件名称
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);

            if (fileFilterRuleEnum == null){
                continue;
            }

            switch (fileFilterRuleEnum){
                case EQUALS:
                    //根据不同的匹配规则进行匹配
                    result = content.equals(value);
                    break;
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
            }

            // 有一个不满足，就直接返回
            if (!result){
                return false;
            }
        }


        return true;
    }
}
