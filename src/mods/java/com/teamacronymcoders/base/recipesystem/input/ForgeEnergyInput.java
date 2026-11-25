package com.teamacronymcoders.base.recipesystem.input;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import com.teamacronymcoders.base.util.CapUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraftforge.energy.CapabilityEnergy;

public class ForgeEnergyInput implements IInput {
   private final int amountRequired;

   public ForgeEnergyInput(int amountRequired) {
      this.amountRequired = amountRequired;
   }

   public boolean isMatched(RecipeContainer recipeContainer) {
      return ((Boolean)CapUtils.getOptional(recipeContainer, CapabilityEnergy.ENERGY).map((energyStorage) -> {
         return energyStorage.extractEnergy(this.amountRequired, true) == this.amountRequired;
      }).orElse(Boolean.valueOf(false))).booleanValue();
   }

   public void consume(RecipeContainer recipeContainer) {
      CapUtils.getOptional(recipeContainer, CapabilityEnergy.ENERGY).ifPresent((energyStorage) -> {
         energyStorage.extractEnergy(this.amountRequired, false);
      });
   }
}
