package dev.bubblecraft.bubbledeathmessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ReloadCommand implements CommandExecutor {

    private final BubbleDeathMessagePlugin plugin;

    public ReloadCommand(BubbleDeathMessagePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            return false;
        }
        if (!sender.hasPermission("bubbledeathmessage.reload")) {
            sender.sendMessage("You do not have permission.");
            return true;
        }

        plugin.reloadInternal();
        sender.sendMessage("BubbleDeathMessage reloaded.");
        return true;
    }
}
