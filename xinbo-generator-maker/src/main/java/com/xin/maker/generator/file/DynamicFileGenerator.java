package com.xin.maker.generator.file;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
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
public class DynamicFileGenerator {

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

        //文件路径如果不存在就创建文件
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }



        //输出位置
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)),StandardCharsets.UTF_8));

        template.process(dataModel, out);
        out.close();

    }

}
