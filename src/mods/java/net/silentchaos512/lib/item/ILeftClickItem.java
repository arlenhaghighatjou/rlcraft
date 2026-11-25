package net.silentchaos512.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ILeftClickItem {
   default ActionResult<ItemStack> onItemLeftClickSL(World world, EntityPlayer player, EnumHand hand) {
      return new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
   }

   default ActionResult<ItemStack> onItemLeftClickBlockSL(World world, EntityPlayer player, EnumHand hand) {
      return this.onItemLeftClickSL(world, player, hand);
   }
}
