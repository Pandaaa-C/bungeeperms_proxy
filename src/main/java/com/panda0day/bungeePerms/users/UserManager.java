    package com.panda0day.bungeePerms.users;

    import com.panda0day.bungeePerms.BungeePerms;
    import com.panda0day.bungeePerms.groups.Group;
    import net.md_5.bungee.api.connection.ProxiedPlayer;

    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class UserManager {
        private final List<User> users;

        public UserManager() {
            users = new ArrayList<>();
        }

        public void loadUsers() {
            ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM perm_users");
            try {
                while (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    int groupId = resultSet.getInt("group_id");
                    String permissions = resultSet.getString("permissions");

                    users.add(new User(uuid, groupId, permissions));
                }
            } catch (SQLException exception) {
                BungeePerms.getInstance().getLogger().info(exception.getMessage());
            }
        }

        public List<User> getUsers() {
            return users;
        }

        public User getUser(String uuid) {
            return getUsers().stream().filter(u -> u.getUuid().equals(uuid)).findFirst().orElse(null);
        }

        public void addPermission(String uuid, String permission) {
            User user = getUser(uuid);
            List<String> permissions = user.getPermissions();
            if (permissions == null) {
                permissions = new ArrayList<>();
            }

            permissions.add(permission);

            String newPermissions = String.join(";", permissions);
            BungeePerms.getInstance().updatePermissions(uuid, newPermissions);
            BungeePerms.getDatabase().executeQuery("UPDATE perm_users SET permissions = ? WHERE uuid = ?", newPermissions, uuid);
        }

        public void removePermission(String uuid, String permission) {
            User user = getUser(uuid);
            user.getPermissions().remove(permission);

            String newPermissions = String.join(";", user.getPermissions());
            BungeePerms.getDatabase().executeQuery("UPDATE perm_users SET permissions = ? WHERE uuid = ?", newPermissions, uuid);
        }

        public boolean hasPermission(String uuid, String permission) {
            User user = getUser(uuid);
            return user.getPermissions().contains(permission);
        }

        public void addGroup(ProxiedPlayer player, Group group) {
            User user = getUser(player.getUniqueId().toString());
            if (user == null) return;

            group.addMember(player.getUniqueId().toString());
            BungeePerms.getGroupManager().updateGroup(group);

            user.setGroup(group);
            BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET members = ? WHERE name = ?", String.join(";", group.getMembers()), group.getName());
            BungeePerms.getDatabase().executeQuery("UPDATE perm_users SET group_id = ? WHERE uuid = ?", group.getId(), player.getUniqueId().toString());
        }

        public void removeGroup(ProxiedPlayer player, Group group) {
            User user = getUser(player.getUniqueId().toString());
            if (user == null) return;

            group.removeMember(player.getUniqueId().toString());
            BungeePerms.getGroupManager().updateGroup(group);

            user.setGroupById(1);
            BungeePerms.getDatabase().executeQuery("UPDATE perm_groups SET members = ? WHERE name = ?", String.join(";", group.getMembers()), group.getName());
            BungeePerms.getDatabase().executeQuery("UPDATE perm_users SET group_id = ? WHERE uuid = ?", 1, player.getUniqueId().toString());
        }

        public void setSuffix(String uuid, String suffix) {
            User user = getUser(uuid);
            if (user == null) return;

            BungeePerms.getDatabase().executeQuery("UPDATE perm_users SET suffix = ? WHERE uuid = ?", suffix, uuid);
        }

        public void createUser(String uuid) {
            BungeePerms.getDatabase().executeQuery("INSERT INTO perm_users (uuid, group_id, permissions) VALUES (?, ?, ?);", uuid, 1, "");
        }

        public static void createDefaultTable() {
            BungeePerms.getDatabase().createTable("""
                CREATE TABLE IF NOT EXISTS perm_users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(255) NOT NULL UNIQUE,
                    group_id VARCHAR(255) NOT NULL,
                    permissions LONGTEXT NOT NULL
                )
                """);
        }
    }
