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

---

