# Elysium Auth

*[中文](README.md)*

### Introduction
Elysium Auth is a flexible authentication plugin supporting multiple server types (Paper/Velocity). It provides four authentication modes (local, official, proxy, and custom) to meet player identity verification needs in various scenarios.

### Features
- Multi-server support: Compatible with both Paper servers and Velocity proxies without requiring separate adaptation code.
- Flexible authentication modes: Four authentication methods available:
  - proxy: Relies on upstream Velocity proxy authentication, ideal for multi-server interconnected environments.
  - local: Local database authentication (supports SQLite/MySQL), suitable for single servers or small clusters.
  - official: Integrates with official authentication interfaces (following ElysiumAuthAPI protocol), perfect for unified account system scenarios.
  - custom: Supports custom authentication (JAR extensions or JS scripts) for special authentication logic requirements.
- Multi-language support: Automatically loads language files based on player region, with default Chinese (zh-CN.yml) and English (en-US.yml) included.
- Player restriction configuration: Customizable username and password format rules (such as length, character types) to enhance account security.

### Requirements
- Paper 1.17+
- Velocity 3.3.0-SNAPSHOT+

### Configuration Files
- [setting.yml](help/setting.yml)
- [zh-CN.yml](help/zh-CN.yml)

### ElysiumAuthAPI Development & Help Manual
- [In Development]()

Elysium Studio\
Skilfully CN
