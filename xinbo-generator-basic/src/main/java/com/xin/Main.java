package com.xin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.xin.generator.StaticGenerator.copyFilesByRecursive;

/**
 * @author 15712
 */
public class Main {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        String acmTemplateFile = new File(parentFile, "xinbo-generator-demo-projects"+File.separator+"acm-template").getAbsoluteFile().getAbsolutePath();
        copyFilesByRecursive(acmTemplateFile, projectPath );
    }
}

