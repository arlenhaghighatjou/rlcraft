package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityThrowableAntiInfestedBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemThrowable extends Item {
   public ItemThrowable(String name, int maxStack) {
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.maxStackSize = maxStack;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!playerIn.capabilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         EntityThrowableAntiInfestedBlock entityegg = new EntityThrowableAntiInfestedBlock(worldIn, playerIn);
         entityegg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 0.5F, 5.0F);
         CooldownTracker track = playerIn.getCooldownTracker();
         track.setCooldown(itemstack.getItem(), 20);
         worldIn.spawnEntity(entityegg);
      }

      playerIn.addStat(StatList.getObjectUseStats(this));
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}
