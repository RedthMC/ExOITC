// 
// Decompiled by Procyon v0.6.0
// 

package me.redth.exoitc.command;

public abstract class SubCommand implements CommandBase {
    private final boolean playerOnly;
    private final String description;
    private final String name;
    private final String permissionNode;
    private final String usage;
    private final String[] aliases;

    public SubCommand(String name, String description, String usage, String permissionNode, boolean playerOnly, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.permissionNode = permissionNode;
        this.playerOnly = playerOnly;
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getPermissionNode() {
        return this.permissionNode;
    }

    public String getUsage() {
        return this.usage;
    }

    public boolean isPlayerOnly() {
        return this.playerOnly;
    }
}
