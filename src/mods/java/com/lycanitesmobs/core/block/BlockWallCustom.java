package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import java.util.List;
import net.minecraft.block.BlockWall;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWallCustom extends BlockWall {
   public BlockWallCustom(BlockBase block) {
      super(block);
      this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + "_wall"));
      this.setUnlocalizedName(block.blockName + "_wall");
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      items.add(new ItemStack(this, 1));
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "§a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }
}
