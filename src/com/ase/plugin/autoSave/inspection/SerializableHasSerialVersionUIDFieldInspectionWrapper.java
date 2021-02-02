package com.ase.plugin.autoSave.inspection;


import com.intellij.codeInspection.LocalInspectionTool;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class changes package between intellij versions.
 * 这个类在intellij版本之间更改包。
 *
 * @see com.siyeh.ig.serialization.SerializableHasSerialVersionUIDFieldInspection
 */
public class SerializableHasSerialVersionUIDFieldInspectionWrapper {

    private static final String CLASS_NAME_INTELLIJ_2016 = "com.siyeh.ig.serialization."
            + "SerializableHasSerialVersionUIDFieldInspectionBase";
    private static final String CLASS_NAME_INTELLIJ_2018_3 = "com.siyeh.ig.serialization."
            + "SerializableHasSerialVersionUIDFieldInspection";

    public static LocalInspectionTool get() {
        return Stream.of(CLASS_NAME_INTELLIJ_2016, CLASS_NAME_INTELLIJ_2018_3)
                .map(SerializableHasSerialVersionUIDFieldInspectionWrapper::getInspectionInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(inspectionTool -> System.out.println("~~~~Found serial version uid class " + inspectionTool.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot find inspection tool SerializableHasSerialVersionUIDFieldInspection"));
    }

    private static Optional<LocalInspectionTool> getInspectionInstance(String className) {
        try {
            Class<? extends LocalInspectionTool> inspectionClass = Class
                    .forName(className).asSubclass(LocalInspectionTool.class);
            LocalInspectionTool localInspectionTool = inspectionClass.cast(inspectionClass.newInstance());
            return Optional.of(localInspectionTool);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return Optional.empty();
        }
    }

}
