package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import java.util.List;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockStairsCustom extends BlockStairs {
   public BlockStairsCustom(BlockBase block) {
      super(block.getDefaultState());
      this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + "_stairs"));
      this.setUnlocalizedName(block.blockName + "_stairs");
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "\u00a7a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }
}
