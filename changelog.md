## IronFist v1.5 更新日志 / Changelog

### 更改 / Changes

- **挖掘速度改为基础速度加成** — `onBreakSpeed` 不再直接覆盖最终挖掘速度，改为乘以 `event.getOriginalSpeed()`，保留水下/空中/药水等环境减速效果。
- **修饰符改在进入世界时应用** — 方块范围、实体范围、攻击伤害修饰符的生效时机从方块破坏/攻击实体事件移至 `IronFistPlayer.load()`，进世界即生效；等级变化和配置开关时自动刷新，关闭配置时自动清除。
- **Config 命令支持查看当前值** — `/ironfist config <setting>` 在不提供值时不再报错，改为输出该配置的当前值。


- **Break speed now scales from base speed** — `onBreakSpeed` no longer overrides the final speed; instead it multiplies `event.getOriginalSpeed()`, preserving environmental slowdowns from water, air, potions, etc.
- **Modifiers applied on world join** — Block reach, entity reach, and attack damage modifiers are now applied in `IronFistPlayer.load()` instead of waiting for a block break or attack. They refresh on level-up, `/setLV`, and config toggles; clearing a config removes the modifier.
- **Config command shows current value** — `/ironfist config <setting>` without a value no longer errors; it displays the current value instead.
