package com.xin.maker.generator.file;


import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author 15712
 */
public class FileGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
        String inputRootPath = "D:/www/planetProject/xinbo-generator/xinbo-generator-demo-projects/acm-template";
        String outputRootPath = "generated";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath, "src/com/yupi/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/xin/acm/MainTemplate.java").getAbsolutePath();
        DynamicFileGenerator.doGenerate(inputPath, outputPath, model);

        inputPath = new File(inputRootPath, ".gitignore.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore.ftl").getAbsolutePath();
        StaticFileGenerator.copyFilesByRecursive(inputPath, outputPath);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticFileGenerator.copyFilesByRecursive(inputPath, outputPath);

    }

}
