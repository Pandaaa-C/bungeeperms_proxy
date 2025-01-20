package com.panda0day.bungeePerms.groups;

import com.panda0day.bungeePerms.BungeePerms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {
    public List<Group> getGroups() {
        ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM perm_groups");
        List<Group> groups = new ArrayList<>();

        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String prefix = resultSet.getString("prefix");
                String members = resultSet.getString("members");
                String permissions = resultSet.getString("permissions");

                List<String> memberList = Arrays.asList(members.split(";"));
                List<String> permissionList = Arrays.asList(permissions.split(";"));

                groups.add(new Group(name, prefix, memberList, permissionList));
                BungeePerms.getInstance().getLogger().info("Loaded group " + name);
            }
        } catch (SQLException exception) {
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }

        return groups;
    }

    public Group getGroup(String groupName) {
        List<Group> groups = getGroups();
        return groups.stream().filter(group -> group.getName().equals(groupName)).findFirst().orElse(null);
    }

    public void addGroup(Group group) {BungeePerms.getDatabase().executeQuery("INSERT INTO perm_groups (name, prefix, members, permissions) VALUES (?, ?, ?, ?)",
                group.getName(),
                group.getPrefix(),
                group.getMembers(),
                group.getPermissions()
        );
    }

    public void removeGroup(String groupName) {
        List<Group> groups = getGroups();
        groups.removeIf(group -> group.getName().equals(groupName));

        BungeePerms.getDatabase().executeQuery("DELETE FROM perm_groups WHERE name = ?", groupName);
    }

    public void setPrefix(String groupName, String prefix) {
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET prefix = ? WHERE name = ?", prefix, groupName);
    }

    public void addPermission(String groupName, String permission) {
        Group group = getGroup(groupName);
        List<String> permissions = Arrays.asList(permission.split(";"));
        permissions.add(permission);

        String newPermissions = String.join(";", permissions);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET permissions = ? WHERE name = ?", newPermissions, groupName);
    }

    public void removePermission(String groupName, String permission) {
        Group group = getGroup(groupName);
        List<String> permissions = Arrays.asList(permission.split(";"));
        permissions.remove(permission);

        String newPermissions = String.join(";", permissions);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET permissions = ? WHERE name = ?", newPermissions, groupName);
    }
}
