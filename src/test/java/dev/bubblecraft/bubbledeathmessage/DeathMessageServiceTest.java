package dev.bubblecraft.bubbledeathmessage;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeathMessageServiceTest {

    private ServerMock server;
    private BubbleDeathMessagePlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BubbleDeathMessagePlugin.class);

        plugin.getConfig().set("settings.suppress-vanilla", false);
        plugin.getConfig().set("messages.default", "<gray><player> died.</gray>");
        plugin.getConfig().set("messages.FALL", "<gray><player> fell.</gray>");
        plugin.saveConfig();
        plugin.reloadInternal();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void usesDefaultWhenNoCauseOverride() {
        PlayerMock steve = server.addPlayer("Steve");

        DamageSource source = DamageSource.builder(DamageType.GENERIC).build();
        PlayerDeathEvent event = new PlayerDeathEvent(steve, source, new ArrayList<>(), 0, (Component) null, true);
        plugin.deathMessageService().handle(event);

        // When suppress-vanilla=false we set the event death message
        // (legacy serialized, but no colors are asserted)
        assertNotNull(event.getDeathMessage());
        assertEquals("Steve died.", event.getDeathMessage().replace("§7", ""));
    }

    @Test
    public void usesCauseOverrideWhenPresent() {
        PlayerMock steve = server.addPlayer("Steve");
        steve.setLastDamageCause(new EntityDamageEvent(steve, EntityDamageEvent.DamageCause.FALL, 10.0));

        DamageSource source = DamageSource.builder(DamageType.FALL).build();
        PlayerDeathEvent event = new PlayerDeathEvent(steve, source, new ArrayList<>(), 0, (Component) null, true);
        plugin.deathMessageService().handle(event);

        assertNotNull(event.getDeathMessage());
        assertEquals("Steve fell.", event.getDeathMessage().replace("§7", ""));
    }

    @Test
    public void suppressVanillaClearsEventMessageWhenEnabled() {
        plugin.getConfig().set("settings.suppress-vanilla", true);
        plugin.saveConfig();
        plugin.reloadInternal();

        PlayerMock steve = server.addPlayer("Steve");
        PlayerMock alex = server.addPlayer("Alex");

        DamageSource source = DamageSource.builder(DamageType.GENERIC).build();
        PlayerDeathEvent event = new PlayerDeathEvent(steve, source, new ArrayList<>(), 0, (Component) null, true);
        plugin.deathMessageService().handle(event);

        assertNull(event.getDeathMessage());
        String alexMsg = alex.nextMessage();
        assertTrue(alexMsg.replace("§7", "").contains("Steve"));
    }
}
