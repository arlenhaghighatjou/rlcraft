package com.endertech.minecraft.forge.items;

import com.endertech.common.CommonTime;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.math.GameTime;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class ConsumableItem extends ForgeItem {
   private final int maxUseDuration;

   public ConsumableItem(ForgeMod mod, UnitConfig config, CreativeTabs tab, CommonTime.Interval consumeDuration) {
      super(mod, config, tab);
      consumeDuration = ForgeConfig.getTimeInSeconds(config, this.getUnitCategory(), "consumeDuration", consumeDuration, "Defines time required for item to be consumed.");
      this.maxUseDuration = (int)GameTime.inServerTicks(consumeDuration);
   }

   public abstract EnumAction getItemUseAction(ItemStack var1);

   public int getMaxItemUseDuration(ItemStack stack) {
      return this.maxUseDuration;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase living) {
      EntityPlayer player = living instanceof EntityPlayer ? (EntityPlayer)living : null;
      if (player == null || !player.capabilities.isCreativeMode) {
         stack.shrink(1);
      }

      if (player instanceof EntityPlayerMP) {
         CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player, stack);
      }

      return super.onItemUseFinish(stack, world, living);
   }
}
