package com.panda0day.bungeePerms.configs;

import com.panda0day.bungeePerms.BungeePerms;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DatabaseConfig implements Config {
    private final File file;
    private Configuration configuration;

    public DatabaseConfig(String fileName) {
        this.file = new File(BungeePerms.getInstance().getDataFolder(), fileName);

        this.createFile();
    }

    public void createFile() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                BungeePerms.getInstance().getLogger().info("Created new configuration file: " + file.getAbsolutePath());
            } catch (IOException exception) {
                BungeePerms.getInstance().getLogger().severe("Failed to create configuration: " + exception.getMessage());
            }
        }

        this.load();
        this.createDefaults();
        this.save();
    }

    public void load() {
        try {
            this.configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException exception) {
            BungeePerms.getInstance().getLogger().severe("Failed to load configuration: " + exception.getMessage());
            this.configuration = null;
        }
    }

    public void createDefaults() {
        if (configuration == null) {
            BungeePerms.getInstance().getLogger().severe("Configuration is null, cannot set defaults.");
            return;
        }

        if (configuration.get("database") == null) {
            configuration.set("database.host", "localhost");
            configuration.set("database.port", 3306);
            configuration.set("database.name", "bungee_perms");
            configuration.set("database.user", "root");
            configuration.set("database.password", "");
        }
    }

    public void save() {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
        } catch (IOException exception) {
            BungeePerms.getInstance().getLogger().severe("Failed to save configuration: " + exception.getMessage());
        }
    }

    public void reload() {
        this.load();
    }

    public String getString(String path) {
        return configuration != null ? configuration.getString(path) : null;
    }

    public int getInt(String path) {
        return configuration != null ? configuration.getInt(path) : 0;
    }

    public boolean getBoolean(String path) {
        return configuration != null && configuration.getBoolean(path);
    }
}
