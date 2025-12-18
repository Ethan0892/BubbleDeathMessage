package dev.bubblecraft.bubbledeathmessage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class DeathListener implements Listener {

    private final BubbleDeathMessagePlugin plugin;

    public DeathListener(BubbleDeathMessagePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        plugin.deathMessageService().handle(event);
    }
}
