package com.xin.generator;

import cn.hutool.core.util.ArrayUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author 15712
 */
public class StaticGenerator {


    /**
     * 文件 A => 目录 B，则文件 A 放在目录 B 下
     * 文件 A => 文件 B，则文件 A 覆盖文件 B
     * 目录 A => 目录 B，则目录 A 放在目录 B 下
     * 递归复制文件
     * @param inputPath 输入的文件路径
     * @param outputPath 复制到目标文件的路径
     *
     */
    public static void copyFilesByRecursive(String inputPath, String outputPath) {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        copyFileByRecursive(inputFile, outputFile);
    }

    public static void copyFileByRecursive(File  inputFile, File outputFile) {
        // 递归复制文件
        if (inputFile.isDirectory()){
            //输出文件名
            System.out.println(inputFile.getName());
            File outputFileDest = new File(outputFile, inputFile.getName());
            // 如果是目录，首先创建目标目录
            if (!outputFileDest.exists()){
                outputFileDest.mkdirs();
            }
            File[] files = inputFile.listFiles();
            //文件为空直接返回
            if (ArrayUtil.isEmpty(files)){
                return;
            }
            for (File file : files) {
                copyFileByRecursive(file, outputFileDest);
            }
        }else {
            try {
                //如果是文件那么就直接复制到当前目录下
                Path resolve = outputFile.toPath().resolve(inputFile.getName());
                Files.copy(inputFile.toPath(), resolve, StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }


}

