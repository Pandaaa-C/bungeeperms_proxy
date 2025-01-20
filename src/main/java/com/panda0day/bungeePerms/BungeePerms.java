package com.panda0day.bungeePerms;

import com.panda0day.bungeePerms.configs.DatabaseConfig;
import com.panda0day.bungeePerms.groups.Group;
import com.panda0day.bungeePerms.groups.GroupManager;
import com.panda0day.bungeePerms.users.User;
import com.panda0day.bungeePerms.utils.Database;
import com.panda0day.bungeePerms.users.UserManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class BungeePerms extends Plugin implements Listener {
    private static final String CHANNEL = "bungee:permissions";

    private static BungeePerms instance;

    private static DatabaseConfig databaseConfig;
    private static Database database;

    private static UserManager userManager;
    private static GroupManager groupManager;

    private final Map<String, String> cachedPrefix = new HashMap<>();
    private final Map<String, String> cachedSuffix = new HashMap<>();
    private final Map<String, String> cachedPermissions = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("BungeePerms plugin has been enabled!");
        getProxy().registerChannel(CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);

        databaseConfig = new DatabaseConfig("database.yml");
        loadDatabase();

        userManager = new UserManager();
        groupManager = new GroupManager();
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equals(getChannel())) return;
        if ((event.getSender() instanceof Server server)) {
            String message = new String(event.getData());
            getLogger().info("Received message: " + message);

            String[] data = message.split(";");
            if (data.length < 2) {
                getLogger().warning("Invalid message received: " + message);
                return;
            }
            String action = data[0];
            String playerName = data[1];
            String response = "";

            switch (action) {
                case "getPrefix" -> response = getPrefix(playerName);
                case "getSuffix" -> response = getSuffix(playerName);
            }

            if (response.isEmpty()) {
                response = "null";
            }

            server.sendData(getChannel(), (action + ";" + response).getBytes());
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player == null) return;

        User user = getUserManager().getUser(player.getUniqueId().toString());
        if (user == null) return;

        Group group = getGroupManager().getGroup(user.getGroup());
        if (group == null) return;

        cachedPrefix.put(player.getUniqueId().toString(), group.getPrefix());
        cachedSuffix.put(player.getUniqueId().toString(), user.getSuffix());

        List<String> groupPermissions = group.getPermissions();
        List<String> userPermissions = user.getPermissions();

        List<String> combinedPermissions = new ArrayList<>();
        groupPermissions.stream().map(combinedPermissions::add);
        userPermissions.stream().map(combinedPermissions::add);

        String permissions = String.join(";", combinedPermissions);
        cachedPermissions.put(player.getUniqueId().toString(), permissions);
    }

    @EventHandler
    public void onPlayerJoin(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                // TODO: add logic to get users group settings, such as Prefix, Suffix, Group Permissions and User Permissions
                String prefix = getPrefix(player.getUniqueId().toString());
                String suffix = getSuffix(player.getUniqueId().toString());
                String permissions = getPermissions(player.getUniqueId().toString());

                sendPrefixSuffix(player, prefix, suffix);
                sendPermissions(player, permissions);
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void sendPrefixSuffix(ProxiedPlayer player, String prefix, String suffix) {
        String playerName = player.getName();
        if (prefix.isEmpty()) {
            prefix = "§r";
        }

        if (suffix.isEmpty()) {
            suffix = "§r";
        }

        String message = "setSuffixPrefix;" + playerName + ";" + prefix + ";" + suffix;
        player.getServer().sendData(getChannel(), message.getBytes(StandardCharsets.UTF_8));
    }

    private void sendPermissions(ProxiedPlayer player, String permissions) {
        if (player != null && player.getServer() != null) {
            String message = "setPermissions;" + player.getUniqueId().toString() + ";" + permissions;
            player.getServer().sendData(getChannel(), message.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void loadDatabase() {
        database = new Database(
                getDatabaseConfig().getString("database.host"),
                getDatabaseConfig().getString("database.user"),
                getDatabaseConfig().getString("database.pass"),
                getDatabaseConfig().getInt("database.port"),
                getDatabaseConfig().getString("database.name")
        );

        database.connect();
    }

    public String getPrefix(String playerUuid) {
        return cachedPermissions.getOrDefault(playerUuid, "");
    }

    public String getSuffix(String playerUuid) {
        return cachedSuffix.getOrDefault(playerUuid, "");
    }
    public String getPermissions(String playerUuid) {
        return cachedPermissions.getOrDefault(playerUuid, "");
    }

    public static String getChannel() {
        return CHANNEL;
    }

    public static BungeePerms getInstance() {
        return instance;
    }

    public static DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public static Database getDatabase() {
        return database;
    }

    public static UserManager getUserManager() {
        return userManager;
    }

    public static GroupManager getGroupManager() {
        return groupManager;
    }
}
