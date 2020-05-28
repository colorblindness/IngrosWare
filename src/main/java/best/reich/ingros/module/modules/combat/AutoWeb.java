package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.game.InventoryUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "AutoWeb", category = ModuleCategory.COMBAT, color = 0x4BF7FF)
public class AutoWeb extends ToggleableModule {

    @Setting("Range")
    public double range = 3.4;

    @Setting("Speed")
    @Clamp(minimum = "1")
    public int speed = 8;

    @Setting("Swing")
    public boolean swing = false;

    @Setting("Players")
    public boolean players = true;

    @Setting("Monsters")
    public boolean monsters = true;

    @Setting("Animals")
    public boolean animals = true;

    @Setting("Passives")
    public boolean passives = true;

    @Setting("Invisibles")
    public boolean invisibles = true;

    @Setting("ShowRotations")
    public boolean showRotations = false;

    @Setting("Replenish")
    public boolean replenish = true;

    public static List<EntityLivingBase> targets = new ArrayList();
    public static EntityLivingBase target;
    public TimerUtil placeTimer = new TimerUtil();


    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            target = null;
        }
        if (event.getType().equals(EventType.PRE)) {
            targets = getTargets();
            if (!targets.isEmpty()) {
                target = targets.get(0);
                BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
                if (canPlace()) {
                    float[] rotation = getRotationFromPosition(pos.getX() + 0.5, pos.getZ() + 0.5, pos.add(0, 0, 0).getY() - mc.player.getEyeHeight());
                    event.setYaw(!showRotations ? mc.player.rotationYaw = (rotation[0]) : rotation[0]);
                    event.setPitch(!showRotations ? mc.player.rotationPitch = rotation[1] : rotation[1]);
                    int lastSlot;
                    int slot;
                    if (InventoryUtil.getItemSlot(mc.player.inventoryContainer, Item.getItemById(30)) < 36 && replenish) {
                        InventoryUtil.swap(InventoryUtil.getItemSlot(mc.player.inventoryContainer, Item.getItemById(30)), 44);
                    }
                    slot = InventoryUtil.getItemSlotInHotbar(Item.getItemById(30));
                    lastSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = slot;
                    mc.playerController.updateController();
                    if (placeTimer.reach(1000 / speed)) {
                        for (final EnumFacing side : EnumFacing.values()) {
                            final BlockPos neighbor = pos.offset(side);
                            if (canBeClicked(neighbor)) {
                                place(neighbor, side.getOpposite());
                            }
                        }
                        placeTimer.reset();
                    }
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
            } else if (target != null)
                target = null;
        }
    }


    public boolean canBeClicked(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    public boolean canPlace() {
        return InventoryUtil.getItemCount(mc.player.inventoryContainer, Item.getItemById(30)) != 0;
    }

    public void place(BlockPos pos, EnumFacing direction) {
        if (swing) {
            mc.player.swingArm(mc.player.getActiveHand());
        } else {
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Object o : mc.world.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (isValidEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> {
            float[] rot1 = getRotations(o1);
            float[] rot2 = getRotations(o2);
            return Float.compare(rot2[0], rot1[0]);
        });
        return targets;
    }

    public static float[] getRotations(Entity ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.getEntityBoundingBox().maxY - 4.0;
        return getRotationFromPosition(x, z, y);
    }


    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().player.posX;
        double zDiff = z - Minecraft.getMinecraft().player.posZ;
        double yDiff = y - Minecraft.getMinecraft().player.posY;
        double hypotenuse = Math.hypot(xDiff, zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-Math.atan2(yDiff, hypotenuse) * 180.0 / Math.PI);
        return new float[]{yaw, pitch};
    }

    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityLivingBase && entity.getEntityId() != -1488 && entity != mc.player && entity.isEntityAlive() && !IngrosWare.INSTANCE.friendManager.isFriend(entity.getName()) && !(entity.isInvisible() && !invisibles) && mc.player.getDistanceSqToEntity(entity) <= range * range && ((entity instanceof EntityPlayer && players) || ((entity instanceof EntityMob || entity instanceof EntityGolem) && monsters) || (entity instanceof IAnimals && animals)) || (passives && (entity instanceof EntityIronGolem || entity instanceof EntityAmbientCreature));
    }
}