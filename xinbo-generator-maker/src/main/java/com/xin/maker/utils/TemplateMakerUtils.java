package com.xin.maker.utils;

import cn.hutool.core.util.StrUtil;
import com.xin.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateMakerUtils {


    /**
     * 从未分组文件中移除组内的同名文件
     *
     * @param fileInfoList 文件的列表集合
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList) {
        //先获取到所有的分组
        List<Meta.FileConfig.FileInfo> groups = fileInfoList.stream().filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey())).collect(Collectors.toList());
        // 获取所有分组内的文件列表,将获取到的每一个分组的文件列表打平为一个
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groups.stream().flatMap(fileInfo -> fileInfo.getFiles().stream()).collect(Collectors.toList());
        // 获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream().map(Meta.FileConfig.FileInfo::getInputPath).collect(Collectors.toSet());
        //移除掉所有外层文件,进行返回
        return fileInfoList.stream().filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath())).collect(Collectors.toList());

    }
}
