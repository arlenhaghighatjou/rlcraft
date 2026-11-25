package com.ferreusveritas.dynamictrees.api.treedata;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IDropCreator {
   ResourceLocation getName();

   List<ItemStack> getHarvestDrop(World var1, Species var2, BlockPos var3, EaglercraftRandom var4, List<ItemStack> var5, int var6, int var7);

   List<ItemStack> getVoluntaryDrop(World var1, Species var2, BlockPos var3, EaglercraftRandom var4, List<ItemStack> var5, int var6);

   List<ItemStack> getLeavesDrop(IBlockAccess var1, Species var2, BlockPos var3, EaglercraftRandom var4, List<ItemStack> var5, int var6);

   List<ItemStack> getLogsDrop(World var1, Species var2, BlockPos var3, EaglercraftRandom var4, List<ItemStack> var5, float var6);
}
