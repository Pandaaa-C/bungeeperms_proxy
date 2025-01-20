package com.panda0day.bungeePerms.groups;

import java.util.List;

public class Group {
    public String name;
    public String prefix;
    public List<String> members;
    public List<String> permissions;

    public Group(String name, String prefix, List<String> members, List<String> permissions) {
        this.name = name;
        this.prefix = prefix;
        this.members = members;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
