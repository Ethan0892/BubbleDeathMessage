# BubbleDeathMessage

Lightweight, configurable death messages for Paper/Spigot servers.

- Replace vanilla death messages with your own templates
- MiniMessage formatting support
- Optional PlaceholderAPI support (PAPI placeholders)
- Optional InteractiveChat support (broadcast through InteractiveChat when present)
- `/bubbledeathmessage reload` command

## Compatibility

- Built for **Paper** (1.21.x)
- Java **17**
- Should also work on Spigot-derived servers, but Paper is recommended.

## Installation

1. Download the jar from your release page (or build it yourself).
2. Put the jar in your server `plugins/` folder.
3. (Optional) Install dependencies:
   - PlaceholderAPI (for `%placeholders%`)
   - InteractiveChat (for InteractiveChat processing)
4. Start the server to generate the config.
5. Edit `plugins/BubbleDeathMessage/config.yml` and run `/bdm reload`.

## Commands

- `/bubbledeathmessage reload` (alias: `/bdm`)

## Permissions

- `bubbledeathmessage.reload` (default: op)

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
  FALL: "<gray><player> fell from a high place.</gray>"
  LAVA: "<red><player> tried to swim in lava.</red>"

settings:
  suppress-vanilla: true
  use-interactivechat-if-present: true
```

Notes:

- Per-cause overrides use Bukkit/Paper `DamageCause` keys (e.g. `FALL`, `LAVA`, `DROWNING`).
- If `suppress-vanilla` is `true`, BubbleDeathMessage will clear the vanilla death message and broadcast your template.

## Building

```bash
mvn clean package
```

The output jar will be in `target/`.

## Support / Issues

- GitHub: https://github.com/Ethan0892/BubbleDeathMessage
