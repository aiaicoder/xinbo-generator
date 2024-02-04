package com.xin.generator;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author xin
 */
public class MainGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
            String inputRootPath = "D:/www/planetProject/xinbo-generator/xinbo-generator-demo-projects/acm-template";
            String outputRootPath =  "generated";

            String inputPath;
            String outputPath;

                inputPath = new File(inputRootPath,"src/com/yupi/acm/MainTemplate.java.ftl").getAbsolutePath();
                outputPath = new File(outputRootPath,"src/com/xin/acm/MainTemplate.java").getAbsolutePath();
                    DynamicGenerator.doGenerate(inputPath,outputPath,model);

                inputPath = new File(inputRootPath,".gitignore").getAbsolutePath();
                outputPath = new File(outputRootPath,".gitignore").getAbsolutePath();
                    StaticGenerator.copyFilesByRecursive(inputPath,outputPath);

                inputPath = new File(inputRootPath,"README.md").getAbsolutePath();
                outputPath = new File(outputRootPath,"README.md").getAbsolutePath();
                    StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
    }

}