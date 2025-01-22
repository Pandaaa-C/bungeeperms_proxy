package com.panda0day.bungeePerms.users;

import com.panda0day.bungeePerms.BungeePerms;
import com.panda0day.bungeePerms.groups.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {
    private final String uuid;
    private Group group;
    private final String permissions;

    public User(String uuid, int groupId, String permissions) {
        this.uuid = uuid;
        this.group = BungeePerms.getGroupManager().getGroupById(groupId);
        this.permissions = permissions;
    }

    public String getUuid() {
        return uuid;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setGroupById(int id) {
        this.group = BungeePerms.getGroupManager().getGroupById(id);
    }

    public List<String> getPermissions() {
        if (permissions == null || permissions.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(permissions.split(";")));
    }

    public String getPermissionsAsString() {
        return permissions;
    }
}
