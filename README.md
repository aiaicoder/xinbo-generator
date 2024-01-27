# 新博-代码生成器

>致力于大幅度提高开发效率的代码生成器

首先下载原始项目的模板，acm-template，在这个基础进行代码的修改

本项目使用到了FreeMarker 模板引擎

模板引擎是一种用于生成动态内容的类库（或框架），通过将预定义的模板与特定数据合并，来生成最终的输出。 使用模板引擎有很多的优点，首先就是提供现成的模板文件语法和解析能力。开发者只要按照特定要求去编写模板文件，比如使用 ${参数} 语法，模板引擎就能自动将参数注入到模板中，得到完整文件，不用再自己编写解析逻辑了。

> FreeMarker 官方文档：https://freemarker.apache.org/docs/index.html
> 中文教程：http://freemarker.foofun.cn/toc.html

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

## java命令行开发

![image-20240127114740028](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240127114740028.png)

### Picocli命令行框架

> 官方文档：https://picocli.info/
>
> 快速入门教程：https://picocli.info/quick-guide.html

在项目中引入相关依赖

```xml
<dependency>
  <groupId>info.picocli</groupId>
  <artifactId>picocli</artifactId>
  <version>4.7.5</version>
</dependency>
```

实例代码

```java
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
// some exports omitted for the sake of brevity

@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true) 
public class ASCIIArt implements Runnable { 

    @Option(names = { "-s", "--font-size" }, description = "Font size") 
    int fontSize = 19;

    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli", 
               description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" }; 

    @Override
    public void run() { 
        // The business logic of the command goes here...
        // In this case, code for generation of ASCII art graphics
        // (omitted for the sake of brevity).
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ASCIIArt()).execute(args); 
        System.exit(exitCode); 
    }
}
```

![image-20240127140429228](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240127140429228.png)

创建一个实现 Runnable 或 Callable 的类。

用@Command 注释类并给它一个名称。

MixinStandardHelpOptions 属性将—— help 和—— version 选项添加到应用程序中。

对于应用程序中的每个选项，在命令类中添加一个带@Option 注释的字段。这个例子说明了如何给选项命名和描述，还有很多其他的属性。

对于每个位置参数，在命令类中添加一个带有@Parameter 注释的字段。

Picocli 将命令行参数转换为强类型值，并将这些值注入到带注释的字段中。

在类的 run 或 call 方法中定义业务逻辑。成功完成解析后调用此方法。

在类的 main 方法中，使用 CommandLine.execute 方法引导应用程序。这将解析命令行、处理错误、处理用法请求和版本帮助，并调用业务逻辑。

Execute 方法返回退出代码。应用程序可以使用这个退出代码调用 System.exit，以向调用进程发出成功或失败的信号。

### Interactive (Password) Options(交互式选项)

```java
//需要交互的命令都需要实现Callable接口，无需交互命令实现Runable接口即可
class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;
	
    //interactive = true 表示打开可交互，
    //prompt = "请输入密码"，表示使用自己自定义提词器
    @Option(names = {"-p", "--password"}, description = "Passphrase", interactive = true，prompt = "请输入密码")
    char[] password;

    public Integer call() throws Exception {
        byte[] bytes = new byte[password.length];
        for (int i = 0; i < bytes.length; i++) { bytes[i] = (byte) password[i]; }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes);

        System.out.printf("Hi %s, your password is hashed to %s.%n", user, base64(md.digest()));

        // null out the arrays when done
        Arrays.fill(bytes, (byte) 0);
        Arrays.fill(password, ' ');

        return 0;
    }

    private String base64(byte[] arr) { /* ... */ }
}
```

```java
new CommandLine(new Login()).execute("-u", "user123", "-p");
```

Interactive options by default cause the application to wait for input on stdin. For commands that need to be run interactively as well as in batch mode, it is useful if the option can optionally consume an argument from the command line.

The default [arity](https://picocli.info/#_arity) for interactive options is zero, meaning that the option takes no parameters. From picocli 3.9.6, interactive options can also take a value from the command line if configured with `arity = "0..1"`. (See [Optional Values](https://picocli.info/#_optional_values).)

默认情况下，交互式选项会导致应用程序等待 stdin 上的输入。对于需要以交互方式以及批处理模式运行的命令，如果该选项可以选择使用来自命令行的参数，那么它将非常有用。

交互式选项的默认值为零，这意味着该选项不接受任何参数。在 picocli 3.9.6中，交互式选项还可以从命令行获取一个值，如果配置为 arity = "0..1"(见可选值。)

当一个选项被定义为 arity = “0. .1”时，它可能有一个参数值，也可能没有。【提供了一个可选择性的交互能力】

但是要使用到交互功能，那么用户就必须在控制台输入`-p`等这些参数如何不输入那么就默认是空，在没有给默认值的情况下，所有我们又可以分成强制性交互，可以选交互，例如强制性叫用户输入密码，我们可以使用hutool的工具类通过反射动态获取到我们注解中的参数信息

```java
/**
     *
     * @param clazz 反射获取得到的内
     * @param args 用户传递的参数
     * @return 返回对应的参数
     * @param <T> 泛型
     */
    public static <T> String[] addArgs(Class<T> clazz, String[] args) {
        StringBuilder sb = new StringBuilder(String.join(",",args));
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            //判断改字段是否有该注解
            if (field.isAnnotationPresent(Option.class)){
                Option annotation = field.getAnnotation(Option.class);
                //判断该注解是否开启了交互功能
                if (annotation.interactive()){
                    //匹配到第一个字符存在位置,即判断用户是否已经填写过
                    if (sb.indexOf(annotation.names()[0]) <= 0){
                        sb.append(",").append(annotation.names()[0]);
                    }
                }
            }
        }
        return sb.toString().split(",");
    }
```







