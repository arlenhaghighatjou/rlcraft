package com.chaosthedude.realistictorches.items;

import com.chaosthedude.realistictorches.config.ConfigHandler;
import net.minecraft.util.text.TextFormatting;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMatchbox extends Item {
   public static final String NAME = "matchbox";

   public ItemMatchbox() {
      this.setUnlocalizedName("realistictorches.matchbox");
      this.setMaxStackSize(1);
      if (ConfigHandler.matchboxDurability > 0) {
         this.setMaxDamage(ConfigHandler.matchboxDurability - 1);
      }

      this.setCreativeTab(CreativeTabs.TOOLS);
      this.setNoRepair();
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!ConfigHandler.matchboxCreatesFire) {
         return EnumActionResult.FAIL;
      } else {
         pos = pos.offset(facing);
         if (!player.canPlayerEdit(pos, facing, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
         } else {
            if (world.isAirBlock(pos)) {
               world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
               world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
            }

            player.getHeldItem(hand).damageItem(1, player);
            return EnumActionResult.SUCCESS;
         }
      }
   }

   public boolean hasContainerItem(ItemStack stack) {
      return true;
   }

   public ItemStack getContainerItem(ItemStack stack) {
      if (ConfigHandler.matchboxDurability <= 0) {
         return new ItemStack(this);
      } else if (stack.getItemDamage() + 1 > stack.getMaxDamage()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack newStack = new ItemStack(this);
         newStack.setItemDamage(stack.getItemDamage() + 1);
         return newStack;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (GuiScreen.isShiftKeyDown()) {
         tooltip.add(TextFormatting.ITALIC + "It's lit");
      }

   }
}
