package com.dhanantry.scapeandrunparasites.item;

import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLure extends ItemBase {
   int sound = 0;

   public ItemLure(String name, int maxStack, byte version) {
      super(name, maxStack, version);
      this.version = version;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (!worldIn.isRemote) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.posX + 1.0D, playerIn.posY + 1.0D, playerIn.posZ + 1.0D)).grow(4.0D);

         for(EntityLivingBase mob : playerIn.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob == playerIn) {
               ;
            }
         }
      }

      return super.onItemRightClick(worldIn, playerIn, handIn);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.AQUA + I18n.format("tootip.srparasites.lurecomp." + this.version, new Object[0]));
   }
}
