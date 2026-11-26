package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class launcherItem extends Item {
   public launcherItem() {
      this.maxStackSize = 1;
      this.setFull3D();
      this.setUnlocalizedName("launcheritem");
      this.setMaxDamage(500);
      this.setCreativeTab(grapplemod.tabGrapplemod);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
      if (worldIn.isRemote) {
         grapplemod.proxy.launchplayer(player);
      }

   }

   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos blockpos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack stack = playerIn.getHeldItem(hand);
      this.dorightclick(stack, worldIn, playerIn);
      return EnumActionResult.SUCCESS;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer entityLiving, EnumHand hand) {
      ItemStack stack = entityLiving.getHeldItem(hand);
      this.dorightclick(stack, worldIn, entityLiving);
      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.NONE;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
      list.add("Launches player");
      list.add("");
      list.add("Use crosshairs to aim");
      list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Launch player");
   }
}
