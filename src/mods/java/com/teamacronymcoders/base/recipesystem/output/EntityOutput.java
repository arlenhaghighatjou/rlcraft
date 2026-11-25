package com.teamacronymcoders.base.recipesystem.output;

import com.google.gson.annotations.JsonAdapter;
import com.teamacronymcoders.base.json.deserializer.EntityFactoryDeserializer;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityOutput implements IOutput {
   @JsonAdapter(EntityFactoryDeserializer.class)
   private final Function<World, ? extends Entity> entity;

   public EntityOutput(Function<World, ? extends Entity> entity) {
      this.entity = entity;
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return true;
   }

   public void output(RecipeContainer recipeContainer) {
      if (!recipeContainer.getWorld().isRemote) {
         Entity entity = (Entity)this.entity.apply(recipeContainer.getWorld());
         BlockPos blockPos = recipeContainer.getPos();
         entity.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
         recipeContainer.getWorld().spawnEntity(entity);
      }

   }
}
