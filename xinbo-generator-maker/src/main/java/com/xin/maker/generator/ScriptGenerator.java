package com.xin.maker.generator;

import cn.hutool.core.io.FileUtil;
import com.xin.maker.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * @author 15712
 */
public class ScriptGenerator {
    public static void doGenerate(String outputPath, String jarPath){
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/bash").append("\n");
            sb.append(String.format("java -jar %s \"$@\"", jarPath)).append("\n");
            FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath);
            // 添加可执行权限
            try {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
            } catch (Exception e) {
                System.out.println(e);
            }
            // windows
            sb = new StringBuilder();
            sb.append("@echo off").append("\n");
            sb.append(String.format("java -jar %s %%*", jarPath)).append("\n");
            FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");
    }
}
