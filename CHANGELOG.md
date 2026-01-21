# Changelog

All notable changes to AverageEssentials will be documented in this file.

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