package com.xin.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.xin.maker.generator.JarGenerator;
import com.xin.maker.generator.ScriptGenerator;
import com.xin.maker.generator.file.DynamicFileGenerator;
import com.xin.maker.generator.file.StaticFileGenerator;
import com.xin.maker.meta.Meta;
import com.xin.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author 15712
 */
public class GenerateTemplate {
    public void doGenerate(){
        //生成数据模型
        Meta meta = MetaManager.getMetaObject();
        //输出根路径
        String projectPath = System.getProperty("user.dir");
        //输出路径
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();

        //检查文件夹是否存在，不存在就创建对应文件
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        //复制原始文件
        String outputSourceRootPath = copyResource(meta, outputPath);

        //生成代码文件
        generateCode(meta, outputPath);

        //构建jar包
        String jarPath = buildJar(meta, outputPath);

        // 封装脚本
        String shellOutputFilePath = buildScript(outputPath, jarPath);

        //生成精简版文件
        buildDist(outputPath, outputSourceRootPath, jarPath, shellOutputFilePath);
    }

    /**
     *生成精简版本文件
     * @param outputPath 输出路径
     * @param outputSourceRootPath 源文件输出路径
     * @param jarPath jar包路径
     * @param shellOutputFilePath shell脚本路径
     */
    protected void buildDist(String outputPath, String outputSourceRootPath, String jarPath, String shellOutputFilePath) {
        //生成简易运行的文件
        String distOutputPath = outputPath + "-dist";
        //拷贝jar包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        //创建target文件夹
        FileUtil.mkdir(targetAbsolutePath);
        //jar包路径
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        //复制jar包
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        //复制脚本文件
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        //复制源文件
        FileUtil.copy(outputSourceRootPath, distOutputPath, true);
    }

    /**
     * 生成shell脚本
     * @param outputPath 输出路径
     * @param jarPath jar包路径
     * @return shell脚本路径
     */
    protected  String buildScript(String outputPath, String jarPath) {
        String shellOutputFilePath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);
        return shellOutputFilePath;
    }

    /**
     * 生成代码文件
     * @param meta 元信息
     * @param outputPath 输出路径
     */
    protected void generateCode(Meta meta, String outputPath){
        //读取resource目录读取模板文件
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();


        //java包的基础路径
        String outputPackage = meta.getBasePackage();
        String outputPackagePath = StrUtil.join("/", StrUtil.split(outputPackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputPackagePath;

        String inputFilePath;
        String outputFilePath;
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成生成文件
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成查看列表指令
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成配置指令
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成工具类
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/utils/CommandUtils.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/utils/CommandUtils.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成命令主类
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //生成ReadeMe.md文件
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //执行gitInit
        if (meta.isGitInit()) {
            try {
                initGit(outputPath);
            } catch (Exception e) {
                System.out.println("git初始化异常："+e);
            }
            inputFilePath = inputResourcePath + File.separator + "templates/.gitignore.ftl";
            outputFilePath = outputPath + File.separator + ".gitignore";
            DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        }
    }

    /**
     * @param meta       元信息
     * @param outputPath 生成文件路径
     * @return 返回输出源文件路径
     */
    protected String copyResource(Meta meta, String outputPath) {
        //复制原始模板文件
        String inputSourceRootPath = meta.getFileConfig().getSourceRootPath();
        String outputSourceRootPath = outputPath + File.separator + ".source";
        StaticFileGenerator.copyFilesByRecursive(inputSourceRootPath, outputSourceRootPath);
        return outputSourceRootPath;
    }

    /**
     * @param meta       元信息
     * @param outputPath 生成文件路径
     * @return 返回jarPath
     */
    protected String buildJar(Meta meta, String outputPath){
        //生成jar包
        try {
            JarGenerator.doGenerate(outputPath);
        } catch (IOException e) {
            throw new RuntimeException("jar包生成异常");
        }
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        return "target/" + jarName;
    }

    protected void initGit(String outputPath) throws IOException, InterruptedException {
        String initCommand = "git init";
        ProcessBuilder pb = new ProcessBuilder(initCommand.split(" "));
        pb.directory(new File(outputPath));
        Process pro = pb.start();
        pro.waitFor();
    }



}
