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
import com.xin.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        String inputPath = projectPath + File.separator + file;
        String templateName = "MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "mainTemplate.java";
        extracted(inputPath, templateName, outputPath, mainTemplateConfig);

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

        template.process(dataModel, writer);

        writer.close();

    }

}
```

动态文件与静态文件相结合

```java
package com.xin.generator;

import com.xin.model.MainTemplateConfig;
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
        String inputPath = new File(parentFile, "xinbo-generator-demo-projects" + File.separator + "acm-template").getAbsolutePath();
        //输出路径
        String outputPath = projectpath;
        //生成静态文件
        StaticGenerator.copyFilesByRecursive(inputPath, outputPath);
        //动态文件输入目录
        File file = new File("src/main/resources/templates/MainTemplate.java.ftl");
        String doInputPath = new File(parentFile, "xinbo-generator-basic" + File.separator + file).getAbsolutePath();
        //输出目录
        String doOutputPath = new File(parentFile, "xinbo-generator-basic" + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java").getAbsolutePath();
        //生成动态文件
        DynamicGenerator.doGenerate(doInputPath, doOutputPath, model);

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
     * @param clazz 反射获取得到的类
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

### 命令模式的使用

客户端 - > 命令（命令的转发） -> 命令接收对象



命令模式的好处就是让我们实现客户端与服务对象解耦，通过命令，中间添加一个发出命令的遥控来处理，所以我们需要新增命令操作时，不需要修改客户端的代码



### Picocli 命令行代码生成器开发

分为3个子命令，查看文件列表，查看模板参数，以及最重要的文件生成

- list
- config
- generate

为了简化使用，要求能同时支持通过完整命令和交互式输入的方式来设置动态参数

首先创建三个命令执行器的类

![image-20240129111711564](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240129111711564.png)

然后在主包中创建对应的主命令，分别对三个子命令进行绑定

在创建一个主类进行进行逻辑

<img src="https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240129112722395.png" alt="image-20240129112722395" style="zoom:50%;" />

切记执行的时候要把测试的注释掉，不然等下打jar包的时候，指定了这个主类，在控制输入命令会失效

```java
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
    	//通过使用输出缓冲流，指定utf-8字符集，来解决乱码问题，如果git也是乱码，在设置中调为utf-8
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)),StandardCharsets.UTF_8));

        template.process(dataModel, out);
        out.close();
    }
```



## 第二阶段开发制作代码生成器的工作（造轮子的轮子）

这个阶段的目的就是省去我们手动编写指令，打包的步骤，提高我们生成模板的开发效率

1. 工具应该提供那些更好的服务
2. 如何动态生成我们需要的命令行工具
3. 怎么挖坑

### 元信息

可以在下图中看到我们的编码路径都是为硬编码，写死在路径当中，所以我们需要一个配置文件去动态的修改文件的路径，这里我们称这种配置文件为元信息

![image-20240201131428566](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240201131428566.png)

设计元信息和设计数据库表是非常类似的，都要根据实际的业务需求，设置合适的存储结构、字段名称和类别

元信息可以是json，可以是yaml

注意，和设计库表一样，能提前确认的字段就提前确认，之后尽量只新增字段、避免修改字段。
后面随着制作工具的能力增强，元信息的配置肯定会越来越多。为此，建议在外层尽量用对象来组织字段，而不是数组。在不确定信息的情况下，这么做更有利于字段的扩展，要知道那些字段是可以变化的，那些字段是重复编写，可以把这些统统放到元信息中统一的管理一下



先在resource目录下创建对应的我们要生成代码生成器的代码目录

![image-20240201133619474](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240201133619474.png)



其中，static 目录用于存放可以直接拷贝的静态文件。
之后，可以把对应的模板文件放到对应的包下，和原项目的文件位置一一对应，便于理解和管理。



在 maker.meta 包下新建 Meta 类，用于接受 JSON 字段。
通过GsonFormatPlus将json字段转换为Meta类

<img src="https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240201134527234.png" alt="image-20240201134527234" style="zoom:50%;" />

部分代码

但是有一个问题，如果我们用一次meta对象就要去创建一个新的meta，这样就会非常消耗我们的性能，因为我们每次都需要去读json文件，如果这个文件很大的话那么就会花费我们很多的事件，所以我们可以用双检索单例模式，让meta只初始化一次，这样就会大大的减少压力

创建一个manager类来复制初始化meta

```java
package com.xin.maker.meta;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;

/**
 * @author 15712
 */
public class MetaManager {

	//volatile保证可见性，防止指令重排
    private static volatile Meta meta;


    private MetaManager() {
        //私有构造器防止外部实例化
    }


    public static Meta getMeta(){
        if (meta == null){
            synchronized (MetaManager.class){
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return null;
    }


    private static Meta initMeta(){
        String metaJson = ResourceUtil.readUtf8Str("mete.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        //todo 校验处理默认值,防止用户输入不合法的内容
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        return meta;

    }

}

```

通过freeMaker编写model模板

```java
package ${basePackage}.model;

import lombok.Data;

/**
 * @author ${author}
 */
@Data
public class DataModel {

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.description??>
        /**
         * ${modelInfo.description}
         */
        </#if>
        private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue?? >= ${modelInfo.defaultValue?c}</#if>;
        <#--其中，modelInfo.defaultValue?c 的作用是将任何类型的变量（比如 boolean 类型和 String 类型）都转换为字符串 -->
    </#list>

}
```

通过编写好的模板去生成对应文件

```java
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

```

接下来的命令生成类也是同样的方法进行操作



要将基础的类修改好之后再进行模板的修改，否则后期打包就会有各种问题频发

<img src="https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240203142948367.png" alt="image-20240203142948367" style="zoom:50%;" />

类似这种

如果在idea出现报错控制台乱码是因为mvn是在win上是gbk的编码

解决办法

①修改命令脚本 -> maven安装目录/bin/mvn.cmd
在mvn.cmd中搜索-D，在带-D的行后添加一行：“-Dfile.encoding=UTF-8” ^

②配置环境变量 -> mvn命令运行时会读取名为MAVEN_OPTS的环境变量
变量：MAVEN_OPTS
值：-Dfile.encoding=UTF-8

做完这里开始编写脚本文件





### 代码优化

可移植性

现在的代码制作工具的可移植性不强，因为我们的模板原始文件都是硬编码写到了，代码里，但是如果换了一个环境，别人的电脑上没有这些代码那么我们的代码就运行不起来了

如下图，MainGenerator 生成代码时依赖的 inputRootPath（模板文件路径）是固定的：

![image-20240207143332895](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240207143332895.png)

所以我们应该直接同时把模板的原文件也直接复制到生成的文件中，文件名称就叫.source，同时去更改meta元信息，修改meta中的代码，最后在MainGenerator中添加复制的原始模板文件的代码

```java
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
    	...

        // 输出根路径
    	...

        // 复制原始文件
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);

        // 读取 resources 目录
        ...
    }
}
```

可以

![image-20240207145128956](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240207145128956.png)

### 制作精简版代码生成器

但其实，对于使用代码生成器的人来说，ta 可能并不关注这些文件，只要能运行脚本就好了。
所以，我们可以生成更为精简的代码生成器，只需要保留 jar 包、脚本文件、原始模板文件，其他的都不用保留。
由于项目还在开发阶段，我们不是修改原有的代码生成方式，而是额外生成一套精简版的代码生成器，放到 dist 目录下，便于后续调试、或者交给用户自己选择生成方式。

思路就是先生成完整的文件，然后通过完整文件，进行copy，只复制出需要的文件



### 健壮性的优化

我们要对元信息中的用户输入的配置信息进行校验，防止用户输入非法信息

**健壮性优化策略**

常用的健壮性优化方式有：输入校验、异常处理、故障恢复（比如事务）、自动重试、降级等。
对于咱们的制作工具项目，影响代码生成结果的、也是需要用户修改的核心内容是元信息配置文件，所以一定要对元信息进行校验、并且用默认值来填充空值，防止用户错误输入导致的异常，从而提高健壮性。

#### 元信息校验和默认值填充

| 字段                            | 默认值                                                       | 校验规则 |
| ------------------------------- | ------------------------------------------------------------ | -------- |
| name                            | my-generator                                                 |          |
| description                     | 我的模板代码生成器                                           |          |
| basePackage                     | com.xin                                                      |          |
| version                         | 1.0                                                          |          |
| author                          | yupi                                                         |          |
| createTime                      | 当前日期                                                     |          |
| fileConfig.sourceRootPath       |                                                              | 必填     |
| fileConfig.inputRootPath        | .source + sourceRootPath 的最后一个层级路径                  |          |
| fileConfig.outputRootPath       | 当前路径下的 generated                                       |          |
| fileConfig.type                 | dir                                                          |          |
| fileConfig.files.inputPath      |                                                              | 必填     |
| fileConfig.files.outputPath     | 等于 inputPath                                               |          |
| fileConfig.files.type           | inputPath 有文件后缀（如 .java）为 file，否则为 dir          |          |
| fileConfig.files.generateType   | 如果文件结尾不为 .ftl，generateType 默认为 static，否则为 dynamic |          |
| modelConfig.models.fieldName    |                                                              | 必填     |
| modelConfig.models.description  |                                                              |          |
| modelConfig.models.type         | String                                                       |          |
| modelConfig.models.defaultValue |                                                              |          |
| modelConfig.models.abbr         |                                                              |          |

自定义异常类
由于元信息校验是一个很重要的操作，所以专门定义一个元信息异常类，便于后续集中处理由于元信息输入错误导致的异常。
在 maker.meta 目录下新增 MetaException.java 文件，代码如下：

```java
package com.xin.maker.meta;

public class MetaException extends RuntimeException{
    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

校验代码

```java
package com.xin.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;


public class MetaValidator {
    public static void doValidAndFill(Meta meta) {
        //基础信息和默认值校验
        String metaName = meta.getName();
        if (StrUtil.isEmpty(metaName)) {
            metaName = "my-generator";
            meta.setName(metaName);
        }
        String description = meta.getDescription();
        if (StrUtil.isEmpty(description)) {
            description = "我的模板代码生成器";
            meta.setDescription(description);
        }
        String basePackage = meta.getBasePackage();
        if (StrUtil.isEmpty(basePackage)) {
            basePackage = "com.xin";
            meta.setBasePackage(basePackage);
        }
        String version = meta.getVersion();
        if (StrUtil.isEmpty(version)) {
            version = "1.0";
            meta.setVersion(version);
        }
        String author = meta.getAuthor();
        if (StrUtil.isEmpty(author)) {
            author = "xin";
            meta.setAuthor(author);
        }
        String createTime = meta.getCreateTime();
        if (StrUtil.isEmpty(createTime)) {
            createTime = LocalDateTimeUtil.format(LocalDate.now(), "yyyy-M-d");
            meta.setCreateTime(createTime);
        }
        //校验文件的基本信息,还有默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            throw new MetaException("文件信息不能为空");
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isEmpty(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        // inputRootPath：.source + sourceRootPath 的最后一个层级路径
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if (StrUtil.isEmpty(inputRootPath)) {
            fileConfig.setInputRootPath(defaultInputRootPath);
        }
        // outputRootPath默认当前路径的 generated
        String outputRootPath = fileConfig.getOutputRootPath();
        if (StrUtil.isEmpty(outputRootPath)) {
            outputRootPath = "generated";
            fileConfig.setOutputRootPath(outputRootPath);
        }
        String fileDitType = fileConfig.getType();
        if (StrUtil.isEmpty(fileDitType)) {
            fileDitType = "dir";
            fileConfig.setType(fileDitType);
        }
        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        for (Meta.FileConfig.FileInfo fileInfo : files) {
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)) {

                throw new MetaException("文件路径不能为空");
            }
            // outputPath: 默认等于 inputPath
            String outputPath = fileInfo.getOutputPath();
            if (StrUtil.isEmpty(outputPath)) {
                fileInfo.setOutputPath(inputPath);
            }
            // type：默认 inputPath 有文件后缀（如 .java）为 file，否则为 dir
            String type = fileInfo.getType();
            if (StrUtil.isBlank(type)) {
                // 无文件后缀
                if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                    fileInfo.setType("dir");
                } else {
                    fileInfo.setType("file");
                }
            }
            // generateType：如果文件结尾不为 Ftl，generateType 默认为 static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                // 为动态模板
                if (inputPath.endsWith(".ftl")) {
                    fileInfo.setGenerateType("dynamic");
                } else {
                    fileInfo.setGenerateType("static");
                }
            }

        }
        // modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig != null) {
            List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
            if (CollectionUtil.isNotEmpty(modelInfoList)) {
                for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
                    // 输出路径默认值
                    String fieldName = modelInfo.getFieldName();
                    if (StrUtil.isBlank(fieldName)) {
                        throw new MetaException("未填写 fieldName");
                    }

                    String modelInfoType = modelInfo.getType();
                    if (StrUtil.isEmpty(modelInfoType)) {
                        modelInfo.setType("String");
                    }
                }
            }
        }
    }
}

