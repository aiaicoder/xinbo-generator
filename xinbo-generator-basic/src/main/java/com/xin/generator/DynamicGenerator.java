package com.xin.generator;

import cn.hutool.core.bean.BeanUtil;
import com.xin.config.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author 15712
 */
public class DynamicGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthorName("安徽金沙湖");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("out:");
        String projectPath = System.getProperty("user.dir");

        File file = new File("src/main/resources/templates/ACMTemplate.java.ftl");

        String inputPath = projectPath + File.separator + file;
        String outputPath = projectPath  + File.separator +  "src/MainTemplate.java";
        doGenerate(inputPath, outputPath, mainTemplateConfig);

    }

    public static void doGenerate(String inputPath, String outputPath, Object templateConfig) throws IOException, TemplateException {
        //指定版本号
        Configuration myCfg = new Configuration(Configuration.VERSION_2_3_32);
        // 设置模板文件使用的字符集
        myCfg.setDefaultEncoding("UTF-8");
        //加载路径
        File templateDir = new File(inputPath).getParentFile();

        myCfg.setDirectoryForTemplateLoading(templateDir);
        //加载模板
        Template template = myCfg.getTemplate(new File(inputPath).getName(),"UTF-8");

        Map<String, Object> dataModel = BeanUtil.beanToMap(templateConfig);
        //输出位置
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)),StandardCharsets.UTF_8));

        template.process(dataModel, out);
        out.close();

    }

}
