package ${basePackage}.model;

import lombok.Data;
<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
    <#--其中，modelInfo.defaultValue?c 的作用是将任何类型的变量（比如 boolean 类型和 String 类型）都转换为字符串 -->
</#macro>

/**
 * @author ${author}
 */
@Data
public class DataModel {
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        /**
        * ${modelInfo.groupName}
        */
        public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

        /**
        * ${modelInfo.description}
        */
        @Data
        public static class ${modelInfo.type} {
        <#list modelInfo.models as SubmodelInfo>
            <@generateModel indent="        " modelInfo=SubmodelInfo />
        </#list>
    }
        <#else>
            <@generateModel indent="        " modelInfo=modelInfo />
        </#if>

    </#list>

}
