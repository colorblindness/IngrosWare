package best.reich.ingros.notification;


import best.reich.ingros.IngrosWare;
import best.reich.ingros.module.persistent.Overlay;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.util.math.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Notification {
    private final TimerUtil timer;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private float x, oldX, y, oldY, width;
    private final String text;
    private final long stayTime;
    private boolean done;
    private float stayBar;

    Notification(String text, long stayTime) {
        this.x = new ScaledResolution(mc).getScaledWidth() - 2;
        this.y = new ScaledResolution(mc).getScaledHeight() - 2;
        this.text = text;
        this.stayTime = stayTime;
        timer = new TimerUtil();
        timer.reset();
        stayBar = stayTime;
        done = false;
    }

    public void renderNotification(float prevY) {
        final Overlay hud = (Overlay) IngrosWare.INSTANCE.moduleManager.getModule("Overlay");
        final float xSpeed = width / (Minecraft.getDebugFPS() / 4);
        final float ySpeed = (new ScaledResolution(mc).getScaledHeight() - prevY) / (Minecraft.getDebugFPS() / 8);
        if (width != RenderUtil.getTextWidth(text,hud.font) + 4) {
            width = RenderUtil.getTextWidth(text,hud.font) + 4;
        }
        if (done) {
            oldX = x;
            x += xSpeed;
            y += ySpeed;
        }
        if (!done() && !done) {
            timer.reset();
            oldX = x;
            if (x <= new ScaledResolution(mc).getScaledWidth() - 2 - width + xSpeed)
                x = new ScaledResolution(mc).getScaledWidth() - 2 - width;
            else x -= xSpeed;
        } else if (timer.reach(stayTime)) done = true;
        if (x < new ScaledResolution(mc).getScaledWidth() - 2 - width) {
            oldX = x;
            x += xSpeed;
        }
        if (y != prevY) {
            if (y > prevY + ySpeed) {
                y -= ySpeed;
            } else {
                y = prevY;
            }
        } else if (y < prevY) {
            oldY = y;
            y += ySpeed;
        }
        if (done() && !done) {
            stayBar = timer.time();
        }
        final float finishedX = oldX + (x - oldX);
        final float finishedY = oldY + (y - oldY);
        RenderUtil.drawRect(finishedX, finishedY, width, 27, new Color(21, 21, 21, 200).getRGB());
        RenderUtil.drawText(text, finishedX + 2, finishedY + 10, -1,hud.font);
        if (done())
            RenderUtil.drawRect(finishedX, finishedY + 26, ((width - 1) / stayTime) * stayBar, 1, hud.getHudColor());
        if (delete()) IngrosWare.INSTANCE.notificationManager.getNotifications().remove(this);
    }

    public boolean done() {
        return x <= new ScaledResolution(mc).getScaledWidth() - 2 - width;
    }

    public boolean delete() {
        return x >= new ScaledResolution(mc).getScaledWidth() - 2 && done;
    }

}