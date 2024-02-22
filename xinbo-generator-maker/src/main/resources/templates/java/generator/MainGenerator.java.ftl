package ${basePackage}.generator;
import freemarker.template.TemplateException;
import com.xin.model.DataModel;
import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 */
<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicGenerator.doGenerate(inputPath,outputPath,model);
<#else>
${indent}StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
</#if>
</#macro>
public class MainGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(DataModel model) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath =  "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
            <#list modelInfo.models as subModelInfo>
            ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
            </#list>
        <#else >
            ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
        </#list>
        <#list fileConfig.files as fileInfo>
            <#if fileInfo.groupKey??>
                <#if fileInfo.condition??>
        if(${fileInfo.condition}){
                    <#list fileInfo.files as fileInfo>
                        <@generateFile fileInfo=fileInfo indent="            " />
                    </#list>
        }
                <#else>
                    <#--有groupkey但是没有condition，直接生成-->
                    <#list fileInfo.files as fileInfo>
                        <@generateFile fileInfo=fileInfo indent="            " />
                    </#list>
                </#if>
                <#else >
                    <#if fileInfo.condition??>
                        if(${fileInfo.condition}){
                            <@generateFile fileInfo=fileInfo indent="        " />
                        }
                        <#else>
                            <@generateFile fileInfo=fileInfo indent="        " />
                    </#if>
            </#if>

        </#list>
    }

}