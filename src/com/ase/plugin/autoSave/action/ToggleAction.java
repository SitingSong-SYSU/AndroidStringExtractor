package com.ase.plugin.autoSave.action;


import com.ase.plugin.autoSave.model.Storage;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import static com.ase.plugin.autoSave.model.Action.activate;

/**
 * This action toggles on and off the plugin, by modifying the underlying storage.
 * 此操作通过修改底层存储来切换插件的开启和关闭。
 */
public class ToggleAction extends com.intellij.openapi.actionSystem.ToggleAction implements DumbAware {

    @Override
    public boolean isSelected(AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            Storage storage = ServiceManager.getService(project, Storage.class);
            return storage.isEnabled(activate);
        }
        return false;
    }

    @Override
    public void setSelected(AnActionEvent event, boolean state) {
        Project project = event.getProject();
        if (project != null) {
            Storage storage = ServiceManager.getService(project, Storage.class);
            storage.setEnabled(activate, state);
        }
    }

}
