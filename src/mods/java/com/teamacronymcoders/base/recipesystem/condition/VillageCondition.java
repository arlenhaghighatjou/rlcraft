package com.teamacronymcoders.base.recipesystem.condition;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public class VillageCondition implements ICondition {
   private final boolean inVillage;

   public VillageCondition(boolean inVillage) {
      this.inVillage = inVillage;
   }

   public boolean isMet(RecipeContainer recipeContainer, @Nullable EntityPlayer entityPlayer) {
      return this.locatedInVillage(recipeContainer) == this.inVillage;
   }

   private boolean locatedInVillage(RecipeContainer recipeContainer) {
      return recipeContainer.getWorld().getVillageCollection().getNearestVillage(recipeContainer.getPos(), 32) != null;
   }
}
