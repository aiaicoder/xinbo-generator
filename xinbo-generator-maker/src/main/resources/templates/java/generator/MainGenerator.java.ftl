package ${basePackage}.generator;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 */
public class MainGenerator {
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
            String inputRootPath = "${fileConfig.inputRootPath}";
            String outputRootPath =  "${fileConfig.outputRootPath}";

            String inputPath;
            String outputPath;
            <#list fileConfig.files as fileInfo>

                inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
                outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
                <#if fileInfo.generateType == "dynamic">
                    DynamicGenerator.doGenerate(inputPath,outputPath,model);
                    <#else >
                    StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
                </#if>
            </#list>
    }

}