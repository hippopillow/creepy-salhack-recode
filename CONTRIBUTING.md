# Creepy SalHack Recode – Module Guide

## Project Structure

```
src/main/java/dev/creepysalhack/
├── CreepySalHack.java          ← Client entrypoint & global accessors
├── event/                      ← EventBus + all event types
│   ├── EventBus.java
│   ├── Event.java
│   ├── CancellableEvent.java
│   └── Events.java             ← All game events defined here
├── manager/                    ← Singletons (access via CreepySalHack.get*())
│   ├── ModuleManager.java      ← Register your module here
│   ├── ConfigManager.java      ← Auto save/load from CreepySalHack/modules.json
│   ├── EventManager.java
│   ├── CommandManager.java
│   ├── FriendManager.java
│   ├── FontManager.java
│   ├── HudManager.java
│   └── NotifManager.java
├── module/
│   ├── Module.java             ← Base class – extend this
│   ├── Setting.java            ← Typed setting (Boolean, Float, Enum, etc.)
│   ├── combat/
│   ├── misc/
│   ├── render/
│   ├── movement/
│   ├── player/
│   └── client/
├── gui/
│   ├── click/                  ← ClickGUI (Right Shift to open)
│   └── hud/                    ← HUD elements & HUD Editor
├── mixin/                      ← Fabric mixins
└── util/                       ← Helpers: RenderUtil, EntityUtil, BlockUtil, etc.
```

---

## Adding a Module – Step by Step

### 1. Create your class

```java
package dev.creepysalhack.module.misc; // pick the right category package

import dev.creepysalhack.event.EventBus;
import dev.creepysalhack.event.Events;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;

public class ExampleModule extends Module {

    // ── Settings ──────────────────────────────────────────────────────────────
    // Call register() so settings appear in the ClickGUI and get saved/loaded.

    private final Setting<Boolean> doSomething = register(new Setting<>(
            "DoSomething",          // name shown in GUI
            "Toggles the feature",  // tooltip
            true));                 // default value

    private final Setting<Float> intensity = register(new Setting<>(
            "Intensity",
            "How intense the effect is",
            1.0f,   // default
            0.0f,   // min
            5.0f)); // max

    public enum Mode { FAST, SLOW, NORMAL }

    private final Setting<Mode> mode = register(new Setting<>(
            "Mode",
            "Operating mode",
            Mode.NORMAL));

    // ── Constructor ───────────────────────────────────────────────────────────

    public ExampleModule() {
        super(
            "Example",              // display name (shown in arraylist + GUI)
            "An example module",    // description
            Category.MISC           // category (COMBAT/MISC/RENDER/MOVEMENT/PLAYER/CLIENT)
        );
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    // onEnable/onDisable are optional – the base class handles event subscription.

    @Override
    public void onEnable() {
        super.onEnable(); // IMPORTANT: always call super – registers event handlers
        // one-time setup here
    }

    @Override
    public void onDisable() {
        super.onDisable(); // IMPORTANT: always call super – unregisters handlers
        // cleanup here
    }

    // ── Events ────────────────────────────────────────────────────────────────
    // Annotate with @EventBus.EventHandler – auto-subscribed when enabled.

    @EventBus.EventHandler
    public void onTick(Events.ClientTickEvent event) {
        if (event.getPhase() != Events.ClientTickEvent.Phase.PRE) return;
        if (!doSomething.getValue()) return;

        // your per-tick logic here
        float power = intensity.getValue();
        switch (mode.getValue()) {
            case FAST   -> doFast(power);
            case SLOW   -> doSlow(power);
            case NORMAL -> doNormal(power);
        }
    }

    @EventBus.EventHandler
    public void onRenderHud(Events.RenderHudEvent event) {
        // draw something on the HUD
    }

    @EventBus.EventHandler
    public void onRenderWorld(Events.RenderWorldEvent event) {
        // draw something in 3D world space
    }

    // ── Arraylist suffix ──────────────────────────────────────────────────────
    // Optional: shown after the module name in the right-side arraylist.

    @Override
    public String getArrayListSuffix() {
        return mode.getValue().name();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void doFast(float power)   { /* ... */ }
    private void doSlow(float power)   { /* ... */ }
    private void doNormal(float power) { /* ... */ }
}
```

### 2. Register it in ModuleManager

Open `src/main/java/dev/creepysalhack/manager/ModuleManager.java` and add one line
in the relevant section of the constructor:

```java
// ── Misc ──────────────────────────────────────────────────────────────────
add(new ExampleModule());  // ← add this
add(new AutoReconnectModule());
// ...
```

That's it. The module will:
- Appear in the ClickGUI under its category panel
- Have its settings rendered and interactive
- Be saved/loaded from `%minecraft%/CreepySalHack/modules.json`
- Appear in the right-side arraylist when enabled
- Be toggleable with `.toggle ExampleModule` in chat

---

## Available Events

| Event class | When it fires |
|---|---|
| `Events.ClientTickEvent` | Every game tick (PRE / POST phase) |
| `Events.PlayerUpdateEvent` | Every player update (PRE / POST) |
| `Events.PlayerJumpEvent` | When the player jumps (cancellable) |
| `Events.RenderWorldEvent` | During 3D world render |
| `Events.RenderHudEvent` | During HUD render |
| `Events.EntityRenderEvent` | Before each entity is rendered (cancellable) |
| `Events.SendChatEvent` | Before a chat message is sent (cancellable) |
| `Events.AttackEntityEvent` | When the player attacks an entity (cancellable) |
| `Events.PacketSendEvent` | Before a packet is sent (cancellable) |
| `Events.PacketReceiveEvent` | When a packet is received (cancellable) |
| `Events.KeyPressEvent` | When a key is pressed |

---

## Useful Utilities

```java
// ── Rendering ────────────────────────────────────────────────────────────────
RenderUtil.drawRect(matrices, x, y, w, h, colour);
RenderUtil.drawRoundedRect(matrices, x, y, w, h, radius, colour);
RenderUtil.drawBox(matrices, box, colour, alpha);          // 3D filled box
RenderUtil.drawBoxOutline(matrices, box, colour, width);   // 3D wireframe
RenderUtil.getTimeRainbow(speed, sat, bri, alpha);         // animated rainbow int

// ── Entities ─────────────────────────────────────────────────────────────────
EntityUtil.getPlayers();                    // List<PlayerEntity> (excl. self)
EntityUtil.getLivingEntities();             // all living entities
EntityUtil.getNearestPlayer(range);         // closest player within range
EntityUtil.isFriend(entity);               // check friend list

// ── Player ────────────────────────────────────────────────────────────────────
PlayerUtil.isNull();                        // safety check
PlayerUtil.getHealth();                     // hp + absorption
PlayerUtil.countTotems();                   // totems in inventory
PlayerUtil.getHorizontalSpeed();            // blocks/tick

// ── Blocks ────────────────────────────────────────────────────────────────────
BlockUtil.isAir(pos);
BlockUtil.isSolid(pos);
BlockUtil.isCrystalBase(pos);              // obsidian or bedrock
BlockUtil.distanceTo(pos);

// ── Math ──────────────────────────────────────────────────────────────────────
MathUtil.getYaw(target);                   // yaw toward Vec3d
MathUtil.getPitch(target);                 // pitch toward Vec3d
MathUtil.clamp(val, min, max);

// ── Timer ─────────────────────────────────────────────────────────────────────
TimerUtil timer = new TimerUtil();
timer.hasReached(500);                     // true if 500ms elapsed
timer.reset();

// ── Notifications ─────────────────────────────────────────────────────────────
CreepySalHack.getNotifManager().success("Title", "Message");
CreepySalHack.getNotifManager().error("Title", "Message");
CreepySalHack.getNotifManager().warn("Title", "Message");
CreepySalHack.getNotifManager().info("Title", "Message");
```

---

## Adding a HUD Element

Extend `HudElement` and register it in `HudManager`:

```java
public class MyHudElement extends HudElement {

    public MyHudElement() { super("MyElement", 200, 50); } // name, defaultX, defaultY

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        String text = "Hello HUD!";
        RenderUtil.drawRoundedRect(ctx.getMatrices(), getX(), getY(),
                mc.textRenderer.getWidth(text) + 8,
                mc.textRenderer.fontHeight + 6, 3,
                RenderUtil.toARGB(10, 10, 10, 160));
        ctx.drawTextWithShadow(mc.textRenderer, text,
                (int)getX() + 4, (int)getY() + 3, 0xFFFFFFFF);
    }

    @Override public float getWidth()  { return mc.textRenderer.getWidth("Hello HUD!") + 8; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
```

Then in `HudManager.java`:
```java
register(new MyHudElement());
```

---

## Building

```bash
./gradlew build
# Output: build/libs/creepysalhack-1.0.0.jar
# Drop into .minecraft/mods/ alongside Fabric Loader 0.16.x
```