```

### **圈复杂度优化**

什么是圈复杂度？

上面的代码虽然能够运行，但是过于复杂了，所有的校验规则全写在一起，会导致圈复杂度过高。
圈复杂度（Cyclomatic Complexity）是一种用于评估代码复杂性的软件度量方法。一般情况下，代码的分支判断越多，圈复杂度越高。一般情况下，代码圈复杂度建议 <= 10，不建议超过 20！

在圈复杂度的计算中，通常使用的指标有

1. COGC (Cyclomatic Complexity)：圈复杂度，是一种通过计算图中的节点、边和连接组件的数量来度量程序复杂性的指标。COGC 通常用于衡量程序中的决策点数量。
2. VG (节点个数)：表示图中节点的数量。
3. EDGES (边的数量)：表示图中边的数量。
4. EVG (Essential VG)：表示程序中的基本节点数，是计算中剔除掉虚拟节点后的节点数量。
5. IVG (Inessential VG)：表示程序中的非基本节点数，是计算中保留的虚拟节点数量。

通过工具可以看到圈复杂非常的高（MetricsReloaded）

![image-20240209134133774](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240209134133774.png)

优化方法：

1）抽取方法
我们可以按照元信息配置的层级，将整段代码抽为 3 个方法：基础元信息校验、fileConfig 校验、modelConfig 校验。

2）在抽取的方法中使用 卫语句，尽早返回

卫语句是在进入主要逻辑之前添加的条件检查语句，以确保程序在执行主要逻辑之前满足某些前提条件，这种技术有助于提高代码的可读性和可维护性。

3）使用工具类减少判断代码

比如基础元信息校验中，使用 Hutool 的 StrUtil.blankToDefault 代替 if (StrUtil.isBlank(xxx))，示例代码如下：

### 模板方法

除了前面提到的校验类之外，项目中还有一个实现流程比较复杂的文件 —— MainGenerator。这个文件的作用是读取元信息，然后根据流程生成不同的代码或执行不同的操作。
之前我们是把所有的流程都写在了 main 方法里，大概 120 行代码。



对应于标准流程的代码优化，我们第一时间要想到的就是模板方法设计模式。

#### 什么是模板方法模式？

模板方法模式通过父类定义了一套算法的标准执行流程，然后由子类具体实现每个流程的操作。使得子类在不改变执行流程结构的情况下，可以自主定义某些步骤的实现。

举个例子，老师让所有的学生每天必须按顺序做 3 件事：

1. 吃饭
2. 睡觉
3. 玩游戏

这相当于定义了一套标准的执行流程，每位学生都必须遵循这个流程去行动，但是可以有不同的做法。

比如小王：

1. 吃拉面
2. 站着睡觉
3. 玩gta

而小李可以：

1. 吃米饭
2. 躺着睡觉
3. 玩apex

这样，不仅可以让子类的行为更加规范、复用父类现成的执行流程，也可以通过创建新的子类来自定义每一步的具体操作，提高了程序的可扩展性。

通过子类来继承父类的方法，来达到定制化效果

**支持 Git 托管项目**

制作工具生成的代码生成器支持使用 Git 版本控制工具来托管，可以根据元信息配置让开发者选择是否开启该特性。

在元信息添加新的属性`gitInit`,类型为boolean，通过process来执行git命令，然后复制.gitignore到代码生成器的根目录实现git托管

```java
public class Meta {
    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;
    private boolean gitInit;
    ....
    }
    
