package com.xin.maker.generator.main;


/**
 * @author 15712
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String outputSourceRootPath, String jarPath, String shellOutputFilePath) {
        System.out.println("不需要生成精简文件");
    }
}
