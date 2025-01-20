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
        this.load();
    }

    public void createFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                BungeePerms.getInstance().getLogger().info(exception.getMessage());
            }
        }
    }

    public void load() {
        try {
            this.configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException exception) {
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }
    }

    public void createDefaults() {
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
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }
    }

    public void reload() {
        this.load();
    }

    public String getString(String path) {
        return configuration.getString(path);
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }
}
