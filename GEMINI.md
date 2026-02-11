# GEMINI.md

This file provides guidance to Gemini CLI for working in this repo.

## Project Overview

**AverageEssentials** is a lightweight Hytale server plugin built with Gradle that provides essential administrative and player management features.

It's a modular plugin that handles chat management, player utilities (homes, nicknames, regions), server administration and more.

## Build & Development Commands

### Build the plugin
```bash
./gradlew build
```

### Build shadowJar (creates unified JAR with all dependencies)
```bash
./gradlew shadowJar
```

### Run tests
```bash
./gradlew test
```

### Run a specific test
```bash
./gradlew test --tests TestClassName
```

### Clean build artifacts
```bash
./gradlew clean
```

### Check dependencies and plugin metadata
Configured in `gradle.properties` and `build.gradle.kts`. Plugin main class: `github.renderbr.hytale.AverageEssentials`

## Architecture

The plugin follows a **registry-based architecture** with clear separation of concerns:

### Core Components

**1. Plugin Entry Point** (`AverageEssentials.java`)
- Extends `JavaPlugin` from the Hytale Server API
- Initializes the database connection and registries during `setup()`
- Manages lifecycle hooks (shutdown, reload)
- Manages `RegionBoundaryService` for region visualization

**2. Registry Pattern** (key design)
- **CommandRegistry** (`registries/CommandRegistry.java`)
  - Stores all commands in static lists (`REGISTERED_COMMANDS_COLLECTIONS` and `REGISTERED_COMMANDS`)
  - Commands extend either `AbstractCommandCollection` (grouped commands like home, region) or `CommandBase` (standalone like nickname, broadcast)
  - Registered during plugin setup via `registerCommands()`

- **ProviderRegistry** (`registries/ProviderRegistry.java`)
  - Manages all configuration providers as static instances
  - Provides a single `reloadAll()` method for syncing all configs (used by ReloadCommand)
  - Key providers: `groupManagerProvider`, `chatFilterConfigurationProvider`, `homeProvider`, `nicknameProvider`, `regionProvider`, `informationalMessageProvider`

- **ListenerRegistry** (`registries/ListenerRegistry.java`)
  - Registers event listeners and ECS-based listeners for Hytale events

**3. Configuration Providers** (`config/` package)
- Each provider handles loading/syncing a specific JSON config file
- Providers are initialized as singletons in `ProviderRegistry`
- Config objects stored in `config/obj/` (e.g., `ChatFilterConfiguration`, `GroupConfigObject`)
- Configs are stored in the `mods/AverageEssentials/` directory on the server

**4. Commands** (`commands/` package)
- **AbstractCommandCollection**: `AlteredPluginCommand`, `GroupManagerCommand`, `HomeCommand`, `RegionCommand` - contain subcommands
- **CommandBase**: `NicknameCommand`, `BroadcastCommand`, `ReloadCommand` - standalone commands
- ReloadCommand reloads all providers via `ProviderRegistry.reloadAll()`

**5. Database Models** (`db/models/` package)
- `PlayerHome`: Stores player home locations
- Region models: `PlayerRegionChunk`, `PlayerRegionGroup`, `PlayerRegionGroupShare`, `PlayerRegionCommandData`
- Database initialized in plugin setup and accessed via `AverageEssentials.databaseService`

**6. Event Listeners** (`listeners/` package)
- ECS-based listeners in `listeners/ecs/player/` for handling player events
- Handles chat filtering, nickname display, region interactions

**7. Services** (`service/` package)
- `RegionBoundaryService`: Manages region visualization boundaries
- Region service classes for database operations

## Key Implementation Patterns

### Adding a New Command
1. Create command class in `commands/` package extending `CommandBase` or `AbstractCommandCollection`
2. Add it to either `REGISTERED_COMMANDS` or `REGISTERED_COMMANDS_COLLECTIONS` in `CommandRegistry`
3. If reloadable config needed, create a provider in `ProviderRegistry`

### Adding Configuration
1. Create config object in `config/obj/`
2. Create a provider in `config/` that extends configuration provider pattern
3. Register in `ProviderRegistry` and add to `reloadAll()` method
4. Configs are automatically synced from JSON files in `mods/AverageEssentials/`

### Database Operations
- Use `AverageEssentials.databaseService` to access database
- Models use ORM pattern with `PlayerHome` and region models as examples

### Internationalization
Found in: `resources/Server/Languages/en-US/server.lang`

- In code, localization keys will be called such as: `server.averageessentials.commands.reload.desc`
- There is one primary lang file. Within `server.lang`, the `server.` prefix is stripped as when used in code, this refers to the file itself
- There must never be missing localization strings

## Important Notes

- The plugin uses a **custom AverageHytaleCore library** (`libs/AverageHytaleCore.jar`) that provides utility classes like `DbUtils`, `PathUtils`, and base configuration providers
- Commands are registered during plugin setup, not dynamically at runtime
- Configuration providers use lazy loading with file-based persistence
- The Hytale Server API is a Maven dependency from `https://maven.hytale.com/release`
- Region visualization and boundary management runs in a separate service thread
- Chat filtering, group management, and player utilities all depend on config providers being loaded

## Testing

Tests use JUnit 5 (Jupiter). Run with `./gradlew test`. Test infrastructure is set up but check `src/test/` for existing test patterns.

## Build Artifacts

- **Regular build**: `build/libs/AverageEssentials-<version>.jar`
- **Shadow JAR**: `build/libs/AverageEssentials-<version>-all.jar` (preferred for deployment, contains all dependencies)
- The shadow JAR is configured with `mergeServiceFiles()`, `duplicatesStrategy = INCLUDE`, and `isZip64 = true`
