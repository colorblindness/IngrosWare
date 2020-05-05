package best.reich.ingros.module.toggles;

import best.reich.ingros.events.render.Render3DEvent;
import best.reich.ingros.util.render.RenderUtil;
import me.xenforu.kelo.module.ModuleCategory;
import me.xenforu.kelo.module.annotation.ModuleManifest;
import me.xenforu.kelo.module.type.ToggleableModule;
import me.xenforu.kelo.setting.annotation.Setting;
import me.xenforu.kelo.util.math.MathUtil;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@ModuleManifest(label = "Trajectories", category = ModuleCategory.RENDER, color = 0xAE85DE)
public class Trajectories extends ToggleableModule {
    private final Queue<Vec3d> flightPoint = new ConcurrentLinkedQueue<>();
    @Setting("Color")
    public Color color = new Color(200, 33, 168);

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;
        ThrowableType throwingType = this.getTypeFromCurrentItem(mc.player);

        if (throwingType == ThrowableType.NONE) {
            return;
        }

        FlightPath flightPath = new FlightPath(mc.player, throwingType);

        while (!flightPath.isCollided()) {
            flightPath.onUpdate();

            flightPoint.offer(new Vec3d(flightPath.position.x - mc.getRenderManager().viewerPosX,
                    flightPath.position.y - mc.getRenderManager().viewerPosY,
                    flightPath.position.z - mc.getRenderManager().viewerPosZ));
        }

        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.disableDepth();
        GL11.glEnable(GL32.GL_DEPTH_CLAMP);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        while (!flightPoint.isEmpty()) {
            bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            Vec3d head = flightPoint.poll();
            bufferbuilder.pos(head.x, head.y, head.z).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f).endVertex();

