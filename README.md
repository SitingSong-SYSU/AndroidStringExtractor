# AndroidStringExtractor 插件

- 特别感谢[https://github.com/a-voyager/StringExtractor](String Extractor 插件)

-  批量完成项目内中文硬编码替换

## 使用方法

- 设置翻译api（以百度翻译为例）

    - TransApi 中设置api链接
    - Translator 中设置 appId 及 securityKey

- *.java文件

    - JavaWriter 中设置增加的头文件
    - StringsExtractorAction#findFile 方法中过滤模块文件
    - JavaFieldFinder 中设置匹配正则

- *.xml文件
    - LayoutXmlFieldFinder 中设置匹配的正则
