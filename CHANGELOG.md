# Changelog

All notable changes to AverageEssentials will be documented in this file.

## [0.2.10] - 2026-02-17
- **Fixed DB saving**
    - Resolved an issue where data was not being saved to the database properly.

## [0.2.9] - 2026-02-17
- **Hytale Update 3 Support**
    - Updated plugin compatibility with the latest Hytale Server API changes, resolving breaking changes
- **Respecting Hytale's semantic versioning**
    - Updated plugin version to 0.2.9, as 0.2.8.1 apparently broke the plugin.

## [0.2.8.1] - 2026-02-10

### Refactor & Cleanup
- **Command Logic Improvements**
  - Refactored `HomeCommand` with better null checks, improved home listing, and simplified permission/limit logic.
  - Streamlined `NicknameCommand` by splitting handling for own vs. other players and adding a helper for nickname updates.
  - Consolidated `RegionCommand` flag updates, refined region claim flow, and improved stats message assembly.
- **Core Logic Refinement**
  - Major rework of `ChatListener` for better organization and streamlined filtering flow (bannable, removable, and censorable terms).
  - Improved `RegionService` with new helpers for interaction logic and colored message parsing.
  - Enhanced null safety and encapsulation across several key components.

### Technical
- **Java Toolchain Update**
  - Updated Java toolchain to version 25 in `build.gradle.kts`.
- **Testing Infrastructure**
  - Added unit tests for `ChatFilterConfiguration`, `RegionZone`, and `ChatListener`.
  - Integrated Mockito for improved testability and enabled Hytale Server context for tests.
- **Documentation**
  - Added `GEMINI.md` with a comprehensive project overview and development instructions.

### Localization
- Fixed and added several translation keys in `server.lang` for more consistent messaging.
- Updated ECS handlers to use the correct translation keys.

## [0.2.8] - 2026-02-10

### Added
- **Reload Command**
  - New `/avreload` command (aliases: `/avr`, `/reloadav`) to reload all configurations without restarting
  - Allows administrators to update settings on-the-fly

### Improvements
- **Configuration Providers**
  - Refactored config providers to use consistent getter methods
  - Improved code consistency across all configuration handlers
  - Enhanced provider lifecycle management during plugin shutdown

- **Plugin Lifecycle**
  - Improved shutdown procedure with proper provider cleanup
  - Added informational message provider shutdown handling
  - Better database connection cleanup during server shutdown

- **Build System**
  - Configured shadowJar to properly merge service files
  - Changed duplicate handling strategy from EXCLUDE to INCLUDE for better dependency bundling
  - Enabled ZIP64 support for handling large JAR files

### Technical
- Updated AverageHytaleCore library with improved utilities
- Added SLF4J Simple logging implementation
- Code cleanup and minor refactoring for consistency

## [0.2.7] - 2026-01-27

### Added
- **Link Embedding Option**
  - New feature to embed links in chat or server messages
  - Configurable link preview settings

### Updated
- **Hytale Server Dependency**
  - Updated to latest server API version
  - Improved compatibility with recent Hytale server releases

### Improvements
- **Home Teleportation**
  - Fixed teleportation to home using correct world context
  - Enhanced world context detection for home teleports

- **Chat Filtering**
  - Refactored chat filtering logic
  - Fixed group weight check in chat prefix assignment

- **Region System Fix**
  - /region view is more null-safe when players first join the server

### Technical
- Updated JAR dependencies, size of JAR reduced significantly
- Code refactoring and performance optimizations

## [0.2.5] - 2026-01-21

### Added
- **Region Teleportation Improvements**
  - Safe teleport algorithm for `/region tp` command
  - Added checks to find a valid teleportation location
  - Prevent teleporting to unsafe or obstructed areas

- **Broadcast Command**
  - Enhanced broadcast functionality with improved message handling
  - Added permission checks for broadcasting

### Improvements
- **Region System**
  - Enhanced block interaction event handling
  - Added preliminary permission bypass checks
  - Improved region boundary detection and validation

### Technical
- Code refactoring and cleanup
- Improved error handling for region and teleportation commands
- Enhanced logging for region interactions