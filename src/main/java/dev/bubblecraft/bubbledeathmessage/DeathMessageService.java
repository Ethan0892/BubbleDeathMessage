package dev.bubblecraft.bubbledeathmessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.Method;

public final class DeathMessageService {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final BubbleDeathMessagePlugin plugin;
    private final boolean suppressVanilla;
    private final boolean useInteractiveChatIfPresent;

    public DeathMessageService(BubbleDeathMessagePlugin plugin) {
        this.plugin = plugin;
        this.suppressVanilla = plugin.getConfig().getBoolean("settings.suppress-vanilla", true);
        this.useInteractiveChatIfPresent = plugin.getConfig().getBoolean("settings.use-interactivechat-if-present", true);
    }

    public void handle(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        EntityDamageEvent lastDamage = victim.getLastDamageCause();
        String causeKey = lastDamage == null || lastDamage.getCause() == null ? "default" : lastDamage.getCause().name();

        String template = plugin.getConfig().getString("messages." + causeKey);
        if (template == null) {
            template = plugin.getConfig().getString("messages.default", "<gray><player> died.</gray>");
        }

        template = applyPlaceholderApiIfPresent(victim, template);

        Player killer = victim.getKiller();
        Location loc = victim.getLocation();

        TagResolver placeholders = TagResolver.resolver(
                Placeholder.component("player", Component.text(victim.getName())),
                Placeholder.component("player_displayname", Component.text(victim.getDisplayName())),
                Placeholder.component("killer", Component.text(killer == null ? "" : killer.getName())),
                Placeholder.component("killer_displayname", Component.text(killer == null ? "" : killer.getDisplayName())),
                Placeholder.component("cause", Component.text(causeKey)),
                Placeholder.component("world", Component.text(loc.getWorld() == null ? "" : loc.getWorld().getName())),
                Placeholder.component("x", Component.text(String.valueOf(loc.getBlockX()))),
                Placeholder.component("y", Component.text(String.valueOf(loc.getBlockY()))),
                Placeholder.component("z", Component.text(String.valueOf(loc.getBlockZ())))
        );

        Component component;
        try {
            component = MINI_MESSAGE.deserialize(template, placeholders);
        } catch (Throwable t) {
            // fallback: if config is invalid minimessage
            component = Component.text(stripMiniMessageTags(template));
        }

        if (suppressVanilla) {
            suppressVanillaDeathMessage(event);
            broadcast(component);
        } else {
            // Prefer Paper component deathMessage if available; else legacy string.
            if (!trySetPaperComponentDeathMessage(event, component)) {
                event.setDeathMessage(LEGACY.serialize(component));
            }
        }
    }

    private String applyPlaceholderApiIfPresent(Player player, String template) {
        if (!plugin.isPlaceholderApiPresent()) {
            return template;
        }
        try {
            Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method setPlaceholders = papi.getMethod("setPlaceholders", Player.class, String.class);
            Object result = setPlaceholders.invoke(null, player, template);
            return result instanceof String ? (String) result : template;
        } catch (Throwable ignored) {
            return template;
        }
    }

    private void broadcast(Component component) {
        if (useInteractiveChatIfPresent && plugin.isInteractiveChatPresent()) {
            if (tryBroadcastViaInteractiveChat(component)) {
                return;
            }
        }

        // Paper / modern Spigot (adventure) path: Player#sendMessage(Component)
        if (tryBroadcastComponent(component)) {
            return;
        }

        // Legacy fallback
        String legacy = LEGACY.serialize(component);
        Bukkit.broadcastMessage(legacy);
    }

    private boolean tryBroadcastViaInteractiveChat(Component component) {
        try {
            Class<?> api = Class.forName("com.loohp.interactivechat.api.InteractiveChatAPI");
            Method sendMessage = api.getMethod("sendMessage", org.bukkit.command.CommandSender.class, Component.class);

            Bukkit.getOnlinePlayers().forEach(p -> {
                try {
                    sendMessage.invoke(null, p, component);
                } catch (Throwable ignored) {
                    // ignore per-player failures
                }
            });
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean tryBroadcastComponent(Component component) {
        try {
            Method sendMessage = Player.class.getMethod("sendMessage", Component.class);
            Bukkit.getOnlinePlayers().forEach(p -> {
                try {
                    sendMessage.invoke(p, component);
                } catch (Throwable ignored) {
                }
            });
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void suppressVanillaDeathMessage(PlayerDeathEvent event) {
        // Try Paper component death message first
        trySetPaperComponentDeathMessage(event, null);

        // Then Spigot string death message
        try {
            event.setDeathMessage(null);
        } catch (Throwable ignored) {
        }
    }

    private boolean trySetPaperComponentDeathMessage(PlayerDeathEvent event, Component component) {
        try {
            Method setDeathMessage = event.getClass().getMethod("deathMessage", Component.class);
            setDeathMessage.invoke(event, component);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String stripMiniMessageTags(String input) {
        // Very small “fallback” sanitizer. Not a full parser.
        return input.replaceAll("<[^>]+>", "");
    }
}
