# Elysium Auth

*[English](README_English.md)*

### 简介
Elysium Auth 是一款支持多服务端（Paper/Velocity）的灵活认证插件，提供本地、官方、代理及自定义四种认证模式，适配不同场景下的玩家身份验证需求。

### 功能
- 多服务端支持：适配 Paper 服务器和 Velocity 代理端，无需单独开发适配代码.
- 灵活认证模式：提供 4 种认证方式：
  - proxy：依赖上游 Velocity 代理端认证，适合多服互联场景。
  - local：本地数据库认证（支持 SQLite/MySQL），适合单服或小型集群。
  - official：对接官方认证接口（遵循 ElysiumAuthAPI 协议），适合需要统一账号体系的场景。
  - custom：支持自定义认证（JAR 扩展或 JS 脚本），适合有特殊认证逻辑的需求。
- 多语言适配：自动根据玩家地区加载语言文件，默认提供中文（zh-CN.yml）和英文（en-US.yml）。
- 玩家限制配置：可自定义用户名、密码格式规则（如长度、字符类型），保障账号安全性。

### 要求
- Paper 1.17+
- Velocity 3.3.0-SNAPSHOT+

### 配置文件
- [setting.yml](help/setting.yml)
- [zh-CN.yml](help/zh-CN.yml)

### ElysiumAuthAPI 开发&帮助手册
- [开发中]()

Elysium Studio\
Skilfully CN
