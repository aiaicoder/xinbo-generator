
# ${name}

> ${description}
>
> 作者：${author}
>
> 基于 [小新](https://xinzz.vip/) 的 [新博代码生成器项目](https://github.com/aiaicoder/xinbo-generator) 制作，感谢您的使用！

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <选项参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo><#if modelInfo.groupKey??><#list modelInfo.models as subModelInfo><#if subModelInfo.abbr??> -${subModelInfo.abbr}</#if><#else > -${modelInfo.fieldName}</#list></#if><#if modelInfo.abbr??> -${modelInfo.abbr}</#if><#else> -${modelInfo.fieldName}</#list>
```

## 参数说明
<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
<#list modelInfo.models as subModelInfo>
    ${subModelInfo?index + 1}）${subModelInfo.fieldName}

    类型：${subModelInfo.type}

    描述：${subModelInfo.description}

    默认值：${subModelInfo.defaultValue?c}

<#if subModelInfo.abbr??>
    缩写： -${subModelInfo.abbr}
</#if>
</#list>
<#else>
    ${modelInfo?index + 1}）${modelInfo.fieldName}

    类型：${modelInfo.type}

    描述：${modelInfo.description}

    默认值：${modelInfo.defaultValue?c}

<#if modelInfo.abbr??>
    缩写： -${modelInfo.abbr}
</#if>
    </#if>
        </#list>

欢迎来到我的GitHub项目！

我希望邀请你作为一个社区成员参与其中。你的支持对我来说意义重大，它不仅可以是对我的个人努力的认可，也能激励我继续改进和发展这个项目。

通过给这个项目点一个Star，你不仅可以享受到最新的功能和改进，还可以与我和其他贡献者进行交流，分享你的想法和建议。你的参与将推动这个项目的进一步成长和发展。

让我们一起在这个开源项目的世界里，共同创造出一个令人瞩目的作品！请不要犹豫，点下那个熟悉的Star按钮，成为这个项目的一部分。

感谢你的关注和支持，我期待与你共同参与这个激动人心的开源项目！