package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.util.game.TickRate;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Mode;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.TimerUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
@ModuleManifest(label = "KillAura", category = ModuleCategory.COMBAT, color = 0xff800000)
public class KillAura extends ToggleableModule {
    public EntityLivingBase target;
    @Setting("SortMode")
    @Mode({"FOV", "HEALTH", "DISTANCE"})
    public String sortmode = "FOV";
    @Setting("Mode")
    @Mode({"NORMAL", "SMOOTH"})
    public String mode = "SMOOTH";
    @Clamp(minimum = "0.1", maximum = "7.0")
    @Setting("Range")
    public double range = 3.4;
    @Setting("Criticals")
    public boolean criticals;
    @Setting("Invisibles")
    public boolean invisibles = true;
    @Setting("Players")
    public boolean players = true;
    @Setting("Animals")
    public boolean animals;
    @Setting("Monsters")
    public boolean monsters;
    @Setting("Passives")
    public boolean passives;
    private final TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEnable() {
        super.onEnable();
        timerUtil.reset();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null)
            return;
        if (event.getType() == EventType.PRE) {
            setSuffix(sortmode);
            target = getTarget();
            if (target != null) {
                final float[] rots = getRotationsToward(target);
                if ("SMOOTH".equals(mode.toUpperCase())) {
                    float sens = getSensitivityMultiplier();
                    float yawGCD = (Math.round(rots[0] / sens) * sens);
                    float pitchGCD = (Math.round(rots[1] / sens) * sens);
                    event.setYaw(yawGCD);
                    event.setPitch(pitchGCD);
                } else {
                    event.setYaw(rots[0]);
                    event.setPitch(rots[1]);
                }
            }
        } else {
            if (target == null)
                return;
            final float ticks = 20.0f - TickRate.TPS;
            final boolean canAttack = mc.player.getCooledAttackStrength(-ticks) >= 1 && target.hurtResistantTime <= 11;
            if (canAttack) {
                attackEntity(target);
            }
        }
    }

    private void attackEntity(EntityLivingBase entityLivingBase) {
        final ItemStack stack = mc.player.getHeldItem(EnumHand.OFF_HAND);
        if (stack != ItemStack.EMPTY && stack.getItem() == Items.SHIELD) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        }
        if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && criticals) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        }
        mc.player.connection.sendPacket(new CPacketUseEntity(entityLivingBase));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.resetCooldown();
    }

    private EntityLivingBase getTarget() {
        double minVal = Double.POSITIVE_INFINITY;
        Entity bestEntity = null;
        for (Entity e : mc.world.loadedEntityList) {
            double val = getSortingWeight(e);
            if (isValidEntity(e) && val < minVal) {
                minVal = val;
                bestEntity = e;
            }
        }
        return (EntityLivingBase) bestEntity;
    }

    private double getSortingWeight(Entity e) {
        switch (sortmode.toUpperCase()) {
            case "FOV":
                return yawDist(e);
            case "HEALTH":
                return e instanceof EntityLivingBase ? ((EntityLivingBase) e).getHealth() : Double.POSITIVE_INFINITY;
            default:
                return mc.player.getDistanceSqToEntity(e);
        }
    }

    private double yawDist(Entity e) {
        if (e != null) {
            final Vec3d difference = e.getPositionVector().addVector(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(mc.player.getPositionVector().addVector(0.0, mc.player.getEyeHeight(), 0.0));
            final double d = Math.abs(mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0f)) % 360.0f;
            return (d > 180.0f) ? (360.0f - d) : d;
        }
        return 0;
    }

    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityLivingBase && entity.getEntityId() != -1488 && entity != mc.player && entity.isEntityAlive() && !IngrosWare.INSTANCE.friendManager.isFriend(entity.getName()) && !(entity.isInvisible() && !invisibles) && mc.player.getDistanceSqToEntity(entity) <= range * range && ((entity instanceof EntityPlayer && players) || ((entity instanceof EntityMob || entity instanceof EntityGolem) && monsters) || (entity instanceof IAnimals && animals)) || (passives && (entity instanceof EntityIronGolem || entity instanceof EntityAmbientCreature));
    }

    private float[] getRotationsToward(final Entity closestEntity) {
        double xDist = closestEntity.posX - mc.player.posX;
        double yDist = closestEntity.posY + closestEntity.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight());
        double zDist = closestEntity.posZ - mc.player.posZ;
        double fDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
        float yaw = this.fixRotation(mc.player.rotationYaw, (float) (MathHelper.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F, 360F);
        float pitch = this.fixRotation(mc.player.rotationPitch, (float) (-(MathHelper.atan2(yDist, fDist) * 180.0D / Math.PI)), 360F);
        return new float[]{yaw, pitch};
    }

    private float fixRotation(final float p_70663_1_, final float p_70663_2_, final float p_70663_3_) {
        float var4 = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
        if (var4 > p_70663_3_) {
            var4 = p_70663_3_;
        }
        if (var4 < -p_70663_3_) {
            var4 = -p_70663_3_;
        }
        return p_70663_1_ + var4;
    }

    private float getSensitivityMultiplier() {
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        return (f * f * f * 8.0F) * 0.15F;
    }
}
