package com.xin.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * @author xin
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
        FileUtil.copy(inputFile, outputFile, false);
    }


}

