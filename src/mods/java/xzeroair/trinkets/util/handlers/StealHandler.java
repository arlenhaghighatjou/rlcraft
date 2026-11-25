package xzeroair.trinkets.util.handlers;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class StealHandler {
   private EaglercraftRandom rand = new EaglercraftRandom();
   private EntityLivingBase player;
   int cooldown = 0;
   int counter = 0;

   public StealHandler(EntityLivingBase entity) {
      this.player = entity;
   }

   public void onUpdate() {
   }

   public void StealWeapon(EntityLivingBase target) {
      boolean success = false;

      for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
         ItemStack stack = target.getItemStackFromSlot(slot);
         if (slot.getSlotType() == Type.HAND && !stack.isEmpty()) {
            ;
         }
      }

   }

   public void StealArmor(EntityLivingBase target) {
      boolean success = false;
      int counter = -1;
      int counter2 = -1;

      for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
         ItemStack stack = target.getItemStackFromSlot(slot);
         if (slot.getSlotType() == Type.ARMOR && !stack.isEmpty()) {
            success = true;
            if (this.player instanceof EntityPlayer) {
               ITextComponent message = new TextComponentString(TextFormatting.BOLD + "" + TextFormatting.GREEN + "Success! You Stole: " + TextFormatting.BOLD + "" + TextFormatting.AQUA + stack.getItem().getRegistryName());
               ((EntityPlayer)this.player).sendStatusMessage(message, true);
               ((EntityPlayer)this.player).addItemStackToInventory(stack);
            }
            break;
         }
      }

      if (!success && this.player instanceof EntityPlayer) {
         ITextComponent message = new TextComponentString(TextFormatting.BOLD + "" + TextFormatting.DARK_RED + "I Failed!");
         ((EntityPlayer)this.player).sendStatusMessage(message, true);
      }

   }

   public void StealDrops(EntityLivingBase target) {
   }
}
