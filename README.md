# Creepy SalHack Recode

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-green)
![Loader](https://img.shields.io/badge/Loader-Fabric-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

A modern Minecraft 1.21.4 Fabric client that combines **Creepy SalHack** (1.12.2) with a clean, extensible architecture built from scratch.

## Requirements

| Requirement | Version |
|---|---|
| Minecraft | 1.21.4 |
| Fabric Loader | ≥ 0.16.10 |
| Fabric API | 0.119.2+1.21.4 |
| Java | 21 |

---

## Building

```bash
git clone https://github.com/hippopillow/creepy-salhack-recode
cd creepysalhack
./gradlew build
```

Output: `build/libs/creepysalhack-1.0.0.jar`

Drop the jar into `.minecraft/mods/` alongside Fabric Loader and Fabric API.

---

## Project Structure

```
src/main/java/dev/creepysalhack/
├── CreepySalHack.java          ← Entrypoint & global accessors
├── event/                      ← EventBus + all event types
├── manager/                    ← ModuleManager, ConfigManager, etc.
├── module/
│   ├── Module.java             ← Base class – extend this
│   ├── Setting.java            ← Typed setting (Boolean, Float, Enum…)
│   ├── combat/
│   ├── misc/
│   ├── render/
│   ├── movement/
│   ├── player/
│   └── client/
├── gui/
│   ├── click/                  ← ClickGUI
│   └── hud/                    ← HUD elements & editor
├── mixin/                      ← Fabric mixins
└── util/                       ← RenderUtil, EntityUtil, BlockUtil, etc.
```

---

## Adding a Module

**1. Create your class** in the right category package:

```java
package dev.creepysalhack.module.misc;

import dev.creepysalhack.event.EventBus;
import dev.creepysalhack.event.Events;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;

public class MyModule extends Module {

    private final Setting<Boolean> enabled = register(new Setting<>(
            "MyToggle", "Does something cool", true));

    private final Setting<Float> speed = register(new Setting<>(
            "Speed", "How fast", 1.0f, 0.1f, 5.0f));

    public MyModule() {
        super("MyModule", "Short description", Category.MISC);
    }

    @EventBus.EventHandler
    public void onTick(Events.ClientTickEvent event) {
        if (event.getPhase() != Events.ClientTickEvent.Phase.PRE) return;
        // your logic here
    }

    @Override
    public String getArrayListSuffix() {
        return String.format("%.1f", speed.getValue());
    }
}
```

**2. Register it** — one line in `ModuleManager.java`:

```java
add(new MyModule());
```

That's it. Settings, GUI, config, and arraylist are all automatic.

---

## Commands

| Command | Description |
|---|---|
| `.help` | List all commands |
| `.toggle <module>` | Toggle a module on or off |
| `.bind <module> <key>` | Bind a module to a key |
| `.set <module> <setting> <value>` | Change a module setting |
| `.friend <add\|remove\|list> [name]` | Manage your friend list |
| `.config <save\|load>` | Save or load config manually |

---

## Available Events

| Event | Phases | Cancellable |
|---|---|---|
| `ClientTickEvent` | PRE, POST | No |
| `PlayerUpdateEvent` | PRE, POST | No |
| `PlayerJumpEvent` | — | Yes |
| `RenderWorldEvent` | — | No |
| `RenderHudEvent` | — | No |
| `EntityRenderEvent` | — | Yes |
| `SendChatEvent` | — | Yes |
| `AttackEntityEvent` | — | Yes |
| `PacketSendEvent` | — | Yes |
| `PacketReceiveEvent` | — | Yes |
| `KeyPressEvent` | — | No |

---

## Credits

- **Creepy SalHack** — original 1.12.2 Forge client, aesthetic and module inspiration
- Built on **Fabric Loader** and **Fabric API**
