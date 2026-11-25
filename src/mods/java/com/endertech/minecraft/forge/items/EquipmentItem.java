package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.UnitId;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EquipmentItem extends DamageableItem {
   private final EntityEquipmentSlot equipmentSlot;
   @Nullable
   private final SoundEvent equipSound;

   public EquipmentItem(ForgeMod mod, UnitConfig config, String name, CreativeTabs tab, int durability, UnitId repairItem, EntityEquipmentSlot equipSlot, @Nullable SoundEvent equipSound) {
      super(mod, config, name, tab, durability, repairItem);
      this.equipmentSlot = equipSlot;
      this.equipSound = equipSound;
   }

   public EntityEquipmentSlot getEquipmentSlot() {
      return this.equipmentSlot;
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return this.equipSound;
   }

   public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
      return armorType == this.getEquipmentSlot();
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack heldStack = player.getHeldItem(hand);
      ItemStack headSlot = player.getItemStackFromSlot(this.getEquipmentSlot());
      if (headSlot.isEmpty()) {
         player.setItemStackToSlot(this.getEquipmentSlot(), heldStack.copy());
         heldStack.setCount(0);
         if (this.getEquipSound() != null) {
            player.playSound(this.getEquipSound(), 1.0F, 1.0F);
         }

         return new ActionResult(EnumActionResult.SUCCESS, heldStack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, heldStack);
      }
   }
}
