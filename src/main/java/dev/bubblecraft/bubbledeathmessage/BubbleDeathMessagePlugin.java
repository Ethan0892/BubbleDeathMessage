package dev.bubblecraft.bubbledeathmessage;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BubbleDeathMessagePlugin extends JavaPlugin {

    private DeathMessageService deathMessageService;
    private boolean placeholderApiPresent;
    private boolean interactiveChatPresent;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        placeholderApiPresent = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        interactiveChatPresent = Bukkit.getPluginManager().getPlugin("InteractiveChat") != null;

        reloadInternal();

        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);

        PluginCommand command = getCommand("bubbledeathmessage");
        if (command != null) {
            command.setExecutor(new ReloadCommand(this));
        }
    }

    public void reloadInternal() {
        reloadConfig();
        deathMessageService = new DeathMessageService(this);
    }

    public DeathMessageService deathMessageService() {
        return deathMessageService;
    }

    public boolean isPlaceholderApiPresent() {
        return placeholderApiPresent;
    }

    public boolean isInteractiveChatPresent() {
        return interactiveChatPresent;
    }
}
