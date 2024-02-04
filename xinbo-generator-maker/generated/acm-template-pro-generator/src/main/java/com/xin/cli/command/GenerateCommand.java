package com.xin.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.xin.generator.MainGenerator;
import com.xin.model.DataModel;

import lombok.Data;
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

    /**
    * 是否生成循环
    */
    @Option(names = {"-l","--loop"},arity = "0..1",description = "是否生成循环", interactive = true,echo = true)
    private boolean loop  = false;
    /**
    * 作者注释
    */
    @Option(names = {"-a","--author"},arity = "0..1",description = "作者注释", interactive = true,echo = true)
    private String author  = "xin";
    /**
    * 输出信息
    */
    @Option(names = {"-o","--outputText"},arity = "0..1",description = "输出信息", interactive = true,echo = true)
    private String outputText  = "sum = ";

    @Override
    public Integer call() throws Exception {
    DataModel mainTemplateConfig = BeanUtil.copyProperties(this, DataModel.class);
    MainGenerator.doGenerate(mainTemplateConfig);
    System.out.println(mainTemplateConfig);
    return 0;
    }
}
