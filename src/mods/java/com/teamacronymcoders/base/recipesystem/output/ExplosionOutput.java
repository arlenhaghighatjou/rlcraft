package com.teamacronymcoders.base.recipesystem.output;

import com.google.gson.annotations.JsonAdapter;
import com.teamacronymcoders.base.json.deserializer.BlockPosDeserializer;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class ExplosionOutput implements IOutput {
   @JsonAdapter(BlockPosDeserializer.class)
   private final BlockPos offset;
   private final float strength;
   private final boolean damageTerrain;
   private final boolean causesFire;

   public ExplosionOutput(BlockPos offset, float strength, boolean damageTerrain, boolean causesFire) {
      this.offset = offset;
      this.strength = strength;
      this.damageTerrain = damageTerrain;
      this.causesFire = causesFire;
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return true;
   }

   public void output(RecipeContainer recipeContainer) {
      BlockPos explosionPos = this.offset == null ? recipeContainer.getPos() : recipeContainer.getPos().add(this.offset);
      recipeContainer.getWorld().newExplosion((Entity)null, (double)explosionPos.getX(), (double)explosionPos.getY(), (double)explosionPos.getZ(), this.strength, this.damageTerrain, this.causesFire);
   }
}
