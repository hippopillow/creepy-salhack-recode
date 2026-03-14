package dev.creepysalhack.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

public final class RenderUtil {

    private RenderUtil() {}

    // ── 2-D helpers ──────────────────────────────────────────────────────────

    public static void drawRect(MatrixStack matrices, float x, float y,
                                float width, float height, int color) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >>  8 & 0xFF) / 255f;
        float b = (color       & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buf.vertex(m, x,         y,          0).color(r,g,b,a);
        buf.vertex(m, x,         y + height, 0).color(r,g,b,a);
        buf.vertex(m, x + width, y + height, 0).color(r,g,b,a);
        buf.vertex(m, x + width, y,          0).color(r,g,b,a);
        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.disableBlend();
    }

    public static void drawRectOutline(MatrixStack matrices, float x, float y,
                                       float width, float height,
                                       float lineWidth, int color) {
        RenderSystem.lineWidth(lineWidth);
        drawRect(matrices, x, y, width, 1, color);
        drawRect(matrices, x, y + height - 1, width, 1, color);
        drawRect(matrices, x, y, 1, height, color);
        drawRect(matrices, x + width - 1, y, 1, height, color);
    }

    /** Rounded rect via manual triangle fan – lightweight approximation. */
    public static void drawRoundedRect(MatrixStack matrices, float x, float y,
                                       float w, float h, float radius, int color) {
        drawRect(matrices, x + radius, y,          w - radius*2, h,       color);
        drawRect(matrices, x,          y + radius, radius,       h - radius*2, color);
        drawRect(matrices, x + w - radius, y + radius, radius,  h - radius*2, color);
        // corners approximated by small squares
        drawRect(matrices, x,              y,              radius, radius, color);
        drawRect(matrices, x + w - radius, y,              radius, radius, color);
        drawRect(matrices, x,              y + h - radius, radius, radius, color);
        drawRect(matrices, x + w - radius, y + h - radius, radius, radius, color);
    }

    public static void drawGradientRect(MatrixStack matrices, float x, float y,
                                        float width, float height,
                                        int colorTop, int colorBottom) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        float a1=(colorTop>>24&0xFF)/255f, r1=(colorTop>>16&0xFF)/255f,
              g1=(colorTop>>8&0xFF)/255f,  b1=(colorTop&0xFF)/255f;
        float a2=(colorBottom>>24&0xFF)/255f, r2=(colorBottom>>16&0xFF)/255f,
              g2=(colorBottom>>8&0xFF)/255f,  b2=(colorBottom&0xFF)/255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buf.vertex(m, x,       y,        0).color(r1,g1,b1,a1);
        buf.vertex(m, x,       y+height, 0).color(r2,g2,b2,a2);
        buf.vertex(m, x+width, y+height, 0).color(r2,g2,b2,a2);
        buf.vertex(m, x+width, y,        0).color(r1,g1,b1,a1);
        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.disableBlend();
    }

    // ── 3-D ESP helpers ───────────────────────────────────────────────────────

    /**
     * Draw a filled axis-aligned box in world space.
     * Call inside a RenderWorldEvent with depth-test disabled for X-ray.
     */
    public static void drawBox(MatrixStack matrices, Box box, int color, float alpha) {
        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        float r=(color>>16&0xFF)/255f, g=(color>>8&0xFF)/255f, b=(color&0xFF)/255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f m = matrices.peek().getPositionMatrix();
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float x1=(float)box.minX, y1=(float)box.minY, z1=(float)box.minZ;
        float x2=(float)box.maxX, y2=(float)box.maxY, z2=(float)box.maxZ;

        // bottom
        buf.vertex(m,x1,y1,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y1,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y1,z2).color(r,g,b,alpha);
        buf.vertex(m,x1,y1,z2).color(r,g,b,alpha);
        // top
        buf.vertex(m,x1,y2,z1).color(r,g,b,alpha);
        buf.vertex(m,x1,y2,z2).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z2).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z1).color(r,g,b,alpha);
        // sides
        buf.vertex(m,x1,y1,z1).color(r,g,b,alpha);
        buf.vertex(m,x1,y2,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y1,z1).color(r,g,b,alpha);

        buf.vertex(m,x2,y1,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z1).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z2).color(r,g,b,alpha);
        buf.vertex(m,x2,y1,z2).color(r,g,b,alpha);

        buf.vertex(m,x1,y1,z2).color(r,g,b,alpha);
        buf.vertex(m,x2,y1,z2).color(r,g,b,alpha);
        buf.vertex(m,x2,y2,z2).color(r,g,b,alpha);
        buf.vertex(m,x1,y2,z2).color(r,g,b,alpha);

        buf.vertex(m,x1,y1,z1).color(r,g,b,alpha);
        buf.vertex(m,x1,y1,z2).color(r,g,b,alpha);
        buf.vertex(m,x1,y2,z2).color(r,g,b,alpha);
        buf.vertex(m,x1,y2,z1).color(r,g,b,alpha);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    /** Draw wireframe outline of a box. */
    public static void drawBoxOutline(MatrixStack matrices, Box box, int color, float lineWidth) {
        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        float r=(color>>16&0xFF)/255f, g=(color>>8&0xFF)/255f,
              b=(color&0xFF)/255f,     a=(color>>24&0xFF)/255f;

        RenderSystem.lineWidth(lineWidth);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f m = matrices.peek().getPositionMatrix();
        float x1=(float)box.minX,y1=(float)box.minY,z1=(float)box.minZ;
        float x2=(float)box.maxX,y2=(float)box.maxY,z2=(float)box.maxZ;

        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // 12 edges
        line(buf,m, x1,y1,z1, x2,y1,z1, r,g,b,a); line(buf,m, x2,y1,z1, x2,y1,z2, r,g,b,a);
        line(buf,m, x2,y1,z2, x1,y1,z2, r,g,b,a); line(buf,m, x1,y1,z2, x1,y1,z1, r,g,b,a);
        line(buf,m, x1,y2,z1, x2,y2,z1, r,g,b,a); line(buf,m, x2,y2,z1, x2,y2,z2, r,g,b,a);
        line(buf,m, x2,y2,z2, x1,y2,z2, r,g,b,a); line(buf,m, x1,y2,z2, x1,y2,z1, r,g,b,a);
        line(buf,m, x1,y1,z1, x1,y2,z1, r,g,b,a); line(buf,m, x2,y1,z1, x2,y2,z1, r,g,b,a);
        line(buf,m, x2,y1,z2, x2,y2,z2, r,g,b,a); line(buf,m, x1,y1,z2, x1,y2,z2, r,g,b,a);

        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static void line(BufferBuilder b, Matrix4f m,
                              float x1,float y1,float z1,
                              float x2,float y2,float z2,
                              float r,float g,float bv,float a) {
        b.vertex(m,x1,y1,z1).color(r,g,bv,a);
        b.vertex(m,x2,y2,z2).color(r,g,bv,a);
    }

    // ── Color helpers ─────────────────────────────────────────────────────────

    public static int toARGB(int r, int g, int b, int a) {
        return (a&0xFF)<<24 | (r&0xFF)<<16 | (g&0xFF)<<8 | (b&0xFF);
    }

    public static int withAlpha(int rgb, int alpha) {
        return (rgb & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /** HSB → ARGB integer. */
    public static int rainbow(float hue, float sat, float bri, int alpha) {
        int rgb = Color.HSBtoRGB(hue, sat, bri);
        return withAlpha(rgb, alpha);
    }

    /** Smooth rainbow that shifts with time. */
    public static int getTimeRainbow(float speed, float sat, float bri, int alpha) {
        float hue = (System.currentTimeMillis() % (long)(1000/speed)) / (1000f/speed);
        return rainbow(hue, sat, bri, alpha);
    }
}
