package com.xin.cli.example;

import freemarker.template.utility.StringUtil;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"},arity = "0..1" ,description = "Passphrase", interactive = true,prompt = "请输入密码:")
    String password;

    @Option(names = {"-c", "--check"}, description = "CheckPassword",arity = "0..1", interactive = true)
    String checkPassword;


    @Override
    public Integer call() throws Exception {
        System.out.println("用户名"+user);
        System.out.println("密码"+password);
        System.out.println("密码"+checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        args = new String[]{"-u","xinxin","-c","123456"};
        new CommandLine(new Login()).execute(addArgs(Login.class, args));
    }

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
}