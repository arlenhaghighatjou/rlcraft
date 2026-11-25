package xzeroair.trinkets.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import xzeroair.trinkets.items.base.TrinketRaceBase;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.compat.baubles.BaublesHelper;
import xzeroair.trinkets.util.config.trinkets.shared.TransformationRingConfig;

public class BaubleRaceBase extends TrinketRaceBase implements IBauble {
   public BaubleRaceBase(String name, EntityRace race, TransformationRingConfig config) {
      super(name, race, config);
   }

   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaublesHelper.getBaubleType(this.serverConfig.compat.baubles.bauble_type);
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