```

```java
protected void initGit(String outputPath) throws IOException, InterruptedException {
        String initCommand = "git init";
        ProcessBuilder pb = new ProcessBuilder(initCommand.split(" "));
        pb.directory(new File(outputPath));
        Process pro = pb.start();
        pro.waitFor();
    }
```

```java
protected void generateCode(Meta meta, String outputPath){
        ....
        //执行gitInit,通过元信息判断是否开启git托管
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
```

### 配置能力的增强

下载精简的Spring Boot 项目模板

**模板能力**

在该项目的 README.md 文件中，可以看到关于该项目的介绍，比如运用的技术、业务特性、业务功能等。

模板拥有的能力如下：
1）实现了用户登录、注册、注销、更新、检索、权限管理
2）帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
3）使用了 MySQL、Redis、Elasticsearch 数据存储
4）使用 Swagger + Knife4j 实现接口文档生成
5）支持全局跨域处理

文件的目录结构

```
.
├── Dockerfile
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── yupi
    │   │           └── springbootinit
    │   │               ├── MainApplication.java
    │   │               ├── common
    │   │               │   ├── BaseResponse.java
    │   │               │   ├── DeleteRequest.java
    │   │               │   ├── ErrorCode.java
    │   │               │   ├── PageRequest.java
    │   │               │   └── ResultUtils.java
    │   │               ├── config
    │   │               │   ├── CorsConfig.java
    │   │               │   ├── JsonConfig.java
    │   │               │   ├── Knife4jConfig.java
    │   │               │   └── MyBatisPlusConfig.java
    │   │               ├── constant
    │   │               │   └── UserConstant.java
    │   │               ├── controller
    │   │               │   ├── PostController.java
    │   │               │   └── UserController.java
    │   │               ├── exception
    │   │               │   ├── BusinessException.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── ThrowUtils.java
    │   │               ├── mapper
    │   │               │   ├── PostMapper.java
    │   │               │   └── UserMapper.java
    │   │               ├── model
    │   │               │   ├── dto
    │   │               │   │   ├── post
    │   │               │   │   │   ├── PostAddRequest.java
    │   │               │   │   │   ├── PostEsDTO.java
    │   │               │   │   │   ├── PostQueryRequest.java
    │   │               │   │   │   └── PostUpdateRequest.java
    │   │               │   │   └── user
    │   │               │   │       ├── UserAddRequest.java
    │   │               │   │       ├── UserLoginRequest.java
    │   │               │   │       ├── UserQueryRequest.java
    │   │               │   │       ├── UserRegisterRequest.java
    │   │               │   │       └── UserUpdateRequest.java
    │   │               │   ├── entity
    │   │               │   │   ├── Post.java
    │   │               │   │   └── User.java
    │   │               │   ├── enums
    │   │               │   │   └── UserRoleEnum.java
    │   │               │   └── vo
    │   │               │       └── LoginUserVO.java
    │   │               └── service
    │   │                   ├── PostService.java
    │   │                   ├── UserService.java
    │   │                   └── impl
    │   │                       ├── PostServiceImpl.java
    │   │                       └── UserServiceImpl.java
    │   └── resources
    │       ├── application.yml
    │       └── mapper
    │           ├── PostMapper.xml
    │           └── UserMapper.xml
    └── test
        └── java
```

生成器应具备的功能

基于模板具有的基本能力，我们可以分析用户可能会有哪些定制化的代码生成需求，并明确代码生成器应该具备的功能。

比如：

1. 替换生成的代码包名
2. 控制是否开启帖子的相关功能
3. 控制是否需要开启跨域
4. 自定义Knife4jConfig接口文档
5. 自定义MySql配置信息
6. 控制是否开启redis
7. 控制是否开启Elasticsearch

实现思路

1）需求：替换生成的代码包名

实现思路：和之前替换包名的实现方式类似，可以将代码中所有出现包名的地方 “挖坑”，指定类似 basePackage 的模型参数，让用户自己输入。

通用能力：由于用到包名的代码非常多，如果都要自己 “挖坑” 并制作 FTL 动态模板，不仅成本高、而且也容易出现遗漏（比如 @MapperScan 注解里也有包名）。

所以我们需要利用制作工具来自动 “挖坑” 并生成模板文件。



2）需求：控制是否生成帖子相关功能

实现思路：允许用户输入一个开关参数来控制帖子功能相关的文件是否生成，比如 PostController、PostService、PostMapper、PostMapper.xml、Post 实体类等。

通用能力：用一个参数同时控制多个文件是否生成，而不是仅仅是某段代码是否生成。



3）需求：控制是否需要开启跨域

实现思路：允许用户输入一个开关参数来控制跨域相关的文件是否生成，比如 CorsConfig.java 文件。

通用能力：用一个参数控制某个文件是否生成，而不是仅能控制代码是否生成。



4）需求：自定义 Knife4jConfig 接口文档配置

实现思路：修改 Knife4jConfig 文件中的配置，比如 title、description、version、apis 扫描包路径等。

通用能力：由于要支持用户输入的参数较多，可以用一个参数控制是否要开启接口文档配置。如果开启，再让用户输入 一组 配置参数。



5）需求：自定义 MySQL 配置信息

实现思路：修改 application.yml 配置文件中 MySQL 的 url、username、password 参数。

通用能力：由于要支持用户输入的参数较多，可以定义一组隔离的配置参数。



6）需求：控制是否开启 Redis

实现思路：修改和开启 Redis 相关的代码，比如 application.yml、pom.xml、MainApplication.java 等多个文件的部分代码

通用能力：用一个参数同时控制多个文件的代码修改（已满足）



7）需求：控制是否开启 Elasticsearch

实现思路：

修改和 Elasticsearch 相关的代码，比如 PostController、PostService、PostServiceImpl、application.yml 等多个文件的部分代码

用参数控制 PostEsDTO 整个文件是否生成

通用能力：用一个参数同时控制多个文件的代码、以及某文件是否生成



 实现流程 

通过上面的分析，我们会发现每个功能的实现所需要的通用能力各不相同，那我们应该先做什么、后做什么、怎么安排实现流程最合理呢？

这里我们一定要综合考虑所有的需求，顾全大局；并且通过需求间的依赖关系、或者实现难易度去综合排序，一步步实现。



现在的制作工具已经具有的能力是：根据某个模型参数同时控制多处代码的修改。

而根据排序，制作工具需要增强的能力有：

1一个模型参数对应某个文件是否生成

2一个模型参数对应多个文件是否生成

3一个模型参数同时控制多处代码修改以及文件是否生成

4定义一组相关的模型参数，控制代码修改或文件生成

5定义一组相关的模型参数，并能够通过其他的模型参数控制是否需要输入该组参数

我们会发现，这些能力基本都和制作工具的 元信息配置文件 有关（因为我们所有做的修改都和元信息挂钩），即我们需要增强它的能力，允许开发者通过修改元信息文件，得到能让用户更灵活生成代码的代码生成器。



通过不断增强元信息配置文件的能力，并且为了防止能力增强导致的冲突应当遵循2点原则

配置文件中的fileConfig应专注于文件生成相关的逻辑

配置文件中的 modelConfig 应专注于数据模型的定义。只是定义有某个参数，但该参数具体的作用是什么，不应该放在 modelConfig 中来控制。比如 model 可以用作配置开关、替换代码内容、控制文件是否生成等。

例如ACM模板中的，通过一个模型参数needGit来控制是否生成 .gitignore 静态文件。

```
{
        "fieldName": "needGit",
        "type": "boolean",
        "description": "是否生成 .gitignore 文件",
        "defaultValue": false,
        "abbr":"n"
      }
```

```
public static void doGenerate(DataModel model) throws IOException, TemplateException {}
boolean needGit = model.isNeedGit();
        if (needGit) {
            inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
            outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
            StaticGenerator.copyFilesByRecursive(inputPath, outputPath);
        }
```

上述代码中，有两处改动：

1. 将 doGenerate 方法的入参类型修改为 DataModel，便于后续获取对象的属性
2. 通过模型的 needGit 作为 if 条件，来判断是否生成 .gitignore 文件

![image-20240221094940570](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240221094940570.png)

并没有生成.gitignore

![image-20240221094952120](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240221094952120.png)

为了做到文件就是单纯的控制文件的生成，模型参数就是专注于模型的定义所以我们需要将是否生成文件和文件的生成关联起来

修改元信息配置文件，给 fileConfig.files 对象新增 condition 字段。它的值可以是某个模型参数的名称，甚至还可以是表达式。
比如指定值为 needGit，配置如下：

```
{
	...
  "fileConfig": {
    "files": [
      {
        "inputPath": ".gitignore",
        "outputPath": ".gitignore",
        "type": "file",
        "generateType": "static",
        "condition": "needGit"
      },
    ]
  },
	...
}
```

同步修改meta类给fileInfo新增字段

确定了预期生成的 DataModel 代码后，修改 maker 项目的 DataModel.java.ftl 模板文件，修改属性的作用域为 public。完整代码如下：

```
package ${basePackage}.model;

import lombok.Data;

/**
 * @author ${author}
 */
@Data
public class DataModel {

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.description??>
        /**
         * ${modelInfo.description}
         */
        </#if>
        public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue?? >= ${modelInfo.defaultValue?c}</#if>;
        <#--其中，modelInfo.defaultValue?c 的作用是将任何类型的变量（比如 boolean 类型和 String 类型）都转换为字符串 -->
    </#list>

}
```

```ftl
package ${basePackage}.generator;
import freemarker.template.TemplateException;
import com.xin.model.DataModel;
import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 */
<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicGenerator.doGenerate(inputPath,outputPath,model);
<#else>
${indent}StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
</#if>
</#macro>
public class MainGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(DataModel model) throws IOException, TemplateException {
            String inputRootPath = "${fileConfig.inputRootPath}";
            String outputRootPath =  "${fileConfig.outputRootPath}";

            String inputPath;
            String outputPath;
        <#list modelConfig.models as modeInfo>
            ${modeInfo.type} ${modeInfo.fieldName} = model.${modeInfo.fieldName};
        </#list>

        <#list fileConfig.files as fileInfo>
            <#if fileInfo.condition??>
            if(${fileInfo.condition}){
                <@generateFile fileInfo=fileInfo indent="                " />
            }
                <#else >
            </#if>
            <@generateFile fileInfo=fileInfo indent="            " />
        </#list>
    }

}
```

```
<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicGenerator.doGenerate(inputPath,outputPath,model);
<#else>
${indent}StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
</#if>
</#macro>
```

其中<#macro></#macro>为宏定义

indent为缩进，fileInfo为宏定义中所需使用到的形参，

```
<#list fileConfig.files as fileInfo>
            <#if fileInfo.condition??>
            if(${fileInfo.condition}){
                <@generateFile fileInfo=fileInfo indent="                " />
            }
                <#else >
            </#if>
            <@generateFile fileInfo=fileInfo indent="            " />
        </#list>
