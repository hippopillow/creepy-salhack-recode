package dev.creepysalhack.module.render;

import dev.creepysalhack.event.EventBus;
import dev.creepysalhack.event.Events;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;

public class TracersModule extends Module {

    public enum TargetMode { PLAYERS, ALL }

    private final Setting<TargetMode> targets = register(new Setting<>(
            "Targets", "Which entities to draw tracers to", TargetMode.PLAYERS));
    private final Setting<Float> lineWidth = register(new Setting<>(
            "Width", "Tracer line width", 1.0f, 0.5f, 3.0f));
    private final Setting<Boolean> rainbow = register(new Setting<>(
            "Rainbow", "Use rainbow colours", true));

    public TracersModule() {
        super("Tracers", "Draws lines to nearby entities", Category.RENDER);
    }

    @EventBus.EventHandler
    public void onRenderWorld(Events.RenderWorldEvent event) {
        if (mc.world == null || mc.player == null) return;

        Vec3d cam    = mc.gameRenderer.getCamera().getPos();
        float delta  = event.getTickDelta();

        // Screen centre (bottom of camera)
        double srcX  = cam.x;
        double srcY  = cam.y - 0.05;
        double srcZ  = cam.z;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(lineWidth.getValue());
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f mat = event.getMatrices().peek().getPositionMatrix();
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        int idx = 0;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (targets.getValue() == TargetMode.PLAYERS && !(e instanceof PlayerEntity)) continue;

            Vec3d pos = e.getLerpedPos(delta).add(0, e.getHeight() / 2.0, 0);

            float r, g, b;
            if (rainbow.getValue()) {
                int col = RenderUtil.getTimeRainbow(0.3f + idx * 0.02f, 0.7f, 1f, 255);
                r = (col >> 16 & 0xFF) / 255f;
                g = (col >>  8 & 0xFF) / 255f;
                b = (col       & 0xFF) / 255f;
            } else {
                r = e instanceof PlayerEntity ? 1f : 1f;
                g = e instanceof PlayerEntity ? 0f : 0.5f;
                b = 0f;
            }

            buf.vertex(mat,
                    (float)(srcX - cam.x), (float)(srcY - cam.y), (float)(srcZ - cam.z))
               .color(r, g, b, 0.9f);
            buf.vertex(mat,
                    (float)(pos.x - cam.x), (float)(pos.y - cam.y), (float)(pos.z - cam.z))
               .color(r, g, b, 0.9f);
            idx++;
        }

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1f);
    }

    @Override
    public String getArrayListSuffix() { return targets.getValue().name(); }
}
