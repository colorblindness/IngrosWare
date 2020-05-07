package best.reich.ingros.notification;

import best.reich.ingros.IngrosWare;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class NotificationManager {
    private ArrayList<Notification> notifications = new ArrayList<>();

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void addNotification(String text, long duration) {
        getNotifications().add(new Notification(text,duration));
    }

    public void renderNotifications() {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        float neededY = scaledResolution.getScaledHeight() - 12;
        for (int i = 0; i < IngrosWare.INSTANCE.notificationManager.getNotifications().size(); i++) {
            IngrosWare.INSTANCE.notificationManager.getNotifications().get(i).renderNotification(neededY -= 30);
        }
    }
}
