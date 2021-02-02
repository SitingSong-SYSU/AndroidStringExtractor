package com.ase.plugin.autoSave.component;

import com.ase.plugin.autoSave.model.StorageFactory;
import com.ase.plugin.autoSave.processors.BuildProcessor;
import com.ase.plugin.autoSave.processors.GlobalProcessor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import static com.intellij.AppTopics.FILE_DOCUMENT_SYNC;

/**
 * The plugin entry class that instanciates (or reuse) and delegates to {@link SaveActionManager}. This is not a
 * singleton, for java based ide the corresponding component will also get instanciated (check {@link JavaComponent}).
 * 重用和委托SaveActionManager的插件入口类。不是单例，对于基于java的ide，相应的组件也将得到重用(JavaComponent)
 *
 * @see SaveActionManager
 */
public class Component implements ApplicationComponent {

    public static final String COMPONENT_NAME = "Save Actions";

    @Override
    public void initComponent() {
        System.out.println("~~~~Starting component: " + COMPONENT_NAME);

        SaveActionManager manager = SaveActionManager.getInstance();
        manager.setStorageFactory(StorageFactory.DEFAULT);
        manager.addProcessors(BuildProcessor.stream());
        manager.addProcessors(GlobalProcessor.stream());

        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(FILE_DOCUMENT_SYNC, manager);
    }

    @Override
    public void disposeComponent() {
        System.out.println("~~~~Stopping component: " + COMPONENT_NAME);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

}
