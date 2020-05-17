package best.reich.ingros.module.modules.combat;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.events.entity.UpdateEvent;
import best.reich.ingros.events.network.PacketEvent;
import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.mixin.accessors.IRenderManager;
import best.reich.ingros.util.logging.Logger;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Clamp;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.MathUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@ModuleManifest(label = "CrystalAura", category = ModuleCategory.COMBAT, color = 0x4BF7FF)
public class CrystalAura extends ToggleableModule {
    @Clamp(minimum = "1.0", maximum = "6.0")
    @Setting("EnemyRange")
    public float enemyRange = 6.0f;
    @Clamp(minimum = "1.0", maximum = "6.0")
    @Setting("PlaceRange")
    public float placeRange = 6.0f;
    @Clamp(maximum = "50")
    @Setting("PlaceDelay")
    public int placeDelay = 0;
    @Clamp(minimum = "1.0", maximum = "6.0")
    @Setting("BreakRange")
    public float breakRange = 6.0f;
    @Setting("AttackSpeed")
    public int attackSpeed = 17;
    @Clamp(minimum = "1", maximum = "16")
    @Setting("MinimumDamage")
    public int minDamage = 4;
    @Clamp(minimum = "1", maximum = "16")
    @Setting("Faceplace")
    public int facePlace = 2;
    @Clamp(minimum = "1")
    @Setting("MaxSelfDamage")
    public int maxDamage = 11;
    @Clamp(minimum = "1", maximum = "75")
    @Setting("MultiPlaceSpeed")
    public int multiPlaceSpeed = 2;
    @Setting("Place")
    public boolean place = true;
    @Setting("pSilent")
    public boolean pSilent = false;
    @Setting("RayTrace")
    public boolean rayTrace = true;
    @Setting("AutoSwitch")
    public boolean autoSwitch = true;
    @Setting("AntiStuck")
    public boolean antiStuck = true;
    @Setting("MultiPlace")
    public boolean multiPlace = false;
    @Setting("ShowRotations")
    public boolean showRotations = true;
    @Setting("Announcer")
    public boolean announcer = true;
    @Setting("AntiSuicide")
    public boolean antiSuicide = true;
    @Setting("PauseWhileEating")
    public boolean pauseWhileEating = true;
    @Setting("Color")
    public Color color = new Color(255,0,0);
    private BlockPos render;
    private String dmg;
    private long placeSystemTime;
    private long breakSystemTime;
    private long antiStuckSystemTime;
    private long multiPlaceSystemTime;
    private boolean switchCooldown;
    private final List<PlaceLocation> placeLocations = new CopyOnWriteArrayList<>();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null || pauseWhileEating && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.isHandActive()) return;
        final EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> entity).min(Comparator.comparing(c -> mc.player.getDistanceToEntity(c))).orElse(null);
        if (crystal != null && render != null && mc.player.getDistanceToEntity(crystal) <= breakRange) {
            if (event.getType() == EventType.PRE) {
                final float[] rots = MathUtil.calcAngle(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(render.getX() + 0.5, render.getY() + 1.0, render.getZ() + 0.5));
                if (showRotations) {
                    mc.player.rotationYaw = rots[0];
                    mc.player.rotationPitch = rots[1];
                } else {
                    event.setYaw(rots[0]);
                    event.setPitch(rots[1]);
                }
            } else if (System.nanoTime() / 1000000L - breakSystemTime >= 420 - attackSpeed * 20) {
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                breakSystemTime = System.nanoTime() / 1000000L;
            }
            if (multiPlace) {
                if (System.nanoTime() / 1000000L - multiPlaceSystemTime <= multiPlaceSpeed * 50 && multiPlaceSpeed < 10) {
                    if (!antiStuck) {
                        return;
                    }
                    if (System.nanoTime() / 1000000L - antiStuckSystemTime <= 300 + (400 - attackSpeed * 20)) {
                        multiPlaceSystemTime = System.nanoTime() / 1000000L;
                        return;
                    }
                }
            } else {
                if (!antiStuck) {
                    return;
                }
                if (System.nanoTime() / 1000000L - antiStuckSystemTime <= 300 + (400 - attackSpeed * 20)) {
                    return;
                }
            }
        }
        int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }
        BlockPos finalPos = null;
        final List<BlockPos> blocks = findCrystalBlocks();
        final List<Entity> entities = mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.player && entityPlayer.getEntityId() != -1488 && !IngrosWare.INSTANCE.friendManager.isFriend(entityPlayer.getName())).collect(Collectors.toList());
        double damage = 0.5;
        double prevSelf = 0.5;
        for (final Entity entity2 : entities) {
            if (((EntityLivingBase) entity2).getHealth() <= 0.0f || mc.player.getDistanceSqToEntity(entity2) > enemyRange * enemyRange) continue;
            for (final BlockPos blockPos : blocks) {
                final double d = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity2);
                final double self = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, mc.player);
                final double b = entity2.getDistanceSq(blockPos);
                if ((!canBlockBeSeen(blockPos) && mc.player.getDistanceSq(blockPos) > 25.0 && rayTrace) || b > 56.2 || d < minDamage && ((EntityLivingBase) entity2).getHealth() + ((EntityLivingBase) entity2).getAbsorptionAmount() > facePlace || maxDamage <= self || (antiSuicide && (mc.player.getHealth() + mc.player.getAbsorptionAmount() - self <= 7.0 || self > d || self >= mc.player.getHealth() + mc.player.getAbsorptionAmount())) || d <= damage && (Math.round(d) != Math.round(damage) || self >= prevSelf))
                    continue;
                damage = d;
                finalPos = blockPos;
                prevSelf = self;
            }
        }
        if (damage == 0.5) {
            render = null;
            dmg = null;
            return;
        }
        if (place) {
            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (autoSwitch) {
                    mc.player.inventory.currentItem = crystalSlot;
                    switchCooldown = true;
                }
                return;
            }
            final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(finalPos.getX() + 0.5, finalPos.getY() - 0.5, finalPos.getZ() + 0.5));
            final EnumFacing f = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
            if (switchCooldown) {
                switchCooldown = false;
                return;
            }
            if (System.nanoTime() / 1000000L - placeSystemTime >= placeDelay * 2) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(finalPos, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                this.placeLocations.add(new PlaceLocation(finalPos.getX(), finalPos.getY(), finalPos.getZ()));
                render = finalPos;
                dmg = MathHelper.floor(damage) + "hp";
                antiStuckSystemTime = System.nanoTime() / 1000000L;
                placeSystemTime = System.nanoTime() / 1000000L;
            }
        }
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (render != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(render.getX() - mc.getRenderManager().viewerPosX, render.getY() - mc.getRenderManager().viewerPosY + 1, render.getZ() - mc.getRenderManager().viewerPosZ, render.getX() + 1 - mc.getRenderManager().viewerPosX, render.getY() + 1.2 - mc.getRenderManager().viewerPosY, render.getZ() + 1 - mc.getRenderManager().viewerPosZ);
            if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                RenderUtil.drawESP(bb, color.getRed(), color.getGreen(), color.getBlue(), 40F);
                RenderUtil.drawESPOutline(bb, color.getRed(), color.getGreen(), color.getBlue(), 255f, 1f);
                final double posX = render.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
                final double posY = render.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
                final double posZ = render.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
                RenderUtil.renderTag(dmg, posX + 0.5, posY, posZ + 0.5, new Color(255, 0, 0).getRGB());
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @Subscribe
    public void onPacket(final PacketEvent event) {
        if (event.getType() == EventType.POST && event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (final Entity e : mc.world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
                        e.setDead();
                    }
                }
            }
        }
    }

    @Subscribe
    public void onReceivePacket(final PacketEvent event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packetSpawnObject = (SPacketSpawnObject) event.getPacket();
            if (event.getType() == EventType.PRE) {
                if (packetSpawnObject.getType() == 51) {
                    for (PlaceLocation placeLocation : this.placeLocations) {
                        if (!placeLocation.placed && placeLocation.getDistance((int) packetSpawnObject.getX(), (int) packetSpawnObject.getY() - 1, (int) packetSpawnObject.getZ()) <= 1) {
                            placeLocation.placed = true;
                            if (pSilent && !mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                                event.setCancelled(true);
                                CPacketUseEntity packetUseEntity = new CPacketUseEntity();
                                packetUseEntity.entityId = packetSpawnObject.getEntityID();
                                packetUseEntity.action = CPacketUseEntity.Action.ATTACK;

                                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(packetSpawnObject.getX() + 0.5, packetSpawnObject.getY() + 0.5, packetSpawnObject.getZ() + 0.5));
                                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
                                mc.player.connection.sendPacket(packetUseEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    private static final class PlaceLocation extends Vec3i {
        private boolean placed = false;
        private PlaceLocation(int xIn, int yIn, int zIn) {
            super(xIn, yIn, zIn);
        }
    }

    private boolean canPlaceCrystal(final BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB((Class) Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB((Class) Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int) r) : cy; y < (sphere ? (cy + r) : ((float) (cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(final EntityLivingBase entity, float damage, final Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(Objects.requireNonNull(Potion.getPotionById(11)))) {
                damage -= damage / 4.0f;
            }
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(final float damage) {
        final int diff = mc.world.getDifficulty().getDifficultyId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static boolean canBlockBeSeen(final BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (announcer) {
            Logger.printMessage("CrystalAura Enabled!", true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        dmg = null;
        render = null;
        if (announcer) {
            Logger.printMessage("CrystalAura Disabled!", true);
        }
    }
}
