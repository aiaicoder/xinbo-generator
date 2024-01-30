package com.xin.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.xin.config.MainTemplateConfig;
import com.xin.generator.MainGenerator;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * @author xin
 * 代码文件生成命令
 */
@Command(name = "generate",mixinStandardHelpOptions = true,description = "代码模板文件的生成")
@Data
public class GenerateCommand implements Callable<Integer> {

    //作者名称
    @Option(names = {"-a","--author"},description = "作者名称",arity = "0..1",interactive = true,echo = true)
    private String authorName = "新";

    //是否需要循环
    @Option(names = {"-l","--loop"},description = "是否需要循环",arity = "0..1",interactive = true,echo = true)
    private Boolean loop = false;

    //输出文本
    @Option(names = {"-o","--output"},description = "输出文本",arity = "0..1",interactive = true,echo = true)
    private String outputText = "sum:";

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = BeanUtil.copyProperties(this, MainTemplateConfig.class);
        MainGenerator.doGenerate(mainTemplateConfig);
        System.out.println(mainTemplateConfig);
        return 0;
    }
}