            if (flightPoint.peek() != null) {
                Vec3d point = flightPoint.peek();
                bufferbuilder.pos(point.x, point.y, point.z).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f).endVertex();
            }

            tessellator.draw();
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableDepth();
        GL11.glDisable(GL32.GL_DEPTH_CLAMP);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        mc.gameSettings.viewBobbing = bobbing;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        if (flightPath.collided) {
            final RayTraceResult hit = flightPath.target;
            AxisAlignedBB bb = null;

            if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                final BlockPos blockpos = hit.getBlockPos();
                final IBlockState iblockstate = mc.world.getBlockState(blockpos);

                if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                    final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    bb = iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z);
                }
            } else if (hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit != null) {
                final AxisAlignedBB entityBB = hit.entityHit.getEntityBoundingBox();
                if (entityBB != null) {
                    bb = new AxisAlignedBB(entityBB.minX - mc.getRenderManager().viewerPosX, entityBB.minY - mc.getRenderManager().viewerPosY, entityBB.minZ - mc.getRenderManager().viewerPosZ, entityBB.maxX - mc.getRenderManager().viewerPosX, entityBB.maxY - mc.getRenderManager().viewerPosY, entityBB.maxZ - mc.getRenderManager().viewerPosZ);
                }
            }

            if (bb != null) {
                RenderUtil.drawESPOutline(bb, color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(),1);
            }
        }
    }

    private ThrowableType getTypeFromCurrentItem(EntityPlayerSP player) {
        // Check if we're holding an item first
        if (player.getHeldItemMainhand() == null) {
            return ThrowableType.NONE;
        }

        final ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
        // Check what type of item this is
        switch (Item.getIdFromItem(itemStack.getItem())) {
            case 261: // ItemBow
                if (player.isHandActive())
                    return ThrowableType.ARROW;
                break;
            case 346: // ItemFishingRod
                return ThrowableType.FISHING_ROD;
            case 438: //splash potion
            case 441: //splash potion linger
                return ThrowableType.POTION;
            case 384: // ItemExpBottle
                return ThrowableType.EXPERIENCE;
            case 332: // ItemSnowball
            case 344: // ItemEgg
            case 368: // ItemEnderPearl
                return ThrowableType.NORMAL;
            default:
                break;
        }

        return ThrowableType.NONE;
    }

    enum ThrowableType {
        /**
         * Represents a non-throwable object.
         */
        NONE(0.0f, 0.0f),

        /**
         * Arrows fired from a bow.
         */
        ARROW(1.5f, 0.05f),

        /**
         * Splash potion entities
         */
        POTION(0.5f, 0.05f),

        /**
         * Experience bottles.
         */
        EXPERIENCE(0.7F, 0.07f),

        /**
         * The fishhook entity with a fishing rod.
         */
        FISHING_ROD(1.5f, 0.04f),

        /**
         * Any throwable entity that doesn't have unique
         * world velocity/gravity constants.
         */
        NORMAL(1.5f, 0.03f);

        private final float velocity;
        private final float gravity;

        ThrowableType(float velocity, float gravity) {
            this.velocity = velocity;
            this.gravity = gravity;
        }

        /**
         * The initial velocity of the entity.
         *
         * @return entity velocity
         */

        public float getVelocity() {
            return velocity;
        }

        /**
         * The constant gravity applied to the entity.
         *
         * @return constant world gravity
         */
        public float getGravity() {
            return gravity;
        }
    }

    /**
     * A class used to mimic the flight of an entity.  Actual
     * implementation resides in multiple classes but the parent of all
     * of them is {@link net.minecraft.entity.projectile.EntityThrowable}
     */
    final class FlightPath {
        private EntityPlayerSP shooter;
        private Vec3d position;
        private Vec3d motion;
        private float yaw;
        private float pitch;
        private AxisAlignedBB boundingBox;
        private boolean collided;
        private RayTraceResult target;
        private ThrowableType throwableType;

        FlightPath(EntityPlayerSP player, ThrowableType throwableType) {
            this.shooter = player;
            this.throwableType = throwableType;

            // Set the starting angles of the entity
            this.setLocationAndAngles(this.shooter.posX, this.shooter.posY + this.shooter.getEyeHeight(), this.shooter.posZ,
                    this.shooter.rotationYaw, this.shooter.rotationPitch);

            Vec3d startingOffset = new Vec3d(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F, 0.1d,
                    MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);

            this.position = this.position.subtract(startingOffset);
            // Update the entity's bounding box
            this.setPosition(this.position);

            // Set the entity's motion based on the shooter's rotations
            this.motion = new Vec3d(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI),
                    -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI),
                    MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));

            this.setThrowableHeading(this.motion, this.getInitialVelocity());
        }


        public void onUpdate() {
            Vec3d prediction = this.position.add(this.motion);
            RayTraceResult blockCollision = this.shooter.getEntityWorld().rayTraceBlocks(this.position, prediction,
                    this.throwableType == ThrowableType.FISHING_ROD, !this.collidesWithNoBoundingBox(), false);
            if (blockCollision != null) {
                prediction = blockCollision.hitVec;
            }
            this.onCollideWithEntity(prediction, blockCollision);
            if (this.target != null) {
                this.collided = true;
                this.setPosition(this.target.hitVec);
                return;
            }
            if (this.position.y <= 0.0d) {
                this.collided = true;
                return;
            }
            this.position = this.position.add(this.motion);
            float motionModifier = 0.99F;
            if (this.shooter.getEntityWorld().isMaterialInBB(this.boundingBox, Material.WATER)) {
                motionModifier = this.throwableType == ThrowableType.ARROW ? 0.6F : 0.8F;
            }
            if (this.throwableType == ThrowableType.FISHING_ROD) {
                motionModifier = 0.92f;
            }
            this.motion = MathUtil.mult(this.motion, motionModifier);
            this.motion = this.motion.subtract(0.0d, this.getGravityVelocity(), 0.0d);
            // Update the position and bounding box
            this.setPosition(this.position);
        }

        private boolean collidesWithNoBoundingBox() {
            switch (this.throwableType) {
                case FISHING_ROD:
                case NORMAL:
                    return true;
                default:
                    return false;
            }
        }

        private void onCollideWithEntity(Vec3d prediction, RayTraceResult blockCollision) {
            Entity collidingEntity = null;
            RayTraceResult collidingPosition = null;

            double currentDistance = 0.0d;
            // Get all possible collision entities disregarding the local player
            List<Entity> collisionEntities = mc.world.getEntitiesWithinAABBExcludingEntity(this.shooter, this.boundingBox.expand(this.motion.x, this.motion.y, this.motion.z).grow(1.0D, 1.0D, 1.0D));

            // Loop through every loaded entity in the world
            for (Entity entity : collisionEntities) {
                // Check if we can collide with the entity or it's ourself
                if (!entity.canBeCollidedWith()) {
                    continue;
                }

                // Check if we collide with our bounding box
                float collisionSize = entity.getCollisionBorderSize();
                AxisAlignedBB expandedBox = entity.getEntityBoundingBox().expand(collisionSize, collisionSize, collisionSize);
                RayTraceResult objectPosition = expandedBox.calculateIntercept(this.position, prediction);

                // Check if we have a collision
                if (objectPosition != null) {
                    double distanceTo = this.position.distanceTo(objectPosition.hitVec);

                    // Check if we've gotten a closer entity
                    if (distanceTo < currentDistance || currentDistance == 0.0D) {
                        collidingEntity = entity;
                        collidingPosition = objectPosition;
                        currentDistance = distanceTo;
                    }
                }
            }

            // Check if we had an entity
            if (collidingEntity != null) {
                // Set our target to the result
                this.target = new RayTraceResult(collidingEntity, collidingPosition.hitVec);
            } else {
                // Fallback to the block collision
                this.target = blockCollision;
            }
        }

        /**
         * Return the initial velocity of the entity at it's exact starting
         * moment in flight.
         *
         * @return entity velocity in flight
         */
        private float getInitialVelocity() {
            switch (this.throwableType) {
                // Arrows use the current use duration as a velocity multplier
                case ARROW:
                    // Check how long we've been using the bow
                    int useDuration = this.shooter.getHeldItem(EnumHand.MAIN_HAND).getItem().getMaxItemUseDuration(this.shooter.getHeldItem(EnumHand.MAIN_HAND)) - this.shooter.getItemInUseCount();
                    float velocity = (float) useDuration / 20.0F;
                    velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
                    if (velocity > 1.0F) {
                        velocity = 1.0F;
                    }

                    // When the arrow is spawned inside of ItemBow, they multiply it by 2
                    return (velocity * 2.0f) * throwableType.getVelocity();
                default:
                    return throwableType.getVelocity();
            }
        }
        
        private float getGravityVelocity() {
            return throwableType.getGravity();
        }

        private void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
            this.position = new Vec3d(x, y, z);
            this.yaw = yaw;
            this.pitch = pitch;
        }

        private void setPosition(Vec3d position) {
            this.position = new Vec3d(position.x, position.y, position.z);
            // Usually this is this.width / 2.0f but throwables change
            double entitySize = (this.throwableType == ThrowableType.ARROW ? 0.5d : 0.25d) / 2.0d;
            // Update the path's current bounding box
            this.boundingBox = new AxisAlignedBB(position.x - entitySize,
                    position.y - entitySize,
                    position.z - entitySize,
                    position.x + entitySize,
                    position.y + entitySize,
                    position.z + entitySize);
        }
        
        private void setThrowableHeading(Vec3d motion, float velocity) {
            // Divide the current motion by the length of the vector
            this.motion = MathUtil.div(motion, (float) motion.lengthVector());
            // Multiply by the velocity
            this.motion = MathUtil.mult(this.motion, velocity);
        }
        
        public boolean isCollided() {
            return collided;
        }
        
        public RayTraceResult getCollidingTarget() {
            return target;
        }
    }
}
