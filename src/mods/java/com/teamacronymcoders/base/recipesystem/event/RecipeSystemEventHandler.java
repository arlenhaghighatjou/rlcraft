package com.teamacronymcoders.base.recipesystem.event;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.event.BaseRegistryEvent;
import com.teamacronymcoders.base.recipesystem.RecipeSystem;
import com.teamacronymcoders.base.recipesystem.condition.BiomeCondition;
import com.teamacronymcoders.base.recipesystem.condition.ICondition;
import com.teamacronymcoders.base.recipesystem.condition.VillageCondition;
import com.teamacronymcoders.base.recipesystem.input.BlockStateInput;
import com.teamacronymcoders.base.recipesystem.input.EntityInput;
import com.teamacronymcoders.base.recipesystem.input.ForgeEnergyInput;
import com.teamacronymcoders.base.recipesystem.input.IInput;
import com.teamacronymcoders.base.recipesystem.loader.AssetJsonRecipeLoader;
import com.teamacronymcoders.base.recipesystem.loader.ILoader;
import com.teamacronymcoders.base.recipesystem.output.BlockStateOutput;
import com.teamacronymcoders.base.recipesystem.output.CommandOutput;
import com.teamacronymcoders.base.recipesystem.output.EntityOutput;
import com.teamacronymcoders.base.recipesystem.output.ExplosionOutput;
import com.teamacronymcoders.base.recipesystem.output.IOutput;
import com.teamacronymcoders.base.recipesystem.output.json.OneOfOutputFactory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(
   modid = "base"
)
public class RecipeSystemEventHandler {
   public RecipeSystemEventHandler() {
   }

   @SubscribeEvent
   public static void registerRecipes(RegistryEvent<IRecipe> recipeRegistryEvent) {
      RecipeSystem.loadRecipeTypes();
      RecipeSystem.loadRecipes(false);
   }

   @SubscribeEvent
   public static void registerConditions(RegisterRecipeFactoriesEvent<ICondition> conditionEvent) {
      conditionEvent.register(new ResourceLocation("base", "biome"), BiomeCondition.class);
      conditionEvent.register(new ResourceLocation("base", "village"), VillageCondition.class);
   }

   @SubscribeEvent
   public static void registerInput(RegisterRecipeFactoriesEvent<IInput> inputEvent) {
      inputEvent.register(new ResourceLocation("base", "blockstate"), BlockStateInput.class);
      inputEvent.register(new ResourceLocation("base", "forge_energy"), ForgeEnergyInput.class);
      inputEvent.register(new ResourceLocation("base", "entity"), EntityInput.class);
   }

   @SubscribeEvent
   public static void registerOutput(RegisterRecipeFactoriesEvent<IOutput> outputEvent) {
      outputEvent.register(new ResourceLocation("base", "command"), CommandOutput.class);
      outputEvent.register(new ResourceLocation("base", "blockstate"), BlockStateOutput.class);
      outputEvent.register(new ResourceLocation("base", "one_of"), new OneOfOutputFactory());
      outputEvent.register(new ResourceLocation("base", "entity"), EntityOutput.class);
      outputEvent.register(new ResourceLocation("base", "explosion"), ExplosionOutput.class);
   }

   @SubscribeEvent
   public static void registerLoader(BaseRegistryEvent<ILoader> loaderRegistryEvent) {
      loaderRegistryEvent.register(new ResourceLocation("base", "asset"), AssetJsonRecipeLoader.getInstance());
      Base.proxy.registerServerLoader(loaderRegistryEvent);
   }
}
