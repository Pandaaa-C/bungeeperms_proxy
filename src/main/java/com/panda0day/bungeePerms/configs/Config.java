package com.panda0day.bungeePerms.configs;

public interface Config {
    void createFile();
    void createDefaults();
    void load();
    void save();
    void reload();
}
