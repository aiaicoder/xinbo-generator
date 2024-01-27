package com.xin.cli.example;

import cn.hutool.core.util.ReflectUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.Arrays;
// some exports omitted for the sake of brevity

@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true) 
public class ASCIIArt implements Runnable {


    /**
     * names 表示Option的参数名，可以有多个
     * description 表示Option的描述
     */
    @Option(names = { "-s", "--font-size" }, description = "Font size") 
    int fontSize = 19;


    /**
     * @Parameters的paramLabel参数，为参数标签，类似于描述信息
     *  defaultValue 默认值
     */
    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli", 
               description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" };

    /**
     * 这里就是命令处理方法，所有命令的逻辑都是写在run方法中
     */
    @Override
    public void run() { 
        // The business logic of the command goes here...
        // In this case, code for generation of ASCII art graphics
        // (omitted for the sake of brevity).
        System.out.println(fontSize);
        System.out.println(Arrays.toString(words));
    }

    public static void main(String[] args) {
        args = new String[] { "-s", "20", "你好吗","啦啦啦" };
        //利用hutool的工具可以获得注解信息
//        Field field = ReflectUtil.getField(ASCIIArt.class,"fontSize");
//        Option annotation = field.getAnnotation(Option.class);
//        String[] names = annotation.names();
//        System.out.println(Arrays.toString(names));
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.exit(exitCode);
    }
}