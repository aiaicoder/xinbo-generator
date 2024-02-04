package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;

import lombok.Data;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
* @author ${author}
* 代码文件生成命令
*/
@Command(name = "generate",mixinStandardHelpOptions = true,description = "代码模板文件的生成")
@Data
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as modelInfo>
    <#if modelInfo.description??>
    /**
    * ${modelInfo.description}
    */
    </#if>
    @Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>,<#if modelInfo.fieldName??>"--${modelInfo.fieldName}"</#if>},arity = "0..1",<#if modelInfo.description??>description = "${modelInfo.description}", </#if>interactive = true,echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
    </#list>

    @Override
    public Integer call() throws Exception {
    DataModel mainTemplateConfig = BeanUtil.copyProperties(this, DataModel.class);
    MainGenerator.doGenerate(mainTemplateConfig);
    System.out.println(mainTemplateConfig);
    return 0;
    }
}
