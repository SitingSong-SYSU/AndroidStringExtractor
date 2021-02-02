package com.ase.plugin.autoSave.model;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;

public enum ExecutionMode {

    /**
     * When the plugin is called normaly (the IDE calls the plugin component on frame deactivation or "save all"). The
     * {@link #saveSingle} is also called on every documents.
     *
     * @see FileDocumentManager#saveAllDocuments()
     */
    saveAll,

    /**
     * When the plugin is called only with a single save (some other plugins like ideavim do that).
     * 当仅通过一次保存调用插件时(其他一些插件如ideavim会这样做)
     *
     * @see FileDocumentManager#saveDocument(Document)
     */
    saveSingle,

    /**
     * When the plugin is called in batch mode (the IDE calls the plugin after a file selection popup).
     * 在批处理模式下调用插件时(IDE在弹出文件选择后调用插件)。
     */
    batch,

    /**
     * When the plugin is called from a user input shortcut.
     * 当从用户输入快捷方式调用插件时。
     */
    shortcut,

    ;

}
