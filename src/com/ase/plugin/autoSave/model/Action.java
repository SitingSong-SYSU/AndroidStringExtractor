package com.ase.plugin.autoSave.model;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static com.ase.plugin.autoSave.model.ActionType.global;
import static com.ase.plugin.autoSave.model.ActionType.activation;
import static com.ase.plugin.autoSave.model.ActionType.build;
import static com.ase.plugin.autoSave.model.ActionType.java;
import static java.util.stream.Collectors.toSet;

public enum Action {

    // Activation

    // 在保存时激活保存操作(在保存每个文件之前，执行下面配置的操作)
    activate("Activate save actions on save (before saving each file, performs the configured actions below)",
            activation, true),

    // 使用快捷方式保存，默认 CTRL + SHIFT + S
    activateOnShortcut("Activate save actions on shortcut (default \"CTRL + SHIFT + S\")",
            activation, false),

    // 使用批处理保存 Code > Save Actions > Execute on multiple files
    activateOnBatch("Activate save actions on batch (\"Code > Save Actions > Execute on multiple files\")",
            activation, false),

    // 编译错误（对单个文件）时不进行操作
    noActionIfCompileErrors("No action if compile errors (applied per file)",
            activation, false),

    stringExtractor("字符串格式化", global, false),

    // Global

    // imports优化
    organizeImports("Optimize imports",
            global, true),

    // 格式化文件
    reformat("Reformat file",
            global, false),

    // 只格式化已修改的代码
    reformatChangedCode("Reformat only changed code (only if VCS configured)",
            global, true),

    // 重排字段和方法，在 File > Settings > Editor > Code Style > (...) > Arrangement 配置
    rearrange("Rearrange fields and methods " +
            "(configured in \"File > Settings > Editor > Code Style > (...) > Arrangement\")",
            global, false),

    // Build

    compile("[experimental] Compile files (using \"Build > Build Project\")",
            build, false),

    reload("[experimental] Reload files in running debugger (using \"Run > Reload Changed Classes\")",
            build, false),

    executeAction("[experimental] Execute an action (using quick lists at " +
            "\"File > Settings > Appearance & Behavior > Quick Lists\")",
            build, false),

    // Java fixes

    fieldCanBeFinal("Add final modifier to field",
            java, false),

    localCanBeFinal("Add final modifier to local variable or parameter",
            java, false),

    localCanBeFinalExceptImplicit("Add final modifier to local variable or parameter except if it is implicit",
            java, false),

    methodMayBeStatic("Add static modifier to methods",
            java, false),

    unqualifiedFieldAccess("Add this to field access",
            java, false),

    unqualifiedMethodAccess("Add this to method access",
            java, false),

    // 向静态成员访问添加类限定符
    unqualifiedStaticMemberAccess("Add class qualifier to static member access",
            java, false),

    customUnqualifiedStaticMemberAccess("Add class qualifier to static member access outside declaring class",
            java, false),

    missingOverrideAnnotation("Add missing @Override annotations",
            java, false),

    // 在if/while/for语句中添加空格
    useBlocks("Add blocks to if/while/for statements",
            java, false),

    generateSerialVersionUID("Add a serialVersionUID field for Serializable classes",
            java, false),

    unnecessaryThis("Remove unnecessary this to field and method",
            java, false),

    finalPrivateMethod("Remove final from private method",
            java, false),

    unnecessaryFinalOnLocalVariableOrParameter("Remove unnecessary final to local variable or parameter",
            java, false),

    explicitTypeCanBeDiamond("Remove explicit generic type for diamond",
            java, false),

    suppressAnnotation("Remove unused suppress warning annotation",
            java, false),

    unnecessarySemicolon("Remove unnecessary semicolon",
            java, false),

    singleStatementInBlock("Remove blocks from if/while/for statements",
            java, false),

    accessCanBeTightened("Change visibility of field or method to lower access",
            java, false),

    // settings

//    addImport("添加必要的头文件", java, false),
//
//    replaceRegex("替换字符串格式，使用$id代替原字符串", java, false)

    ;

    private final String text;
    private final ActionType type;
    private final boolean defaultValue;

    Action(String text, ActionType type, boolean defaultValue) {
        this.text = text;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getText() {
        return text;
    }

    public ActionType getType() {
        return type;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public static Set<Action> getDefaults() {
        return Arrays.stream(Action.values())
                .filter(Action::isDefaultValue)
                .collect(toSet());
    }

    public static Stream<Action> stream() {
        return Arrays.stream(values());
    }

    public static Stream<Action> stream(ActionType type) {
        return Arrays.stream(values()).filter(action -> action.type.equals(type));
    }

}
