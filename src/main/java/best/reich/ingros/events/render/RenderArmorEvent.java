package best.reich.ingros.events.render;

import net.b0at.api.event.Event;
import net.minecraft.inventory.EntityEquipmentSlot;

public class RenderArmorEvent extends Event {

    public boolean shouldNotRenderArmor(EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD)
            return true;
        if (entityEquipmentSlot == EntityEquipmentSlot.CHEST)
            return true;
        if (entityEquipmentSlot == EntityEquipmentSlot.LEGS)
            return true;
        if (entityEquipmentSlot == EntityEquipmentSlot.FEET)
            return true;
        return false;
    }
}
