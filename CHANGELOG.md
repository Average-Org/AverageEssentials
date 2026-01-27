# Changelog

All notable changes to AverageEssentials will be documented in this file.

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