package com.xin.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.xin.maker.generator.file.DynamicFileGenerator;
import com.xin.maker.meta.Meta;
import com.xin.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author 15712
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        //输出根路径
        String projectPath = System.getProperty("user.dir");
        //输出路径
        String outputPath = projectPath + File.separator + "generated"+ File.separator + meta.getName();
        //检查文件夹是否存在，不存在就创建对应文件
        if (!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }


        //读取resource目录读取模板文件
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        //java包的基础路径
        String outputPackage = meta.getBasePackage();
        String outputPackagePath = StrUtil.join("/",StrUtil.split(outputPackage, "."));
        String outputBaseJavaPackagePat = outputPath + File.separator +"src/main/java/"+ outputPackagePath;

        String inputFilePath;
        String outputFilePath;
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePat + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);



    }
}
