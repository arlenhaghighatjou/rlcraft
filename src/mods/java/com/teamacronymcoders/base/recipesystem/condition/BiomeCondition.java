package com.teamacronymcoders.base.recipesystem.condition;

import com.google.gson.annotations.JsonAdapter;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocation.Serializer;

public class BiomeCondition implements ICondition {
   @JsonAdapter(
      value = Serializer.class,
      nullSafe = false
   )
   private final ResourceLocation biome;
   private final boolean inBiome;

   public BiomeCondition(ResourceLocation biome, boolean inBiome) {
      this.biome = biome;
      this.inBiome = inBiome;
   }

   public boolean isMet(RecipeContainer recipeContainer, @Nullable EntityPlayer entityPlayer) {
      return recipeContainer.getWorld().isBlockLoaded(recipeContainer.getPos()) && this.biome.equals(recipeContainer.getWorld().getBiome(recipeContainer.getPos()).getRegistryName()) == this.inBiome;
   }
}
