package com.ase.plugin.autoSave.model;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.util.Collections.emptyList;

public enum EpfStorage {

    INSTANCE;

    public Storage getStorageOrDefault(String configurationPath, Storage defaultStorage) {
        try {
            return getStorageOrDefault0(configurationPath, defaultStorage);
        } catch (IOException e) {
            System.out.println("~~~~Error in configuration file " + defaultStorage.getConfigurationPath());
            return defaultStorage;
        }
    }

    private Storage getStorageOrDefault0(String configurationPath, Storage defaultStorage) throws IOException {
        if ("".equals(configurationPath) || configurationPath == null) {
            return defaultStorage;
        }
        Storage storage = new Storage(defaultStorage);
        Properties properties = readProperties(configurationPath);
        Action.stream().forEach(action -> storage.setEnabled(action, isEnabledInEpf(properties, action)
                .orElse(defaultStorage.isEnabled(action))));
        return storage;
    }

    private Optional<Boolean> isEnabledInEpf(Properties properties, Action action) {
        List<EpfKey> epfKeys = EpfAction.getEpfActionForAction(action).map(EpfAction::getEpfKeys).orElse(emptyList());
        for (EpfKey epfKey : epfKeys) {
            if (isEnabledInEpf(properties, epfKey, true)) {
                return Optional.of(true);
            }
            if (isEnabledInEpf(properties, epfKey, false)) {
                return Optional.of(false);
            }
        }
        return Optional.empty();
    }

    private boolean isEnabledInEpf(Properties properties, EpfKey key, boolean value) {
        return EpfKey.getPrefixes().stream()
                .anyMatch(prefix -> String.valueOf(value).equals(properties.getProperty(prefix + "." + key)));
    }

    private Properties readProperties(String configurationPath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(configurationPath)) {
            properties.load(in);
        }
        return properties;
    }

}
