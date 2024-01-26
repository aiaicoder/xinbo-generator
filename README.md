# 新博-代码生成器

>致力于大幅度提高开发效率的代码生成器

首先下载原始项目的模板，acm-template，在这个基础进行代码的修改

本项目使用到了FreeMarker 模板引擎

模板引擎是一种用于生成动态内容的类库（或框架），通过将预定义的模板与特定数据合并，来生成最终的输出。 使用模板引擎有很多的优点，首先就是提供现成的模板文件语法和解析能力。开发者只要按照特定要求去编写模板文件，比如使用 ${参数} 语法，模板引擎就能自动将参数注入到模板中，得到完整文件，不用再自己编写解析逻辑了。

> FreeMarker 官方文档：https://freemarker.apache.org/docs/index.html

![image-20240123180316258](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240123180316258.png)

![image-20240123180433251](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240123180433251.png)



## 模板展示

模板的部分语法

1. 插值

   ```java
   表达式：${100 + money}
   ```

2. 分支和判空

   ```java
   <if# user == "??">
   
   <#else>
   xxx
   </if>
   ```

   这是判空

   ```java
   <if# user??">
   
   <#else>
   xxx
   </if>
   ```

3. 循环

   ```java
   <#list users as user>
   	${user} 
   </#list>
   ```

4. 插值

   ```java
   $<user>
   ```

   不建议在模板中使用使用默认值，建议在自己的配置类中准备好默认值

## 动态文件生成

<img src="https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240126112916904.png" alt="image-20240126112916904" style="zoom:50%;" />

其中读取文件模板的时候一定要，加上项目的路径，再拼接上src的路径，不然模板文件会无法找到，而出现报错



文件模板

<img src="https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240126112932939.png" alt="image-20240126112932939" style="zoom:50%;" />

之前我们的路径都是写死在主方法中，所以我们直接把生成模板的方法直接抽取出来，单独作为一个函数进行传参调用

```java
package com.xin.generator;

import cn.hutool.core.bean.BeanUtil;
import com.xin.config.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author 15712
 */
public class DynamicGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthorName("lala");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("out:");
        String projectPath = System.getProperty("user.dir");

        File file = new File("src/main/resources/templates");

        String inputPath = projectPath+ File.separator +file;
        String templateName = "ACMTemplate.java.ftl";
        String outputPath = projectPath+ File.separator+ "mainTemplate.java";
        extracted(inputPath,templateName,outputPath,mainTemplateConfig);

    }

    private static void extracted(String inputPath, String templateName, String outputPath, MainTemplateConfig templateConfig) throws IOException, TemplateException {
        //指定版本号
        Configuration myCfg = new Configuration(Configuration.VERSION_2_3_32);
        // 设置模板文件使用的字符集
        myCfg.setDefaultEncoding("UTF-8");
        //加载路径
        myCfg.setDirectoryForTemplateLoading(new File(inputPath));
        //加载模板
        Template template = myCfg.getTemplate(templateName);

        Map<String, Object> dataModel = BeanUtil.beanToMap(templateConfig);
        //输出位置
        FileWriter writer = new FileWriter(outputPath);

        template.process(dataModel,writer);

        writer.close();

    }

}
```

动态文件与静态文件相结合

```java
package com.xin.generator;

import com.xin.config.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author 15712
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthorName("lala");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("out:");
        doGenerate(mainTemplateConfig);
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
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
        //动态文件输入目录
        File file = new File("src/main/resources/templates/ACMTemplate.java.ftl");
        String doInputPath = new File(parentFile,"xinbo-generator-basic"+File.separator+file).getAbsolutePath();
        //输出目录
        String doOutputPath = new File(parentFile,"xinbo-generator-basic"+File.separator+"acm-template/src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
        //生成动态文件
        DynamicGenerator.doGenerate(doInputPath,doOutputPath, model);

    }
}
```



