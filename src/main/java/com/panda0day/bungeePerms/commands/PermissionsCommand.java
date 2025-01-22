package com.panda0day.bungeePerms.commands;

import com.panda0day.bungeePerms.BungeePerms;
import com.panda0day.bungeePerms.groups.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PermissionsCommand extends Command {
    public PermissionsCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sendMessage(sender, ChatColor.RED + "You must be a player to use this command!");
            return;
        }

        if (!player.hasPermission("bungeeperms.admin")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        if (args.length == 0) {
            sendUsageMessage(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "group" -> handleGroupCommand(player, args);
            case "help" -> sendHelpMessage(player);
            default -> sendMessage(player, ChatColor.RED + "Unknown command! Use /bungeeperms help for available commands.");
        }
    }

    private void handleGroupCommand(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group <action> [arguments]");
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "create" -> {
                if (args.length < 3) {
                    sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group create <name>");
                    return;
                }
                handleGroupCreate(player, args[2]);
            }
            case "delete" -> {
                if (args.length < 3) {
                    sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group delete <name>");
                    return;
                }
                handleGroupRemove(player, args[2]);
            }
            case "set" -> handleGroupSet(player, args);
            case "permission" -> handleGroupPermission(player, args);
            case "member" -> handleGroupMember(player, args);
            default -> sendMessage(player, ChatColor.RED + "Unknown group action! Use /bungeeperms help for available commands.");
        }
    }

    private void handleGroupSet(ProxiedPlayer player, String[] args) {
        if (args.length < 5) {
            sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group set <group> <prefix|suffix> <value>");
            return;
        }

        String type = args[2].toLowerCase();
        String group = args[3];

        StringBuilder value = new StringBuilder();
        for (int i = 4; i < args.length; i++) {
            value.append(" ").append(args[i]);
        }

        switch (type) {
            case "prefix" -> handleGroupSetPrefix(player, group, value.toString().trim());
            case "suffix" -> handleGroupSetSuffix(player, group, value.toString().trim());
            default -> sendMessage(player, ChatColor.RED + "Invalid type! Use 'prefix' or 'suffix'.");
        }
    }

    private void handleGroupMember(ProxiedPlayer player, String[] args) {
        if (args.length < 5) {
            sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group member <add/remove> <group> <name>");
            return;
        }

        String type = args[2].toLowerCase();
        String group = args[3];
        String name = args[4];

        switch (type) {
            case "add" -> handleGroupAddMember(player, group, name.trim());
            case "remove" -> handleGroupRemoveMember(player, group, name.trim());
            default -> sendMessage(player, ChatColor.RED + "Invalid type! Use 'add' or 'remove'.");
        }
    }

    private void handleGroupPermission(ProxiedPlayer player, String[] args) {
        if (args.length < 5) {
            sendMessage(player, ChatColor.RED + "Usage: /bungeeperms group permission <add/remove> <group> <value>");
            return;
        }

        String type = args[2].toLowerCase();
        String group = args[3];
        String value = args[4];

        switch (type) {
            case "add" -> handleGroupAddPermission(player, group, value.trim());
            case "remove" -> handleGroupRemovePermission(player, group, value.trim());
            default -> sendMessage(player, ChatColor.RED + "Invalid type! Use 'add' or 'remove'.");
        }
    }

    private void handleGroupCreate(ProxiedPlayer player, String groupName) {
        if (!player.hasPermission("bungeeperms.admin.group.create")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        BungeePerms.getGroupManager().addGroup(groupName, "&f", "&f", "", "");
        sendMessage(player, ChatColor.GREEN + "Group " + ChatColor.GOLD + groupName + ChatColor.GREEN + " has been created!");
    }

    private void handleGroupRemove(ProxiedPlayer player, String groupName) {
        if (!player.hasPermission("bungeeperms.admin.group.remove")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        BungeePerms.getGroupManager().removeGroup(groupName);
        sendMessage(player, ChatColor.GREEN + "Group " + ChatColor.GOLD + groupName + ChatColor.GREEN + " has been deleted!");
    }

    private void handleGroupAddPermission(ProxiedPlayer player, String groupName, String permission) {
        handleGroupSetPermission(player, groupName, permission, "add");
    }

    private void handleGroupRemovePermission(ProxiedPlayer player, String groupName, String permission) {
        handleGroupSetPermission(player, groupName, permission, "remove");
    }

    private void handleGroupSetPermission(ProxiedPlayer player, String groupName, String permission, String attribute) {
        if (!player.hasPermission("bungeeperms.admin.group.set")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        Group group = BungeePerms.getGroupManager().getGroup(groupName);
        if (group == null) {
            sendMessage(player, ChatColor.RED + "That group does not exist!");
            return;
        }

        switch (attribute) {
            case "add" -> {
                BungeePerms.getGroupManager().addPermission(groupName, permission);
                player.sendMessage(new TextComponent(ChatColor.GREEN + "Added permission to group " + groupName));
            }
            case "remove" -> {
                BungeePerms.getGroupManager().removePermission(groupName, permission);
                player.sendMessage(new TextComponent(ChatColor.GREEN + "Removed permission from group " + groupName));
            }
            default -> sendMessage(player, ChatColor.RED + "Invalid type! Use 'add' or 'remove'.");
        }
    }

    private void handleGroupAddMember(ProxiedPlayer player, String groupName, String permission) {
        handleGroupSetMember(player, groupName, permission, "add");
    }

    private void handleGroupRemoveMember(ProxiedPlayer player, String groupName, String permission) {
        handleGroupSetMember(player, groupName, permission, "remove");
    }

    private void handleGroupSetMember(ProxiedPlayer player, String groupName, String member, String attribute) {
        if (!player.hasPermission("bungeeperms.admin.group.set")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        Group group = BungeePerms.getGroupManager().getGroup(groupName);
        if (group == null) {
            sendMessage(player, ChatColor.RED + "That group does not exist!");
            return;
        }

        ProxiedPlayer target = BungeePerms.getInstance().getProxy().getPlayer(member);
        if (target == null) {
            sendMessage(player, ChatColor.RED + "That member does not exist!");
            return;
        }

        switch (attribute) {
            case "add" -> {
                BungeePerms.getUserManager().addGroup(target, group);
                player.sendMessage(new TextComponent(ChatColor.GREEN + "Added Member " + target.getName() + " to group " + groupName));
            }
            case "remove" -> {
                BungeePerms.getUserManager().removeGroup(target, group);
                player.sendMessage(new TextComponent(ChatColor.GREEN + "Removed Member " + target.getName() + " to group " + groupName));
            }
            default -> sendMessage(player, ChatColor.RED + "Invalid type! Use 'add' or 'remove'.");
        }
    }

    private void handleGroupSetPrefix(ProxiedPlayer player, String groupName, String prefix) {
        handleGroupSetAttribute(player, groupName, prefix, "prefix");
    }

    private void handleGroupSetSuffix(ProxiedPlayer player, String groupName, String suffix) {
        handleGroupSetAttribute(player, groupName, suffix, "suffix");
    }

    private void handleGroupSetAttribute(ProxiedPlayer player, String groupName, String value, String attribute) {
        if (!player.hasPermission("bungeeperms.admin.group.set")) {
            sendMessage(player, ChatColor.RED + "You do not have permission to use this command!");
            return;
        }

        Group group = BungeePerms.getGroupManager().getGroup(groupName);
        if (group == null) {
            sendMessage(player, ChatColor.RED + "That group does not exist!");
            return;
        }

        if (attribute.equals("prefix")) {
            BungeePerms.getGroupManager().setPrefix(groupName, value);
        } else if (attribute.equals("suffix")) {
            BungeePerms.getGroupManager().setSuffix(groupName, value);
        }

        sendMessage(player, ChatColor.GREEN + "Group " + ChatColor.GOLD + groupName + ChatColor.GREEN
                + " has been set the " + attribute + " " + ChatColor.GOLD + value + ChatColor.GREEN + "!");
    }

    private void sendHelpMessage(ProxiedPlayer player) {
        sendMessage(player, ChatColor.DARK_GRAY + "------------------------------------------------");
        sendMessage(player, ChatColor.GOLD + "Group Commands:");
        sendMessage(player, ChatColor.GREEN + "/bungeeperms group create <name>");
        sendMessage(player, ChatColor.GREEN + "/bungeeperms group delete <name>");
        sendMessage(player, ChatColor.GREEN + "/bungeeperms group set <prefix|suffix> <group> <value>");
        sendMessage(player, ChatColor.GREEN + "/bungeeperms group permission <add/remove> <group> <value>");
        sendMessage(player, ChatColor.GREEN + "/bungeeperms group member <add/remove> <group> <name>");
        sendMessage(player, ChatColor.DARK_GRAY + "------------------------------------------------");
    }

    private void sendUsageMessage(ProxiedPlayer player) {
        sendMessage(player, ChatColor.RED + "Wrong Usage!");
        sendMessage(player, ChatColor.GOLD + "/bungeeperms help");
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(message));
    }
}
