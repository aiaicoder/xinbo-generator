package ${basePackage}.model;

import lombok.Data;

/**
 * @author ${author}
 */
@Data
public class DataModel {

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.description??>
        /**
         * ${modelInfo.description}
         */
        </#if>
        private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue?? >= ${modelInfo.defaultValue?c}</#if>;
        <#--其中，modelInfo.defaultValue?c 的作用是将任何类型的变量（比如 boolean 类型和 String 类型）都转换为字符串 -->
    </#list>

}
