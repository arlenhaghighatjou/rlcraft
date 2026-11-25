package com.teamacronymcoders.base.recipesystem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.command.CommandSubBase;
import com.teamacronymcoders.base.event.BaseRegistryEvent;
import com.teamacronymcoders.base.recipesystem.command.ReloadRecipesCommand;
import com.teamacronymcoders.base.recipesystem.loader.ILoader;
import com.teamacronymcoders.base.recipesystem.type.RecipeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraftforge.common.MinecraftForge;

public class RecipeSystem {
   private static final Map<String, RecipeType> recipeTypes = Maps.newHashMap();
   private static final Map<RecipeType, List<Recipe>> recipeLists = Maps.newHashMap();
   private static final Map<String, ILoader> loaders = Maps.newHashMap();
   private static final CommandSubBase recipeCommand = new CommandSubBase("recipes");

   public RecipeSystem() {
   }

   public static void loadRecipeTypes() {
      BaseRegistryEvent<RecipeType> recipeTypeEvent = new BaseRegistryEvent<RecipeType>(RecipeType.class);
      MinecraftForge.EVENT_BUS.post(recipeTypeEvent);
      recipeTypes.putAll(recipeTypeEvent.getEntries());
      BaseRegistryEvent<ILoader> loaderEvent = new BaseRegistryEvent<ILoader>(ILoader.class);
      MinecraftForge.EVENT_BUS.post(loaderEvent);
      loaders.putAll(loaderEvent.getEntries());
   }

   public static void loadRecipes(boolean reload) {
      loaders.values().stream().filter((loader) -> {
         return !reload || loader.getRecipeSource().canReload();
      }).map(ILoader::loadRecipes).flatMap(Collection::stream).forEach((recipe) -> {
         recipeLists.putIfAbsent(recipe.type, Lists.newArrayList());
         ((List)recipeLists.get(recipe.type)).add(recipe);
      });
      recipeTypes.values().parallelStream().forEach(RecipeSystem::handleRecipeType);
   }

   private static void handleRecipeType(RecipeType recipeType) {
      List<Recipe> recipes = getRecipesFor(recipeType);
      recipes.sort(Comparator.comparingInt((recipe) -> {
         return recipe.priority;
      }));
      recipeType.recipeHandlers.forEach((recipeHandler) -> {
         recipeHandler.reloadRecipes(recipes);
      });
   }

   public static void reloadRecipe() {
      for(List<Recipe> recipes : recipeLists.values()) {
         recipes.removeIf((recipe) -> {
            return recipe.source.canReload();
         });
      }

      loadRecipes(true);
   }

   public static RecipeType getRecipeType(String typeName) {
      return (RecipeType)recipeTypes.get(typeName);
   }

   public static List<Recipe> getRecipesFor(RecipeType recipeType) {
      return (List)recipeLists.getOrDefault(recipeType, new ArrayList());
   }

   public static void setup() {
      Base.instance.getBaseCommand().addSubcommand(recipeCommand);
      recipeCommand.addSubcommand(new ReloadRecipesCommand());
   }
}
