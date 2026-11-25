package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockFenceCustom extends BlockFence {
   public BlockFenceCustom(BlockBase block) {
      super(block.getDefaultState().getMaterial(), MapColor.STONE);
      this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + "_fence"));
      this.setUnlocalizedName(block.blockName + "_fence");
      block.copyAttributesTo(this);
      this.setSoundType(block.getSoundType());
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
