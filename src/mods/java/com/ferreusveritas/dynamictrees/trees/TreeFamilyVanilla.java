package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class TreeFamilyVanilla extends TreeFamily {
   public final EnumType woodType;

   public TreeFamilyVanilla(EnumType wood) {
      super(new ResourceLocation("dynamictrees", wood.getName().replace("_", "")));
      this.woodType = wood;
      this.getCommonLeaves().setTree(this);
      boolean isOld = wood.getMetadata() < 4;
      this.setPrimitiveLog((isOld ? Blocks.LOG : Blocks.LOG2).getDefaultState().withProperty(isOld ? BlockOldLog.VARIANT : BlockNewLog.VARIANT, wood));
      this.getCommonSpecies().generateSeed();
   }

   public ILeavesProperties getCommonLeaves() {
      return (ILeavesProperties)ModBlocks.leaves.get(this.getName().getResourcePath());
   }
}
