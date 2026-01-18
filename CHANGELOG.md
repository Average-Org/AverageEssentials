# Changelog

All notable changes to AverageEssentials will be documented in this file.

## [0.1.0] - 2026-01-15

### Added
- **Chat Filter System** - Three-tier content moderation system:
  - Bannable Terms: Automatically ban players for using prohibited words (instant permanent ban)
  - Censurable Terms: Replace offensive words with `****` while allowing message delivery
  - Removable Terms: Silently block restricted messages and notify the sender
  - Case-insensitive regex pattern matching with word boundary detection for accurate filtering
- **Chat Formatting** - Group-based prefix system with highest-weighted group priority in chat display
- **Color Code Support** - Minecraft-style color codes (&0-&f, &l) for rich text formatting with toggle option
- **Group Management** - Permission group prefix configuration with weight-based priority
- **Plugin Management Commands** - Ability to list, load, unload, and reload plugins from console/in-game
- **Informational Messages** - Welcome messages for new players and periodic server broadcasts
- **Configurable Broadcasting** - Scheduled broadcasts with cycling message queues

### Features
- JSON-based configuration system for easy customization
- Permission group integration with Hytale server permissions
- Player event listeners for chat and join events
- Automatic configuration file generation on first run
- Access control integration for ban management

### Technical
- Built with Java using Gradle for dependency management
- Compatible with Hytale Server APIs (events, permissions, commands, messaging)
- Requires Java 8 or higher
- Shadow JAR packaging for easy deployment

## [0.2.0] - 2026-01-18

### Added
- **Home System** - Complete home management functionality:
  - `/home set <name>` - Set a home location with custom name
  - `/home tp <name>` - Teleport to a saved home
  - `/home list` - Display all saved homes for the player
  - `/home delete <name>` - Remove a specific home
  - Permission-based home limits with `averageessentials.homes.limit.<number>` permissions
  - Configurable default maximum homes (default: 3)
  - Unlimited homes for operators
  - SQLite database storage for persistent home data across server restarts
- **Nickname Management** - Player nickname customization:
  - `/nickname <nick>` - Set personal nickname (alias: `/nick`)
  - `/nickname <player> <nick>` - Set nickname for another player (admin only)
  - `/nickname <player> clear` - Clear a player's nickname
  - Automatic nickname application on player join
  - JSON-based storage for nickname persistence

### Features
- Database integration with SQLite for home storage
- Permission system integration for home limits and nickname management
- Automatic configuration file generation for new features
- Enhanced player utilities for improved gameplay experience
