package com.teamacronymcoders.base.recipesystem;

import com.teamacronymcoders.base.recipesystem.condition.ICondition;
import com.teamacronymcoders.base.recipesystem.input.IInput;
import com.teamacronymcoders.base.recipesystem.output.IOutput;
import com.teamacronymcoders.base.recipesystem.source.IRecipeSource;
import com.teamacronymcoders.base.recipesystem.type.RecipeType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Recipe {
   public final ResourceLocation name;
   public final int priority;
   public final RecipeType type;
   public final IRecipeSource source;
   private final List<IInput> inputs;
   private final List<IOutput> outputs;
   private final List<ICondition> conditions;

   public Recipe(ResourceLocation name, int priority, IRecipeSource source, RecipeType type, List<IInput> inputs, List<IOutput> outputs, List<ICondition> conditions) {
      this.name = name;
      this.priority = priority;
      this.source = source;
      this.type = type;
      this.inputs = inputs;
      this.outputs = outputs;
      this.conditions = conditions;
   }

   public boolean matches(RecipeContainer recipeContainer, EntityPlayer entityPlayer) {
      return this.inputs.parallelStream().allMatch((input) -> {
         return input.isMatched(recipeContainer);
      }) && this.conditions.parallelStream().allMatch((condition) -> {
         return condition.isMet(recipeContainer, entityPlayer);
      });
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return this.outputs.parallelStream().allMatch((output) -> {
         return output.canOutput(recipeContainer);
      });
   }

   public void doOutput(RecipeContainer recipeContainer) {
      this.outputs.forEach((output) -> {
         output.output(recipeContainer);
      });
   }

   public void consumeInput(RecipeContainer recipeContainer) {
      this.inputs.parallelStream().forEach((input) -> {
         input.consume(recipeContainer);
      });
   }
}
