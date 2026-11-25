package de.Whitedraco.switchbow.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

public class EntityArrowLauncher extends EntityLivingBase {
   public EntityArrowLauncher(World worldIn) {
      super(worldIn);
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return null;
   }

   public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
      return null;
   }

   public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
   }

   public EnumHandSide getPrimaryHand() {
      return null;
   }
}
