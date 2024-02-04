package com.xin.maker.generator;


import com.xin.maker.utils.SystemUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author 15712
 */
public class JarGenerator {
    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand;
        int os = SystemUtils.checkOs();
        if (os == 1) {
            // Windows操作系统
            mavenCommand = winMavenCommand;
        } else {
            // 其他操作系统
            mavenCommand = otherMavenCommand;
        }
        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        //设置命令的执行目录
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        //读取命令输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String readLine;
        while ((readLine = reader.readLine()) != null){
            System.out.println(readLine);
        }
        //等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("命令执行完成，退出码：" + exitCode);
    }
}

