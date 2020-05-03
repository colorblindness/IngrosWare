package best.reich.ingros.util.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import me.xenforu.kelo.traits.Minecraftable;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.util.EnumMap;
import java.util.Map;

public class WeaponUtil implements Minecraftable {
    public static boolean canExecute(final EntityLivingBase e) {
        return (getItemDamage(mc.player.getHeldItemMainhand()) * mc.player.getCooledAttackStrength(0)) * (1.0 - (e instanceof EntityPlayer ? getDamageReduced((EntityPlayer) e) : 0.0)) >= e.getHealth();
    }

    public static float getItemDamage(final ItemStack itemStack) {
        if (itemStack == null) return 1.0F;
        final Multimap<String, AttributeModifier> multimap = itemStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        if (!multimap.isEmpty()) {
            for (AttributeModifier attributeModifier : multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                final double damage = attributeModifier.getAmount();
                return damage > 1.0D ? 1.0F + (float) damage : 1.0F;
            }
        }
        return 1.0F;
    }

    public static double getDamageReduced(final EntityPlayer player) {
        final EnumMap<EntityEquipmentSlot, ItemStack> armorItems = Maps.newEnumMap(EntityEquipmentSlot.class);
        armorItems.put(EntityEquipmentSlot.HEAD, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        armorItems.put(EntityEquipmentSlot.CHEST, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        armorItems.put(EntityEquipmentSlot.LEGS, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        armorItems.put(EntityEquipmentSlot.FEET, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        double reduction = 0.0;
        for (Map.Entry<EntityEquipmentSlot, ItemStack> item : armorItems.entrySet()) {
            if (item.getValue() != null) {
                final Multimap<String, AttributeModifier> multimap = item.getValue().getAttributeModifiers(item.getKey());
                if (!multimap.isEmpty()) {
                    for (AttributeModifier attributeModifier : multimap.get(SharedMonsterAttributes.ARMOR.getName())) {
                        reduction += attributeModifier.getAmount() + EnchantmentHelper.getEnchantmentModifierDamage(armorItems.values(), DamageSource.causePlayerDamage(player));
                    }
                }
            }
        }
        return (reduction * 4) / 100.0;
    }

}
