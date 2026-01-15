# AverageEssentials

**AverageEssentials** is a lightweight Java plugin for Hytale servers, built with Gradle, designed to enhance core server functionality with essential tools for player management, communication, and administration.

## Features

- **Chat Formatting**: Automatically formats player chat messages with group-based prefixes. Displays the highest-weighted permission group prefix alongside the player's username and message for a clean, organized chat experience.
- **Plugin Management**: Provides comprehensive commands to list, load, unload, reload, and manage plugins directly from the server console or in-game.
- **Group Management**: Allows administrators to set and manage prefixes for permission groups, stored in configurable JSON files.
- **Informational Messages**: Supports welcome messages for new players, periodic broadcasts to all online players, and dynamic commands for quick information dissemination (e.g., Discord links).
- **Color Code Support**: Parses Minecraft-style color codes (&0-&f for colors, &l for bold) in messages and prefixes for rich text formatting.
- **Configurable Broadcasting**: Scheduled broadcasts with customizable frequency and messages, cycling through a queue for variety.

## Requirements

- Hytale Server (compatible with core APIs for events, permissions, commands, and messaging)
- Java 8 or higher
- Gradle for building (included in the project)

## Installation

1. Download the latest release JAR from the releases section.
2. Place the JAR file in your server's plugins directory.
3. Restart the server to load the plugin.
4. Configure groups, messages, and broadcasts via the generated JSON config files in `mods/AverageEssentials/`.

## Building from Source

Clone the repository and run `./gradlew build` to compile the plugin.

For more details, visit the project repository or report issues on GitHub.
