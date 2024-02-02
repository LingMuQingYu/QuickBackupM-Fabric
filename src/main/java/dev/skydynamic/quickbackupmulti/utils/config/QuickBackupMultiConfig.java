package dev.skydynamic.quickbackupmulti.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.skydynamic.quickbackupmulti.QbmConstant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class QuickBackupMultiConfig {
    private final Object lock = new Object();
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final Path configPath = QbmConstant.configDir;
    private ConfigStorage configStorage;
    File path = configPath.toFile();
    File config = configPath.resolve("QuickBackupMulti.json").toFile();

    public void load() {
        synchronized (lock) {
            try {
                if (!path.exists() || !path.isDirectory()) {
                    path.mkdirs();
                }
                if (!config.exists()) {
                    saveModifiedConfig(ConfigStorage.DEFAULT);
                }
                var reader = new FileReader(config);
                var result = gson.fromJson(reader, ConfigStorage.class);
                this.configStorage = fixFields(result, ConfigStorage.DEFAULT);
                saveModifiedConfig(this.configStorage);
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveModifiedConfig(ConfigStorage c) {
        synchronized (lock) {
            try {
                if (config.exists()) config.delete();
                if (!config.exists()) config.createNewFile();
                var writer = new FileWriter(config);
                gson.toJson(fixFields(c, ConfigStorage.DEFAULT), writer);
                writer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <T> T fixFields(T t, T defaultVal) {
        if (t == null) {
            throw new NullPointerException();
        }
        if (t == defaultVal) {
            return t;
        }
        try {
            var clazz = t.getClass();
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (Arrays.stream(declaredField.getDeclaredAnnotations()).anyMatch(it -> it.annotationType() == Ignore.class))
                    continue;
                declaredField.setAccessible(true);
                var value = declaredField.get(t);
                var dv = declaredField.get(defaultVal);
                if (value == null) {
                    declaredField.set(t, dv);
                }
            }
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getIgnoredFiles() {
        synchronized (lock) {
            return configStorage.ignoredFiles;
        }
    }

    public int getNumOfSlot() {
        synchronized (lock) {
            return configStorage.numOfSlots;
        }
    }

    public String getLang() {
        synchronized (lock) {
            return configStorage.lang;
        }
    }

    public boolean getScheduleBackup() {
        synchronized (lock) {
            return configStorage.scheduleBackup;
        }
    }

    public String getScheduleCron() {
        synchronized (lock) {
            return configStorage.scheduleCron;
        }
    }

    public int getScheduleInrerval() {
        synchronized (lock) {
            return configStorage.scheduleInterval;
        }
    }

    public String getScheduleMode() {
        synchronized (lock) {
            return configStorage.scheduleMode;
        }
    }

    public void setLang(String lang) {
        synchronized (lock) {
            configStorage.lang = lang;
            saveModifiedConfig(configStorage);
        }
    }

    public void setScheduleCron(String value) {
        synchronized (lock) {
            configStorage.scheduleCron = value;
            saveModifiedConfig(configStorage);
        }
    }

    public void setScheduleInterval(int value) {
        synchronized (lock) {
            configStorage.scheduleInterval = value;
            saveModifiedConfig(configStorage);
        }
    }

    public void setScheduleBackup(boolean value) {
        synchronized (lock) {
            configStorage.scheduleBackup = value;
            saveModifiedConfig(configStorage);
        }
    }

    public void setScheduleMode(String mode) {
        synchronized (lock) {
            configStorage.scheduleMode = mode;
            saveModifiedConfig(configStorage);
        }
    }

}
