package com.xin.cli.command;


import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * @author xin
 * 查看模板文件列表
 */
@Command(name = "list",description = "查看文件列表" , mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String inputPath = new File(parentFile,"xinbo-generator-demo-projects"+File.separator+"acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            //打印文件名称
            System.out.println(file.getName());
        }
    }
}
