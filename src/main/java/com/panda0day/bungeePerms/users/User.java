package com.panda0day.bungeePerms.users;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {
    public String uuid;
    public String suffix;
    public String group;
    public String permissions;

    public User(String uuid, String suffix, String group, String permissions) {
        this.uuid = uuid;
        this.suffix = suffix;
        this.group = group;
        this.permissions = permissions;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getGroup() {
        return group;
    }

    public List<String> getPermissions() {
        return Arrays.asList(permissions.split(";"));
    }

    public String getPermissionsAsString() {
        return permissions;
    }
}
