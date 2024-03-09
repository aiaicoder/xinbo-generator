package com.xin.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xin.maker.meta.Meta;
import com.xin.maker.meta.enums.FileGenerateTypeEnum;
import com.xin.maker.meta.enums.MetaEnum;
import com.xin.maker.template.enums.FileFilterRangeEnum;
import com.xin.maker.template.enums.FileFilterRuleEnum;
import com.xin.maker.template.model.TemplateMakerConfig;
import com.xin.maker.template.model.TemplateMakerOutputConfig;
import com.xin.maker.utils.TemplateMakerUtils;
import com.xin.model.FileFilterConfig;
import com.xin.model.TemplateMakerFileConfig;
import com.xin.model.TemplateMakerModelConfig;
import freemarker.ext.beans.MemberAccessPolicy;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 15712
 */
public class TemplateMaker {

    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Long id = templateMakerConfig.getId();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        Meta meta = templateMakerConfig.getMeta();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        return makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig,outputConfig ,id);
    }

    /**
     * 制作模板
     *
     * @param id        唯一文件id
     * @return
     */
    public static long makeTemplate(Meta newMeta,
                                    String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig,
                                    Long id) {
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        //复制目录
        //目录的唯一性
        //临时工作区目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        //判断是否存在目录，不存在就创建
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            //如果不存在说明是首次制作，就直接进行文件的复制，如果存在不需要复制原始项目文件
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        //输入文件的信息,目标文件，将这个改为隔离之后的工作目录
        String sourceRootPath = FileUtil.
                loopFiles(new File(templatePath),1,null).
                stream().filter(File::isDirectory).
                findFirst().
                orElseThrow(RuntimeException::new).
                toString();

        //注意window的路径可能是\\，要进行字符串的替换
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList =  templateMakerFileConfig.getFiles();

        //生成文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath, fileConfigInfoList);

        //处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();


        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig, models);

        //生成元信息文件
        String metaOutPath = templatePath + File.separator + "meta.json";

        //判断是否是第一次构建元信息文件
        if (FileUtil.exist(metaOutPath)) {
            //如果元信息文件存在就直接读取，在原有的基础上继续追加替换
            String metaContent = FileUtil.readUtf8String(metaOutPath);
            //将元信息文件的内容转换为json对象
            Meta oldMeta = JSONUtil.toBean(metaContent, Meta.class);
            //将newMeta的新值重新赋值给oldMeta
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //将文件信息添加到元信息文件当中
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            //去除重复的配置信息
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            //构造模板文件参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            //将文件信息设置到meta元信息中
            newMeta.setFileConfig(fileConfig);
            //设置源文件位置
            fileConfig.setSourceRootPath(sourceRootPath);
            //创建存放文件信息的列表
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            //将文件信息列表设置到文件信息中
            fileConfig.setFiles(fileInfoList);
            //将文件信息添加到列表当中
            fileInfoList.addAll(newFileInfoList);
            //构造模型参数
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            //在元信息中设置模型参数
            newMeta.setModelConfig(modelConfig);
            //模型参数设置到列表中
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            //将模型参数列表添加到模型参数中
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }
        if (templateMakerOutputConfig != null){
            // 文件外层和分组去重
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                List<Meta.FileConfig.FileInfo>  fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }
        //写出元信息调用
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);
        return id;
    }

    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig, List<TemplateMakerModelConfig.ModelInfoConfig> models) {
        // - 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        if (templateMakerModelConfig == null){
            return newModelInfoList;
        }




        //如果没有模型配置，直接返回
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }

        //转换为配置接受的模型对像
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        // - 如果是模型组
        if (modelGroupConfig != null) {
            // 新增分组配置
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);


            //如果过滤后的文件有分组就把分组的文件添加进去
            groupModelInfo.setModels(inputModelInfoList);
            //重新创建列表
            newModelInfoList.add(groupModelInfo);
        }else {
            //不是模型组，直接添加到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();

        //可以同时多个文件生成
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        if (CollUtil.isEmpty(fileConfigInfoList)) {
            return newFileInfoList;
        }

        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();
            //文件的输入路径一定要是绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不会存在目录）
            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());
            fileList = fileList.stream().filter(file -> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig,fileInfoConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();


        if (fileGroupConfig != null) {
            //拿到条件，组键和组名
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(MetaEnum.GROUP.getValue());
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setCondition(condition);
            //如果过滤后的文件有分组就把分组的文件添加进去
            groupFileInfo.setFiles(newFileInfoList);
            //重新创建列表
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    private static Meta.FileConfig.FileInfo makeFileTemplate(
            TemplateMakerModelConfig templateMakerModelConfig,
            TemplateMakerFileConfig.FileInfoConfig templateMakerFileConfig,
            String sourceRootPath, File inputFile) {
        //要挖坑的文件
        //win系统对路径进行转换
        //文件输出的绝对路径(用于找到文件)
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutPathAbsolutePath = fileInputAbsolutePath + ".ftl";

        //文件输入输出相对路径(用于配置元信息)
        String fileOutputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileInputPath = fileOutputPath + ".ftl";


        boolean hasTemplateFile = FileUtil.exist(fileOutPathAbsolutePath);

        String fileContent;
        //判断模板文件是否被创建出来
        if (hasTemplateFile) {
            //如果模板文件存在就直接读取，在原有的基础上继续追加替换
            fileContent = FileUtil.readUtf8String(fileOutPathAbsolutePath);
        } else {
            //如果模板文件不存在就先读取工作区的项目文件，再进行替换
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String newFileContent = fileContent;
        String replacement;

        //对于同一个文件的内容支持多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfig = templateMakerModelConfig.getModels();
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfo : modelInfoConfig) {
            //不是分组
            if (modelGroupConfig == null){
                //替换文件内容
                replacement = String.format("${%s}", modelInfo.getFieldName());
            }else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s}.{%s}",groupKey ,modelInfo.getFieldName());
            }
            newFileContent = StrUtil.replace(fileContent, modelInfo.getReplaceText(), replacement);
        }

        //三.
        //将文件信息提前，不管是第一次修改，还是第二次修改，都可以使用
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        //设置模板的输入路径（这里输入路径应该是模板的输出路径）
        fileInfo.setOutputPath(fileOutputPath);
        //设置生成文件的输出路径（通过模板生成的文件的输出路径）
        fileInfo.setInputPath(fileInputPath);
        //设置文件类型
        fileInfo.setType(MetaEnum.FILE.getValue());
        //先默认文件类型为动态
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        //设置condition
        fileInfo.setCondition(templateMakerFileConfig.getCondition());
        //判断是否对模板进行了修改如果没有修改就当作静态文件(防止生成无用的模板文件)
        //如果不存在模板文件，并且没有更改文件内容，则为静态生成
        boolean contentEquals = fileContent.equals(newFileContent);
        if (!hasTemplateFile) {
            if (contentEquals) {
                fileInfo.setOutputPath(fileOutputPath);
                fileInfo.setInputPath(fileOutputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                //没有模板文件需要挖坑
                //输出模板文件
                FileUtil.writeUtf8String(newFileContent, fileOutPathAbsolutePath);
            }
        }else if (!contentEquals){
            // 有模板文件，且增加了新坑，生成模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutPathAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 去除重复的文件信息
     *
     * @param files 文件信息列表
     * @return 去重后的文件信息列表
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> files) {
        //1.将所有文件(fileInfo)分为有分组的和无分组的
        //先拿到有分组的文件
        //{“groupKey“:"a",files:[1,2]} {“groupKey“:"a",files:[3,2]} {“groupKey“:"b",files:[4,5]} => {“groupKey“:"a",files:[[1,2]][[3,2]} {“groupKey“:"b",files:[[4,5]]}
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfo= files.stream().filter(fileInfo -> {
            //通过判断groupKey不为空进行保留
            return StrUtil.isNotBlank(fileInfo.getGroupKey());
        }).collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));


        //2.对于有分组的如果分组相同，同分组的文件进行合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        //同组内文件合并
        //{“groupKey“:"a",files:[[1,2]][[3,2]} => {“groupKey“:"a",files:[1,2,2,3]} =>{“groupKey“:"a",files:[1,2,3]}
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfo.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream().
                    flatMap(fileInfo -> fileInfo.getFiles().stream()).
                    collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values());

            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }
        //3.创建新的文件配置列表。先将合并后的添加进去
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        //4.再将无分组的文件配置列表添加到结构列表
        resultList.addAll(files.stream()
                .filter(fileInfo -> {
                    //通过判断groupKey为空单独
                    return StrUtil.isEmpty(fileInfo.getGroupKey());
                })
                .collect(
                Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values());
        return resultList;
    }

    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> metes) {
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfo = metes.stream().filter(mete -> {
            return StrUtil.isNotBlank(mete.getGroupKey());
        }).collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();

        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfo.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream().
                    flatMap(modelInfo -> modelInfo.getModels().stream()).
                    collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());

            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        //3.创建新的文件配置列表。先将合并后的添加进去
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        resultList.addAll(metes.stream().
                filter(mete -> {
                    return StrUtil.isEmpty(mete.getGroupKey());
                }).
                collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r))
                .values());
        return resultList;
    }

}


