package com.panda0day.bungeePerms.groups;

import com.panda0day.bungeePerms.BungeePerms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {
    private final List<Group> groups;

    public GroupManager() {
        this.groups = new ArrayList<>();
    }

    public void loadGroups() {
        ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM perm_groups");

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String prefix = resultSet.getString("prefix");
                String suffix = resultSet.getString("suffix");
                String members = resultSet.getString("members");
                String permissions = resultSet.getString("permissions");

                List<String> memberList = new ArrayList<>();
                if (!members.isEmpty()) {
                    memberList = Arrays.asList(members.split(";"));
                }

                List<String> permissionList = new ArrayList<>();
                if (!permissions.isEmpty()) {
                    permissionList = Arrays.asList(permissions.split(";"));
                }

                groups.add(new Group(id, name, prefix, suffix, memberList, permissionList));
                BungeePerms.getInstance().getLogger().info("Loaded group " + name);
            }
        } catch (SQLException exception) {
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }

    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group getGroup(String groupName) {
        List<Group> groups = getGroups();
        return groups.stream().filter(group -> group.getName().equals(groupName)).findFirst().orElse(null);
    }

    public Group getGroupById(int groupId) {
        List<Group> groups = getGroups();
        return groups.stream().filter(group -> group.getId() == groupId).findFirst().orElse(null);
    }

    public void addGroup(String name, String prefix, String suffix, String members, String permissions) {
        BungeePerms.getDatabase().executeQuery("INSERT INTO perm_groups (name, prefix, suffix, members, permissions) VALUES (?, ?, ?, ?, ?)",
                name,
                prefix,
                suffix,
                members,
                permissions
        );
    }

    public void removeGroup(String groupName) {
        List<Group> groups = getGroups();
        groups.removeIf(group -> group.getName().equals(groupName));

        BungeePerms.getDatabase().executeQuery("DELETE FROM perm_groups WHERE name = ?", groupName);
    }

    public void updateGroup(Group group) {
        List<Group> groups = getGroups();
        groups.remove(group);
        groups.add(group);
    }

    public void setPrefix(String groupName, String prefix) {
        Group group = getGroup(groupName);
        if (group == null) return;

        group.setPrefix(prefix);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET prefix = ? WHERE name = ?", prefix, groupName);
        updateGroup(group);
    }

    public void setSuffix(String groupName, String suffix) {
        Group group = getGroup(groupName);
        if (group == null) return;

        group.setSuffix(suffix);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET suffix = ? WHERE name = ?", suffix, groupName);
        updateGroup(group);
    }

    public void addPermission(String groupName, String permission) {
        Group group = getGroup(groupName);
        if (group == null) return;

        List<String> permissions = new ArrayList<>();
        permissions.addAll(group.getPermissions());
        permissions.add(permission);
        if (permissions.isEmpty()) {
            permissions = new ArrayList<>();
        }
        group.setPermissions(permissions);

        String newPermissions = String.join(";", permissions);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET permissions = ? WHERE name = ?", newPermissions, groupName);
    }

    public void removePermission(String groupName, String permission) {
        Group group = getGroup(groupName);
        if (group == null) return;

        List<String> permissions = new ArrayList<>();
        permissions.addAll(group.getPermissions());
        permissions.remove(permission);
        if (permissions.isEmpty()) {
            permissions = new ArrayList<>();
        }

        group.setPermissions(permissions);

        String newPermissions = String.join(";", permissions);
        BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET permissions = ? WHERE name = ?", newPermissions, groupName);
    }

    public static void createDefaultTable() {
        BungeePerms.getDatabase().createTable("""
               CREATE TABLE IF NOT EXISTS perm_groups (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    prefix VARCHAR(255) NOT NULL,
                    suffix VARCHAR(255) NOT NULL,
                    members LONGTEXT NULL,
                    permissions LONGTEXT NULL
                )
               """);
    }
}
