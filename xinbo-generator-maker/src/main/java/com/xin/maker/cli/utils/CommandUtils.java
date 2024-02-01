package com.xin.maker.cli.utils;

import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * @author xin
 */
public class CommandUtils {
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
            if (field.isAnnotationPresent(CommandLine.Option.class)){
                CommandLine.Option annotation = field.getAnnotation(CommandLine.Option.class);
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
