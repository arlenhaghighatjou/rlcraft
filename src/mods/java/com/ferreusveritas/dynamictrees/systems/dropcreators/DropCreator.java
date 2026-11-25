package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DropCreator implements IDropCreator {
   ResourceLocation name;

   public DropCreator(ResourceLocation name) {
      this.name = name;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
      return dropList;
   }

   public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, Random random, List<ItemStack> dropList, int soilLife) {
      return dropList;
   }

   public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
      return dropList;
   }

   public List<ItemStack> getLogsDrop(World world, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, float volume) {
      return dropList;
   }
}
