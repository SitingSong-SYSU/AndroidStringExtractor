package com.ase.plugin.autoSave.component;

import com.ase.plugin.autoSave.processors.JavaProcessor;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import static com.ase.plugin.autoSave.model.StorageFactory.JAVA;

/**
 * The plugin entry class for java based ide. This is not a singleton, the main component {@link Component} is also
 * instanciated before, this one is instanciated after.
 *
 * @see SaveActionManager
 */
public class JavaComponent implements ApplicationComponent {

    private static final String COMPONENT_NAME = "Save Actions Java";

    @Override
    public void initComponent() {
        System.out.println("~~~~Starting component: " + COMPONENT_NAME);

        SaveActionManager manager = SaveActionManager.getInstance();
        manager.setStorageFactory(JAVA);
        manager.enableJava();
        manager.addProcessors(JavaProcessor.stream());
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
