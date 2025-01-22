package com.panda0day.bungeePerms.groups;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final int id;
    private final String name;
    private String prefix;
    private String suffix;
    private List<String> members;
    private List<String> permissions;

    public Group(int id, String name, String prefix, String suffix, List<String> members, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.members = members;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public List<String> getMembers() {
        return members;
    }
    public void addMember(String member) {
        members.add(member);
    }
    public void removeMember(String member) {
        members.remove(member);
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> _permissions) {
        this.permissions = new ArrayList<>(this.permissions);

        this.permissions.clear();
        this.permissions.addAll(_permissions);
    }
}
