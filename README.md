[![Get quality hosting!](https://i.imgur.com/4rIaoNo.jpeg)](https://billing.kinetichosting.com/aff.php?aff=1261)

> **[Kinetic Hosting](https://billing.kinetichosting.com/aff.php?aff=1261):** Check out my game hosting partner! They offer fast, affordable hosting with excellent customer support!

# AverageEssentials

**AverageEssentials** is a lightweight Java plugin for Hytale servers, built with Gradle, designed to enhance core server functionality with essential tools for player management, communication, and administration.

## Features

### Chat Management
- **Chat Formatting**: Automatically formats player chat messages with group-based prefixes. Displays the highest-weighted permission group prefix alongside the player's username and message for a clean, organized chat experience.
- **Chat Filtering System**: Advanced content moderation with three-tier filtering:
  - **Bannable Terms**: Automatically ban players who use specified prohibited terms. Players using bannable words are instantly disconnected and permanently banned from the server.
  - **Censurable Terms**: Automatically censor (replace with ****) specified words while allowing the message to be sent, providing a less disruptive moderation approach.
  - **Removable Terms**: Silently block messages containing restricted words and notify the player that the message was not allowed. Messages are not sent to other players.
  - **Regex Pattern Matching**: All filters use case-insensitive regex pattern matching with word boundary detection for accurate and flexible filtering.
- **Color Code Support**: Parses Minecraft-style color codes (&0-&f for colors, &l for bold) in messages and prefixes for rich text formatting. Optionally disable color codes in chat per configuration.
- **Configurable Broadcasting**: Scheduled broadcasts with customizable frequency and messages, cycling through a queue for variety.

### Administration
- **Plugin Management**: Provides comprehensive commands to list, load, unload, reload, and manage plugins directly from the server console or in-game.
- **Group Management**: Allows administrators to set and manage prefixes for permission groups, stored in configurable JSON files.
- **Informational Messages**: Supports welcome messages for new players, periodic broadcasts to all online players, and dynamic commands for quick information dissemination (e.g., Discord links).

### Player Utilities
- **Home System**: Allows players to set, teleport to, list, and delete personal home locations with configurable limits based on permissions. Supports unlimited homes for operators.
- **Nickname Management**: Enables players and administrators to set custom nicknames, with support for clearing nicknames back to default usernames.
- **Region Management**: Advanced region claiming and management system:
  - Claim and manage land regions with precise block-level controls
  - Safe teleportation within claimed regions
  - Configurable interaction permissions (block break, place, interact)
  - Region boundary visualization and management
  - Supports region sharing and collaborative land management

## Requirements

- Hytale Server (latest API version)
- Java 8 or higher
- Gradle for building (included in the project)

## Installation

1. Download the latest release JAR (version 0.2.7) from the releases section.
2. Place the JAR file in your server's plugins directory.
3. Restart the server to load the plugin.
4. Configure groups, messages, broadcasts, home limits, link embedding, and other settings via the generated JSON config files in `mods/AverageEssentials/`.

### New in v0.2.7
- Link embedding option with configurable preview settings
- Updated Hytale Server dependency compatibility
- Improved home and chat system functionality

## Configuration

### Chat Filter Configuration (`chat.json`)

The chat filter is highly configurable and allows administrators to define three levels of content moderation:

```json
{
  "config": {
    "bannableTerms": ["badword1", "badword2"],
    "termsToCensor": ["mildword1", "mildword2"],
    "termsToDisable": ["restrictedword1", "restrictedword2"],
    "allowUsersToUseChatColorCodes": true
  }
}
```

**Configuration Options:**

- **`bannableTerms`** (String[]): An array of terms that trigger an automatic permanent ban. Players using these words are instantly disconnected and banned. Use this for the most severe violations.
  
- **`termsToCensor`** (String[]): An array of terms that are automatically replaced with `****` in chat messages. Messages are still delivered to players. Use this for mild language or spam prevention.
  
- **`termsToDisable`** (String[]): An array of restricted terms whose messages are silently blocked. The sender receives a notification but other players don't see the message. Use this for maintaining topic relevance or preventing specific discussions.
  
- **`allowUsersToUseChatColorCodes`** (Boolean): When `true`, players can use Minecraft-style color codes (&0-&f, &l, etc.) in their messages. When `false`, color codes are treated as plain text. Default: `true`

**Filtering Behavior:**

- All filters are **case-insensitive** to catch variations in capitalization
- Uses **word boundary detection** (word break regex) to match whole words only, preventing false positives (e.g., "test" won't match in "testing")
- Filters are processed in order: Bannable → Removable → Censurable
- If a message triggers a bannable term, it's immediately blocked and the player is banned
- If it triggers a removable term, the message is blocked and the player is notified
- Censurable terms are applied after other checks pass

### Group Manager Configuration (`group.json`)

Configure permission group prefixes for chat formatting:

```json
{
  "groups": {
    "admin": {
      "prefix": "&c[Admin]&r",
      "weight": 100
    },
    "moderator": {
      "prefix": "&6[Mod]&r",
      "weight": 50
    },
    "member": {
      "prefix": "&7[Member]&r",
      "weight": 1
    }
  }
}
```

- **`weight`**: Determines priority when a player has multiple groups. Higher weight = higher priority in chat display.
- **`prefix`**: The text displayed before the player's name in chat, supporting color codes.

### Home Configuration (`homes.json`)

Configure default home limits for players:

```json
{
  "defaultMaxHomes": 3
}
```

**Configuration Options:**

- **`defaultMaxHomes`** (Integer): The default maximum number of homes a player can set. This can be overridden by permissions (e.g., `averageessentials.homes.limit.5` for 5 homes). Operators have unlimited homes by default. Default: `3`

### Nickname Configuration (`nicknames.json`)

Stores player nicknames (automatically managed, no manual editing required):

```json
{
  "nicknames": {
    "player-uuid-1": "CoolNick",
    "player-uuid-2": "AnotherNick"
  }
}
```

**Notes:**

- Nicknames are stored as a map of player UUIDs to their custom nicknames.
- This file is automatically updated when nicknames are set or cleared.
- Manual editing is not recommended as it may cause inconsistencies.
