package dev.creepysalhack.gui.click;

import java.util.Random;

/** A single falling snow particle used in the ClickGUI background. */
public class SnowParticle {

    private float x, y;
    private final float speed;
    private final int   size;
    private final int   screenWidth;
    private final int   screenHeight;

    private static final Random RNG = new Random();

    public SnowParticle(int screenWidth, int screenHeight) {
        this.screenWidth  = screenWidth;
        this.screenHeight = screenHeight;
        this.x            = RNG.nextInt(screenWidth);
        this.y            = RNG.nextInt(screenHeight);
        this.speed        = 0.3f + RNG.nextFloat() * 0.7f;
        this.size         = 1 + RNG.nextInt(2);
    }

    /** Move the particle down; wrap to top when it leaves the screen. */
    public void tick() {
        y += speed;
        x += (float) Math.sin(y * 0.03f) * 0.4f;
        if (y > screenHeight) {
            y = -size;
            x = RNG.nextInt(screenWidth);
        }
    }

    public float getX()  { return x; }
    public float getY()  { return y; }
    public int   getSize(){ return size; }
}
