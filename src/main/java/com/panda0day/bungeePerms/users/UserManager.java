    package com.panda0day.bungeePerms.users;

    import com.panda0day.bungeePerms.BungeePerms;

    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class UserManager {
        public List<User> getUsers() {
            List<User> users = new ArrayList<>();

            ResultSet resultSet = BungeePerms.getDatabase().executeQuery("SELECT * FROM perm_users");
            try {
                while (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    String group = resultSet.getString("group");
                    String suffix = resultSet.getString("suffix");
                    String permissions = resultSet.getString("permissions");

                    users.add(new User(uuid, group, suffix, permissions));
                }
            } catch (SQLException exception) {
                BungeePerms.getInstance().getLogger().info(exception.getMessage());
            }

            return users;
        }

        public User getUser(String uuid) {
            return getUsers().stream().filter(u -> u.getUuid().equals(uuid)).findFirst().orElse(null);
        }
    }
