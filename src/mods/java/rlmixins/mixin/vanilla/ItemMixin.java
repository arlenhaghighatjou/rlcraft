package rlmixins.mixin.vanilla;

import com.google.common.collect.Multimap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlmixins.wrapper.SMEWrapper;

@Mixin({Item.class})
public abstract class ItemMixin {
   private static final EaglercraftUUID SS_UUID = EaglercraftUUID.fromString("fc1c8dca-9411-4a4e-97a4-90e66c883a77");
   private static final EaglercraftUUID HW_UUID = EaglercraftUUID.fromString("e2765897-134f-4c14-a535-29c3ae5c7a21");

   public ItemMixin() {
   }

   @Inject(
      method = {"getAttributeModifiers"},
      at = {@At("RETURN")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_vanillaItem_getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack, CallbackInfoReturnable<Multimap<String, AttributeModifier>> cir) {
      if (!stack.isEmpty() && slot == EntityEquipmentSlot.MAINHAND) {
         Multimap<String, AttributeModifier> map = (Multimap)cir.getReturnValue();
         int ssLevel = EnchantmentHelper.getEnchantmentLevel(SMEWrapper.getSwifterSlashes(), stack);
         int hwLevel = EnchantmentHelper.getEnchantmentLevel(SMEWrapper.getHeavyWeight(), stack);
         if (ssLevel > 0) {
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SS_UUID, "swifterSlashes", 0.2D * (double)ssLevel, 1));
         }

         if (hwLevel > 0) {
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(HW_UUID, "heavyWeight", ((double)hwLevel * 0.1D + 0.2D) * -1.0D, 1));
         }

         cir.setReturnValue(map);
      }
   }
}
