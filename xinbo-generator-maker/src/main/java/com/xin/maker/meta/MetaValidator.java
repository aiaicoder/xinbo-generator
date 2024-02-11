package com.xin.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;


public class MetaValidator {
    public static void doValidAndFill(Meta meta) {
        validAndFillMetaRoot(meta);
        validAndFillFileConfig(meta);
        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        // modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            throw new MetaException("数据模板信息不能为空");
        }
        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollectionUtil.isNotEmpty(modelInfoList)) {
            for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
                // 输出路径默认值
                String fieldName = modelInfo.getFieldName();
                if (StrUtil.isBlank(fieldName)) {
                    throw new MetaException("未填写 fieldName");
                }
                String modelInfoType = StrUtil.blankToDefault(modelInfo.getType(), "String");
                modelInfo.setType(modelInfoType);
            }
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        //校验文件的基本信息,还有默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            throw new MetaException("文件信息不能为空");
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isEmpty(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        // inputRootPath：.source + sourceRootPath 的最后一个层级路径
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source" + File.separator +
                FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        //判断inputRootPath是否为空，如果为空就设置默认值
        defaultInputRootPath = StrUtil.blankToDefault(inputRootPath, defaultInputRootPath);
        fileConfig.setInputRootPath(defaultInputRootPath);

        // outputRootPath默认当前路径的 generated
        String outputRootPath = StrUtil.blankToDefault(fileConfig.getOutputRootPath(), "generated");
        fileConfig.setOutputRootPath(outputRootPath);

        //获取文件类型，默认dir
        String fileDitType = StrUtil.blankToDefault(fileConfig.getType(), MetaEnum.DIR.getValue());
        fileConfig.setType(fileDitType);


        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        for (Meta.FileConfig.FileInfo fileInfo : files) {
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("文件路径不能为空");
            }
            // outputPath: 默认等于 inputPath
            String outputPath  = StrUtil.blankToDefault(fileInfo.getOutputPath(), inputPath);
            fileInfo.setOutputPath(outputPath);
            // type：默认 inputPath 有文件后缀（如 .java）为 file，否则为 dir
            String type = fileInfo.getType();
            if (StrUtil.isBlank(type)) {
                // 无文件后缀
                if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                    fileInfo.setType(MetaEnum.DIR.getValue());
                } else {
                    fileInfo.setType(MetaEnum.FILE.getValue());
                }
            }
            // generateType：如果文件结尾不为 Ftl，generateType 默认为 static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                // 为动态模板
                if (inputPath.endsWith(".ftl")) {
                    fileInfo.setGenerateType("dynamic");
                } else {
                    fileInfo.setGenerateType("static");
                }
            }

        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "yupi");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.yupi");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        String gitInit = StrUtil.emptyToDefault(String.valueOf(meta.isGitInit()), "false");
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setCreateTime(createTime);
        meta.setGitInit(Boolean.parseBoolean(gitInit));
    }
}

