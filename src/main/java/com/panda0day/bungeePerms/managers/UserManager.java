package com.panda0day.bungeePerms.managers;

import com.panda0day.bungeePerms.BungeePerms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserManager {
    public List<String> getUserPermissions(String playerName) {
        ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM permissions WHERE name = ?;", playerName);
        List<String> _perms = new ArrayList<>();

        try {
            while(resultSet.next()) {
                String permissions = resultSet.getString("permissions");
                String[] perms = permissions.split(",");

                Arrays.stream(perms).map(String::trim).forEach(_perms::add);
            }
        } catch (SQLException exception) {
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }

        return _perms;
    }

    public void createPermissions(String playerName) {
        BungeePerms.getDatabase().executeQuery("INSERT INTO permissions (name, permissions) VALUES (?, ?);", playerName, "");
    }

    public void addPermission(String playerName, String permission) {
        List<String> oldPermissions = getUserPermissions(playerName);
        oldPermissions.add(permission);

        String permissions = String.join(";", oldPermissions);
        BungeePerms.getDatabase().executeQuery("UPDATE permissions SET permissions = ? WHERE name = ?;", permissions, playerName);
    }

    public void removePermission(String playerName, String permission) {
        List<String> oldPermissions = getUserPermissions(playerName);
        oldPermissions.remove(permission);

        String permissions = String.join(";", oldPermissions);
        BungeePerms.getDatabase().executeQuery("UPDATE permissions SET permissions = ? WHERE name = ?;", permissions, playerName);
    }

    public String getUserSuffix(String playerName) {
        ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM permissions WHERE name = ?;", playerName);

        try {
            while (resultSet.next()) {
                return resultSet.getString("suffix");
            }
        } catch (SQLException exception) {
            BungeePerms.getInstance().getLogger().info(exception.getMessage());
        }

        return null;
    }

    public void setUserSuffix(String playerName, String suffix) {
        BungeePerms.getDatabase().executeQuery("UPDATE permissions SET suffix = ? WHERE name = ?;", suffix, playerName);
    }
}