```

**同参数控制多个文件生成**

想要用同一个参数来控制多个文件是否生成，最简单的方式是直接给多个文件配置指定相同参数的 condition 就好了，比如：

```
"files": [
  {
    "inputPath": "src/com/yupi/acm/MainTemplate.java.ftl",
    "outputPath": "src/com/yupi/acm/MainTemplate.java",
    "type": "file",
    "generateType": "dynamic",
    "condition": "needGit"
  },
  {
    "inputPath": ".gitignore",
    "outputPath": ".gitignore",
    "type": "file",
    "generateType": "static",
    "condition": "needGit"
  }
]
```

但如果之后我想统一更改这些字段的 condition 条件，或者查看某个 condition（或者模型参数）同时控制的多个文件怎么办？文件越多，会不会越难维护和管理？，同时如果需要修改还必须要一个一个进行修改就会比较麻烦



所以我们就需要对文件进行分组

有两种方案



1. 给元信息的 fileInfo 增加 group 字段，指定每个文件所属的组。

   参考代码：

   ```
   "files": [
     {
       "inputPath": "src/com/yupi/acm/MainTemplate.java.ftl",
       "outputPath": "src/com/yupi/acm/MainTemplate.java",
       "type": "file",
       "generateType": "dynamic",
       "group": "post"
     }
   ]
   ```

   然后可以在 fileConfig 增加 groupConfig 组配置，包括组名、生成条件等。
   参考代码：

   ```
   "fileConfig": {
     ...
     "groupConfig": {
       "groups": [
         {
           "name": "post",
           "condition": "needCors"
         }
       ]
     }
   }
   ```

   这种方式很像设计库表，把组、文件分别定义，再通过文件指定所属组实现关联。
   优点是结构清晰，可以通过读取 groupConfig 直接获取到所有组的信息。
   但这种方式的缺点是，不好通过配置文件直接获取到同组下的所有文件。还可能会导致文件生成的代码不够优雅、会有很多重复的 if 块判断，【应为每一个文件生成都有condition，每一次生成都需要判断】，比如：

   ```
   boolean needGit = model.needGit;
   
   if (needGit) {
       inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
       outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
       StaticGenerator.copyFilesByHutool(inputPath, outputPath);
   }
   
   if (needGit) {
       inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
       outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
       StaticGenerator.copyFilesByHutool(inputPath, outputPath);
   }
   ```

   **方案二**

   直接把文件组当成一个特殊的文件夹，可以把同组文件都放到该组配置下。
   元信息部分代码如下：

   ```
   "files": [
     {
       "groupKey": "git",
       "groupName": "开源",
       "type": "group",
       "condition": "needGit",
       "files": [
           {
             "inputPath": ".gitignore",
             "outputPath": ".gitignore",
             "type": "file",
             "generateType": "static"
           },
           {
             "inputPath": "README.md",
             "outputPath": "README.md",
             "type": "file",
             "generateType": "static"
           }
       ]
     },
     {
       "inputPath": "src/com/yupi/acm/MainTemplate.java.ftl",
       "outputPath": "src/com/yupi/acm/MainTemplate.java",
       "type": "file",
       "generateType": "dynamic"
     }
   ]
   ```

上述代码中新增字段：

- groupKey：表示组的唯一表示
- groupName：组的名称
- type：值为 group 代表是分组
- condition：该分组共享的生成条件，同时控制组内多个文件的生成

这里整个组就是控制多个文件的生成

**定义一组相关的参数**

对于一个复杂的代码生成器，可能会有很多允许用户自定义的参数，比如光 MySQL 的配置，都可能有十几条。
如果把所有这些参数全部按顺序写在元信息的模型配置中，可能用户在使用时，会被要求一次性输入大量的参数，增加使用和理解成本。而且配置之间有可能会有名称冲突，比如 MySQL 和其他数据库可能都有 url 配置。

和上述文件分组类似，我们也可以对数据模型进行分组。各组下的模型参数互相隔离、保证不会出现命名冲突。

```json
{
        "groupKey": "mainTemplate",
        "groupName": "核心模板",
        "type": "MainTemplate",
        "description": "用于生成核心模板文件",
        "models": [
          {
            "fieldName": "author",
            "type": "String",
            "description": "作者注释",
            "defaultValue": "xin",
            "abbr": "a"
          },
          {
            "fieldName": "outputText",
            "type": "String",
            "description": "输出信息",
            "defaultValue": "sum = ",
            "abbr": "o"
          }
```

上述代码的 modelInfo 中新增字段：

- groupKey：组的唯一表示，有 groupKey 表示开启分组，必须为英文
- groupName：组的名称
- type：表示组对应的 Java Class 类型，必须为大写开头

还需要修改 MetaValidator 的校验逻辑，如果模型的 groupKey 不为空，表示为模型组配置，则不校验 fieldName 等。



同组参数封装
既然参数都已经分组了，为了统一管理同组参数，我们可以将同组参数封装为一个类：

```java
/**
 * 用于生成核心模板文件
 */
@Data
public class MainTemplate {
    /**
     * 作者注释
     */
    public String author = "yupi";

    /**
     * 输出信息
     */
    public String outputText = "sum = ";
}
```

然后可以在 DataModel 中定义该类型的属性字段，代码如下：

```
/**
 * 核心模板
 */
public MainTemplate mainTemplate;
```

如何让用户的输入自动填充到该对象中呢？
最直接方式：像原来一样让用户依次输入所有的参数，然后将参数一个个地设置到参数组对象中。
但这种方式并不优雅，Picocli 作为一个强大的命令行开发框架，已经帮我们想到了复杂参数的场景，并提供了 参数组特性。
Picocli 参数组官方介绍：https://picocli.info/#_argument_groups

```java
@Command(name = "sectiondemo", description = "Section demo")
public class OptionSectionDemo {

    @ArgGroup(validate = false, heading = "This is the first section%n")
    Section1 section1;

    static class Section1 {
        @Option(names = "-a", description = "Option A") int a;
        @Option(names = "-b", description = "Option B") int b;
        @Option(names = "-c", description = "Option C") int c;
    }

    @ArgGroup(validate = false, heading = "This is the second section%n")
    Section2 section2;

    static class Section2 {
        @Option(names = "-x", description = "Option X") int x;
        @Option(names = "-y", description = "Option Y") int y;
        @Option(names = "-z", description = "Option Z") int z;
    }

    public static void main(String[] args) {
        new CommandLine(new OptionSectionDemo()).usage(System.out);
    }
}
```

**定义可选开启的参数组**

我们的需求是：可以根据用户输入的某个开关参数，来控制是否要让用户输入其他的参数组。
也就是说，我们的模型参数之间是存在依赖关系的，必须按照某个顺序依次引导用户输入。
那其实我们可以想一种更优雅的交互方式：首先让用户输入最外层未分组的模型参数，然后再根据用户的输入情况，引导用户依次输入分组的参数。
简单来说，就是对复杂的输入进行了分步，让用户一步步填写。

**实现思路**

1）给每个参数组创建一个独立的 Picocli Command 类，用于通过命令行接受该组参数值
2）先用根 Command 类（GenerateCommand类）接受最外层未分组的参数
3）外层参数输入完成后，在 run（或者 call）方法中判断输入的参数，如果需要使用参数组，就再次使用步骤 1 中创建的 Command 类和用户交互。

制作工具实现
已经确定了要制作的代码生成器的代码后，就可以增强制作工具，通过配置自动生成上述代码了。
比较关键的一点是：如何根据一个模型参数，控制另一个模型参数组是否要输入？
其实和控制文件是否生成的逻辑一样，我们给模型组增加一个 condition 字段，可以将其他模型参数的 fieldName 作为 condition 表达式的值。

1）修改元信息文件的 models 组配置，补充 condition 字段，用 loop 字段来控制是否开启分组：

同步修改 Meta 文件，给 ModelInfo 补充 condition 字段

2）修改 DataModel.java.ftl 模板文件，目标是生成前面已经跑通流程的代码。

3）注意，模型分组后，我们在 MainGenerator 中获取模型字段的代码也要修改，多取一个层级

4）修改 GenerateCommand.java.ftl 模板文件，目标是生成前面已经跑通流程的代码。
这里最复杂的地方在于如何根据 models 配置生成包含所有参数的 args 列表。

开发小技巧

> 开发复杂需求或新项目时，先一切从简，完成核心流程的开发。在这个过程中可以记录想法和扩展思路，后面再按需实现



在上面的操作当中

我们都是自己手动生成模板，自己挖坑，自己生成meta元信息，但是如果文件很多的话，也要慢慢生产吗

下面的代码都遵循这张图

在使用制作工具生成前，我们依次做了以下事情：

1. 先指定一个原始的、待“挖坑”的输入文件
2. 明确文件中需要被动态替换的内容和模型参数
3. 自己编写 FreeMarker FTL 模板文件
4. 自己编写生成器的元信息配置，包括基本信息、文件配置、模型参数配置



前面两部都是我们先确定再去编写，所以在前两部确定的情况下可以通过程序实现后面两个步骤

![image-20240227130057535](https://my-notes-li.oss-cn-beijing.aliyuncs.com/li/image-20240227130057535.png)





第一步：输入模板基本信息

```java
  //输入基本信息
  String name = "acm-template-generator";
  String description = "ACM 示例模板生成器";
```

第二步：输入文件信息

```java

//输入文件的信息,目标文件
        String projectPath = System.getProperty("user.dir");
        String sourceRootPath = new File(projectPath).getParent() +
                File.separator + "xinbo-generator-demo-projects/acm-template";
        System.out.println(sourceRootPath);
        //注意window的路径可能是\\，要进行字符串的替换
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        //要挖坑的文件
        String fileInputPath = "src/com/yupi/acm/MainTemplate.java";
        String fileOutputPath = fileInputPath + ".flt";
```

第三步：模型参数

```java
//模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum =");

        //使用字符串替换算法进行替换，通过使用hutool工具
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String content = FileUtil.readUtf8String(fileInputAbsolutePath);
        String replacement = String.format("{%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(content, "Sum: ", replacement);

        //输出模板文件
        //路径都用绝对路径
        String fileOutPathAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        FileUtil.writeUtf8String(newFileContent, fileOutPathAbsolutePath);

        //三.
        //输出元信息文件
        String metaOutPath = sourceRootPath + File.separator + "meta.json";
        //构造模型参数
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        //构造模板文件参数
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        //将文件信息设置到meta元信息中
        meta.setFileConfig(fileConfig);
        //设置源文件位置
        fileConfig.setSourceRootPath(sourceRootPath);
        //创建存放文件信息的列表
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        //设置文件类型
        fileInfo.setType(MetaEnum.FILE.getValue());
        //设置文件生成类型
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        //设置模板的输入路径（这里输入路径应该是模板的输出路径）
        fileInfo.setOutputPath(fileOutputPath);
        //设置生成文件的输出路径（通过模板生成的文件的输出路径）
        fileInfo.setInputPath(fileOutputPath);
        //将文件信息添加到列表当中
        fileInfoList.add(fileInfo);
        //将文件信息列表设置到文件信息中
        fileConfig.setFiles(fileInfoList);


        //构造模型参数
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        //在元信息中设置模型参数
        meta.setModelConfig(modelConfig);
        //模型参数设置到列表中	
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        //将模型参数列表添加到模型参数中
        modelConfig.setModels(modelInfoList);
        modelInfoList.add(modelInfo);
        //作为json文件写出
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutPath);
```

虽然制作模板的流程是跑通了，但我们会发现一个问题：上述代码直接在原始项目内生成了模板和元信息配置，其实是对原项目的污染。如果我们想重新生成，就得一个个删除上次生成的文件。



这个时候我们就要隔离工作空间

### **工作空间隔离**

为了我们每次生成模板的时候不会对原文件进行修改这个时候就要将源文件复制到一个临时的目录进行模板的生成

我们约定将 maker 项目下的 `.temp` 临时目录作为工作空间的根目录，并且在项目的 `.gitignore `文件中忽略该目录。

1. 先定义一个原始项目的路径的存放位置
2. 每次制作分配一个唯一 id（使用雪花算法），作为工作空间的名称，从而实现隔离
3. 通过 FileUtil.copy 复制目录
4. 改变量 sourceRootPath 的值为复制后的工作空间内的项目根目录

```java
public static void main(String[] args) {
        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() +
                File.separator + "xinbo-generator-demo-projects/acm-template";
        //复制目录
        //目录的唯一性
        long id = IdUtil.getSnowflakeNextId();
        //临时工作区目录
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        //判断是否存在目录，不存在就创建
        if (FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
        }
        FileUtil.copy(originProjectPath, templatePath,true);


        //输入基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";
        //输入文件的信息,目标文件，将这个改为隔离之后的工作目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(new File(originProjectPath).toPath()).toString();
        System.out.println(sourceRootPath);
        //注意window的路径可能是\\，要进行字符串的替换
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        .....
        }
```

接下来就是对模板制作工具的改进，例如已经生成的模板可以继续进行修改，无需重新生成，可以后续多次追加配置，

这个时候我们就要记录文件的修改状态

有状态和无状态

是指程序或请求多次执行时，下一次执行保留对上一次执行的记忆。比如用户登录后服务器会记住用户的信息，下一次请求就能正常使用系统。

与之相对的是无状态。是指每次程序或请求执行，都像是第一次执行一样，没有任何历史信息

有状态的实现

需要两个要素：唯一标识，和存储

唯一表示可以使用我们之前使用雪花算法生成的id

存储就是我们在工作空间创建的文件



### 多次制作实现

如果根据 id 判断出并非首次制作，我们又应该做哪些调整呢？应该如何追加配置和文件呢？

1. 非首次制作，不需要复制原始项目文件
2. 非首次制作，可以在已有模板的基础上再次挖坑
3. 非首次制作，不需要重复输入已有元信息，而是在此基础上覆盖和追加元信息配置

配置文件重复的问题，通过转换为map进行解决

```
private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> files) {
        Collection<Meta.FileConfig.FileInfo> fileInfos = files.stream().collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values();
        return new ArrayList<>(fileInfos);
    }
```

上述代码中，用到了 Java 8 的 Stream API 和 Lambda 表达式来简化代码，其中 Collectors.toMap 表示将列表转换为 Map，详细解释一下：
通过第一个参数（inputPath）作为 key 进行分组
通过第二个参数作为 value 存储值（o -> o 表示使用原对象作为 value）
最后的 (e, r) -> r 其实是 (exist, replacement) -> replacement 的缩写，表示遇到重复的值是保留新值，返回 exist 表示保留旧值。

```java
package com.xin.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xin.maker.meta.Meta;
import com.xin.maker.meta.enums.FileGenerateTypeEnum;
import com.xin.maker.meta.enums.MetaEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 15712
 */
public class TemplateMaker {
    public static void main(String[] args) {
        long l = makeTemplate(1L);

    }


    public static long makeTemplate(Long id) {
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        //业务逻辑
        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() +
                File.separator + "xinbo-generator-demo-projects/acm-template";
        //复制目录
        //目录的唯一性
        //临时工作区目录
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        //判断是否存在目录，不存在就创建
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            //如果不存在说明是首次制作，就直接进行文件的复制，如果存在不需要复制原始项目文件
            FileUtil.copy(originProjectPath, templatePath, true);
        }


        //输入基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";
        //输入文件的信息,目标文件，将这个改为隔离之后的工作目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(new File(originProjectPath).toPath()).toString();
        System.out.println(sourceRootPath);
        //注意window的路径可能是\\，要进行字符串的替换
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        //要挖坑的文件
        String fileInputPath = "src/com/yupi/acm/MainTemplate.java";
        String fileOutputPath = fileInputPath + ".ftl";

        //模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum =");

        //使用字符串替换算法进行替换，通过使用hutool工具
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        //文件输出的绝对路径
        String fileOutPathAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        String fileContent = null;
        //判断模板文件是否被创建出来
        if (FileUtil.exist(fileOutPathAbsolutePath)) {
            //如果模板文件存在就直接读取，在原有的基础上继续追加替换
            fileContent = FileUtil.readUtf8String(fileOutPathAbsolutePath);
        } else {
            //如果模板文件不存在就先读取工作区的项目文件，再进行替换
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String replacement = String.format("{%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "Sum: ", replacement);

        //输出模板文件
        FileUtil.writeUtf8String(newFileContent, fileOutPathAbsolutePath);

        //三.
        //将文件信息提前，不管是第一次修改，还是第二次修改，都可以使用
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        //设置模板的输入路径（这里输入路径应该是模板的输出路径）
        fileInfo.setOutputPath(fileOutputPath);
        //设置生成文件的输出路径（通过模板生成的文件的输出路径）
        fileInfo.setInputPath(fileInputPath);
        //设置文件类型
        fileInfo.setType(MetaEnum.FILE.getValue());
        //设置文件生成类型
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //输出元信息文件
        String metaOutPath = sourceRootPath + File.separator + "meta.json";

        //判断是否是第一次构建元信息文件
        if (FileUtil.exist(metaOutPath)) {
            //如果元信息文件存在就直接读取，在原有的基础上继续追加替换
            String metaContent = FileUtil.readUtf8String(metaOutPath);
            //将元信息文件的内容转换为json对象
            Meta oldMeta = JSONUtil.toBean(metaContent, Meta.class);
            //将文件信息添加到元信息文件当中
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            //去除重复的配置信息
            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));

            //更新元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutPath);
        } else {
            //如果是第一次构建,就重新构造模型参数
            Meta meta = new Meta();
            meta.setName(name);
            meta.setDescription(description);

            //构造模板文件参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            //将文件信息设置到meta元信息中
            meta.setFileConfig(fileConfig);
            //设置源文件位置
            fileConfig.setSourceRootPath(sourceRootPath);
            //创建存放文件信息的列表
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            //将文件信息列表设置到文件信息中
            fileConfig.setFiles(fileInfoList);
            //将文件信息添加到列表当中
            fileInfoList.add(fileInfo);

            //构造模型参数
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            //在元信息中设置模型参数
            meta.setModelConfig(modelConfig);
            //模型参数设置到列表中
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            //将模型参数列表添加到模型参数中
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
            //作为json文件写出
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutPath);
        }
        return id;
    }

    /**
     * 去除重复的文件信息
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

```



将固定参数提取出来

```java
public static long makeTemplate(Meta newMeta,
                                    String originProjectPath,
                                    String fileInputPath,Meta.ModelConfig.ModelInfo modelInfo,String searchStr,Long id) {
```

多个模板的制作，将制作模板的方法抽象出来，通过遍历文件的形式进行制作

```java
private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath,File inputFile) {
...
    return fileinfo;
}
```

将文件信息返回

其中最后一展示为文件类型，如果是相对路径，是找不到对应文件的

sourceRootPath的作用则是将绝对路径替换成相对路径

制作模板部分

这个变量就是文件或者目录的绝对路径

```java
String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;
```

通过hutool来判断是否是目录和文件，如果是目录就遍历目录下的文件，执行模板制作

详情见源码

实现多个文件同时制作，只要把输入路径改为集合即可，在外层多加一个遍历就行



### **文件过滤**

> 需求：控制是否生成帖子相关功能
> 实现思路：允许用户输入一个开关参数来控制帖子功能相关的文件是否生成，比如 PostController、PostService、PostMapper、PostMapper.xml、Post 实体类等。
> 通用能力：某个范围下的多个指定文件挖坑 => 绑定同个参数

过滤文件的json结构

```json
{
  "files": [
    {
      "path": "文件（目录）路径",
      "filters": [
        {
          "range": "fileName",
          "rule": "regex",
          "value": ".*lala.*"
        },
        {
          "range": "fileContent",
          "rule": "contains",
          "value": "haha"
        }
      ]
    }
  ],
}
```

对应到具体的java类,外层

```java
public class TemplateMakerFileConfig {
    private List<FileInfoConfig> fileInfoConfig;

    private static class FileInfoConfig{
        private String path;
        private List<FileFilterConfig> filterConfigList;
    }
}
```

如果想使用 or 逻辑（有一个过滤条件符合要求就保留），可以定义多个重复的 file，并且每个 file 指定一个过滤条件来实现

创建一个枚举值来确定过滤的范围是文件名称还是文件内容





模板制作工具类使用过滤器

因为已经进行了文件的过滤会返回过滤后的路径，并且返回的一定是文件所以之前的逻辑就可以省略一部分

```java
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
```

同时还为了支持文件分组在fileFilterConfig中添加了新的groupconfig





