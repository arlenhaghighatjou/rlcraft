package com.jamieswhiteshirt.trumpetskeleton.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTrumpet extends Item {
   public ItemTrumpet() {
      this.maxStackSize = 1;
      this.setMaxDamage(200);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 55;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }
}
