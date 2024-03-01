package com.xin.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xin.maker.meta.Meta;
import com.xin.maker.meta.enums.FileGenerateTypeEnum;
import com.xin.maker.meta.enums.MetaEnum;
import com.xin.maker.template.enums.FileFilterRangeEnum;
import com.xin.maker.template.enums.FileFilterRuleEnum;
import com.xin.model.FileFilterConfig;
import com.xin.model.TemplateMakerFileConfig;
import freemarker.ext.beans.MemberAccessPolicy;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 15712
 */
public class TemplateMaker {
    public static void main(String[] args) {
        //输入基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() +
                File.separator + "xinbo-generator-demo-projects/springboot-init";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);
        //模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum =");
//        String searchStr = "Sum: ";

        //第二次设置
        modelInfo.setFieldName("className");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("haha");
        String searchStr = "BaseResponse";
        List<FileFilterConfig> filterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder().
                range(FileFilterRangeEnum.FILE_NAME.getValue()).
                rule(FileFilterRuleEnum.CONTAINS.getValue()).
                value("Base").
                build();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(inputFilePath1);
        fileInfoConfig.setFilterConfigList(filterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);

        filterConfigList.add(fileFilterConfig);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();

        List<TemplateMakerFileConfig.FileInfoConfig> templateMakerFileConfigList = new ArrayList<>();
        templateMakerFileConfigList.add(fileInfoConfig);
        templateMakerFileConfigList.add(fileInfoConfig2);
        // 分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");

        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);
        templateMakerFileConfig.setFileInfoConfig(templateMakerFileConfigList);
        long l = makeTemplate(meta, originProjectPath,modelInfo,templateMakerFileConfig, searchStr, 1763078773211250688L);
        System.out.println(l);
    }


    /**
     * 制作模板
     *
     * @param searchStr 想要挖坑的内容
     * @param id        唯一文件id
     * @return
     */
    public static long makeTemplate(Meta newMeta,
                                    String originProjectPath,
                                    Meta.ModelConfig.ModelInfo modelInfo,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    String searchStr,
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
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(new File(originProjectPath).toPath()).toString();

        //注意window的路径可能是\\，要进行字符串的替换
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList =  templateMakerFileConfig.getFileInfoConfig();
        //生成文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        //可以同时多个文件生成
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            //文件的输入路径一定要是绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不会存在目录）

            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());

            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }

            /*
            //目录或文件的绝对路径
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;

            //模板文件信息
            Meta.FileConfig.FileInfo fileInfo;

            //如果是目录就遍历目录的文件
            if (FileUtil.isDirectory(inputFileAbsolutePath)) {
                //通过hutool进行遍历文件
                List<File> fileList = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : fileList) {
                    fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                    newFileInfoList.add(fileInfo);
                }
            } else {
                // 输入的是文件
                fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, new File(inputFileAbsolutePath));
                newFileInfoList.add(fileInfo);
            }
            */

        }

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            //拿到条件，组键和组名
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setCondition(condition);
            //如果过滤后的文件有分组就把分组的文件添加进去
            groupFileInfo.setFiles(newFileInfoList);
            //重新创建列表
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        //生成元信息文件
        String metaOutPath = sourceRootPath + File.separator + "meta.json";

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
            modelInfoList.add(modelInfo);

            //去除重复的配置信息
            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));

            //更新元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutPath);
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
            modelInfoList.add(modelInfo);
            //作为json文件写出
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);
        }
        return id;
    }

    private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath, File inputFile) {
        //要挖坑的文件
        //win系统对路径进行转换
        //文件输出的绝对路径(用于找到文件)
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutPathAbsolutePath = fileInputAbsolutePath + ".ftl";

        //文件输入输出相对路径(用于配置元信息)
        String fileOutputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileInputPath = fileOutputPath + ".ftl";

        String fileContent;
        //判断模板文件是否被创建出来
        if (FileUtil.exist(fileOutPathAbsolutePath)) {
            //如果模板文件存在就直接读取，在原有的基础上继续追加替换
            fileContent = FileUtil.readUtf8String(fileOutPathAbsolutePath);
        } else {
            //如果模板文件不存在就先读取工作区的项目文件，再进行替换
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //替换文件内容
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        //三.
        //将文件信息提前，不管是第一次修改，还是第二次修改，都可以使用
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        //设置模板的输入路径（这里输入路径应该是模板的输出路径）
        fileInfo.setOutputPath(fileOutputPath);
        //设置生成文件的输出路径（通过模板生成的文件的输出路径）
        fileInfo.setInputPath(fileInputPath);
        //设置文件类型
        fileInfo.setType(MetaEnum.FILE.getValue());

        //判断是否对模板进行了修改如果没有修改就当作静态文件(防止生成无用的模板文件)
        if (fileContent.equals(newFileContent)) {
            fileInfo.setOutputPath(fileOutputPath);
            fileInfo.setInputPath(fileOutputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            //设置文件生成类型
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            //输出模板文件
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
        Collection<Meta.FileConfig.FileInfo> fileInfos = files.stream().collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values();
        return new ArrayList<>(fileInfos);
    }

    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> metes) {
        Collection<Meta.ModelConfig.ModelInfo> modelInfos = metes.stream().collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r))
                .values();
        return new ArrayList<>(modelInfos);
    }

}


