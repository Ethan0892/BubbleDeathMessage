# BubbleDeathMessage

✨ Lightweight, configurable death messages for Paper/Spigot servers.

## Features

- 🎯 Replace vanilla death messages with your own templates
- 🎨 MiniMessage formatting support
- 🧩 Optional PlaceholderAPI support (PAPI placeholders)
- 💬 Optional InteractiveChat support
- 🔄 `/bubbledeathmessage reload` (alias: `/bdm`)

## Dependencies

- ✅ None required.

Optional (soft-depend):

- 🧩 PlaceholderAPI — enables `%placeholders%` inside your message templates
- 💬 InteractiveChat — broadcasts through InteractiveChat when present

## Commands

- 🔧 `/bubbledeathmessage reload` (alias: `/bdm`)

## Permissions

- 🔐 `bubbledeathmessage.reload` (default: op)

## Configuration

Messages are **MiniMessage** strings.

Built-in placeholders:

- `<player>` `<player_displayname>`
- `<killer>` `<killer_displayname>` (may be empty)
- `<cause>`
- `<world>`
- `<x>` `<y>` `<z>`

If PlaceholderAPI is installed, PAPI placeholders like `%vault_prefix%` are supported.

Example (`config.yml`):

```yml
messages:
  default: "<gray><player> died.</gray>"
  ENTITY_ATTACK: "<red><player> was slain by <killer></red>"
  FALL: "<gray><player> fell from a high place.</gray>"

settings:
  suppress-vanilla: true
  use-interactivechat-if-present: true
```

## Compatibility

- Java 17
- Paper 1.21.x

## Source / Issues

https://github.com/Ethan0892/BubbleDeathMessage
