package xzeroair.trinkets.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import xzeroair.trinkets.items.trinkets.TrinketRibbonBow;

public class BaubleRibbonBow extends TrinketRibbonBow implements IBauble {
   public BaubleRibbonBow(String name) {
      super(name);
   }

   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.HEAD;
   }

   public boolean canEquip(ItemStack stack, EntityLivingBase player) {
      return super.canEquipAccessory(stack, player);
   }

   public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
      return super.canUnequipAccessory(stack, player);
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      super.onAccessoryEquipped(stack, player);
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
      super.onAccessoryUnequipped(stack, player);
   }
}
