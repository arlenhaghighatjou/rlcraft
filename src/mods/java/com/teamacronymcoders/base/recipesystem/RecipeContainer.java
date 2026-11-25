package com.teamacronymcoders.base.recipesystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class RecipeContainer implements ICapabilityProvider {
   private final ICapabilityProvider capabilityProvider;
   private final Object recipeHolder;
   private final World world;
   private final BlockPos pos;

   public RecipeContainer(Object recipeHolder, ICapabilityProvider capabilityProvider, World world, BlockPos pos) {
      this.capabilityProvider = capabilityProvider;
      this.recipeHolder = recipeHolder;
      this.world = world;
      this.pos = pos;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return this.capabilityProvider.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)this.capabilityProvider.getCapability(capability, facing);
   }

   public Object getRecipeHolder() {
      return this.recipeHolder;
   }

   public World getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
