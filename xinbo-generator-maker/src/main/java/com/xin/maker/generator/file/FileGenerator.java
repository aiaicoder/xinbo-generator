package com.xin.maker.generator.file;


import com.xin.maker.meta.Meta;
import com.xin.maker.meta.MetaManager;
import com.xin.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author 15712
 */
public class FileGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);
    }
    public static void doGenerate(Object model) throws IOException, TemplateException {
        //获取当前路径
        String projectpath = System.getProperty("user.dir");
        //获取父路径
        File parentFile = new File(projectpath).getParentFile();
        //输入路径
        String inputPath = new File(parentFile,"xinbo-generator-demo-projects"+File.separator+"acm-template").getAbsolutePath();
        //输出路径
        String outputPath = projectpath;
        //生成静态文件
        StaticFileGenerator.copyFilesByRecursive(inputPath,outputPath);
        //动态文件输入目录
        File file = new File("src/main/resources/templates/ACMTemplate.java.ftl");

        String doInputPath = new File(parentFile,"xinbo-generator-maker"+File.separator+file).getAbsolutePath();
        //输出目录
        String doOutputPath = new File(parentFile,"xinbo-generator-maker"+File.separator+"acm-template/src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
        //生成动态文件
        DynamicFileGenerator.doGenerate(doInputPath,doOutputPath, model);

    }

}
