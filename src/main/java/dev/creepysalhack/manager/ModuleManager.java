package dev.creepysalhack.manager;

import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Module.Category;

// ── Combat ────────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.combat.*;
// ── Misc ──────────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.misc.*;
// ── Render ────────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.render.*;
// ── Movement ──────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.movement.*;
// ── Player ────────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.player.*;
// ── Client ────────────────────────────────────────────────────────────────────
import dev.creepysalhack.module.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // ── Combat ────────────────────────────────────────────────────────────
        add(new AuraModule());
        add(new AutoArmorModule());
        add(new AutoCrawlTrapModule());
        add(new AutoCrystalModule());
        add(new AutoLogModule());
        add(new AutoMineModule());
        add(new AutoTotemModule());
        add(new AutoTrapModule());
        add(new BlockerModule());
        add(new CombatPlaceModule());
        add(new CriticalsModule());
        add(new SelfTrapModule());
        add(new SurroundModule());

        // ── Misc ──────────────────────────────────────────────────────────────
        add(new AutoReconnectModule());
        add(new AutoGGModule());
        add(new AutoRegearModule());
        add(new AutoKitModule());
        add(new ChatModule());
        add(new ExtraTabModule());
        add(new FastLatencyModule());
        add(new FastTransactionsModule());
        add(new HitSoundModule());
        add(new KillSoundModule());
        add(new MiddleClickModule());
        add(new NoInterpModule());
        add(new NoPacketKickModule());
        add(new ReplenishModule());
        add(new SpammerModule());
        add(new UnfocusedCPUModule());
        add(new WeatherModule());

        // ── Render ────────────────────────────────────────────────────────────
        add(new AmbienceModule());
        add(new AnimationsModule());
        add(new BlockHighlightModule());
        add(new BreakHighlightModule());
        add(new ChamsModule());
        add(new FreecamModule());
        add(new FullbrightModule());
        add(new HolesModule());
        add(new ItemsModule());
        add(new KillEffectModule());
        add(new NametagsModule());
        add(new NoRenderModule());
        add(new PopChamsModule());
        add(new SearchModule());
        add(new ShadersModule());
        add(new TracersModule());
        add(new TrajectoriesModule());
        add(new ViewmodelModule());

        // ── Movement ──────────────────────────────────────────────────────────
        add(new ElytraFlyModule());
        add(new AccelerationModule());
        add(new AnchorModule());
        add(new FastFallModule());
        add(new FlightModule());
        add(new HoleSnapModule());
        add(new InvMoveModule());
        add(new NoFallModule());
        add(new NoJumpDelayModule());
        add(new NoPushModule());
        add(new NoSlowModule());
        add(new PhaseModule());
        add(new SpeedModule());
        add(new SprintModule());
        add(new StepModule());
        add(new TickShiftModule());
        add(new VelocityModule());

        // ── Player ────────────────────────────────────────────────────────────
        add(new AutoRespawnModule());
        add(new AutoXPModule());
        add(new BlockTweaksModule());
        add(new FastPlaceModule());
        add(new MultitaskModule());
        add(new NoInteractModule());
        add(new NoRotateModule());
        add(new ScaffoldModule());
        add(new ReachModule());
        add(new SpeedMineModule());
        add(new TimerModule());
        add(new XCarryModule());

        // ── Client ────────────────────────────────────────────────────────────
        add(new AccountModule());
        add(new CalculationsModule());
        add(new ColorsModule());
        add(new EngineModule());
        add(new FontModule());
        add(new HUDModule());
        add(new MediaModule());
        add(new NotificationsModule());
        add(new RenderModule());
        add(new RotationsModule());
        add(new UIModule());
        add(new SearchBarModule());
    }

    private void add(Module m) { modules.add(m); }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category cat) {
        return modules.stream().filter(m -> m.getCategory() == cat).toList();
    }

    public Optional<Module> getByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean isEnabled(String name) {
        return getByName(name).map(Module::isEnabled).orElse(false);
    }

    /** Called from the keyboard mixin every tick to fire bound keys. */
    public void handleKey(int keyCode) {
        for (Module m : modules) {
            if (m.getKeyBind() == keyCode) m.toggle();
        }
    }
}
