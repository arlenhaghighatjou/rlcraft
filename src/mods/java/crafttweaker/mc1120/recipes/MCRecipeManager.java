package crafttweaker.mc1120.recipes;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IRecipeAction;
import crafttweaker.api.recipes.IRecipeFunction;
import crafttweaker.api.recipes.IRecipeManager;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.item.MCItemStack;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.annotations.Optional;

public final class MCRecipeManager implements IRecipeManager {
   public static final List<MCRecipeManager.ActionBaseAddRecipe> recipesToAdd = new ArrayList();
   public static final List<MCRecipeManager.ActionBaseRemoveRecipes> recipesToRemove = new ArrayList();
   public static final MCRecipeManager.ActionRemoveRecipesNoIngredients actionRemoveRecipesNoIngredients = new MCRecipeManager.ActionRemoveRecipesNoIngredients();
   public static Set<Entry<ResourceLocation, IRecipe>> recipes;
   public static final List<MCRecipeBase> transformerRecipes = new ArrayList();
   public static final List<MCRecipeBase> actionRecipes = new ArrayList();
   private static final TIntSet usedHashes = new TIntHashSet();
   private static final HashSet<String> usedRecipeNames = new HashSet();

   public MCRecipeManager() {
   }

   private static boolean matchesItem(ItemStack input, IIngredient ingredient) {
      return ingredient == null ? input.isEmpty() : !input.isEmpty() && ingredient.matches(CraftTweakerMC.getIItemStackForMatching(input));
   }

   private static boolean matches(Object input, IIngredient ingredient) {
      if (input instanceof String) {
         return ingredient.contains(CraftTweakerMC.getOreDict((String)input));
      } else if (input instanceof ItemStack) {
         return matchesItem((ItemStack)input, ingredient);
      } else if (!(input instanceof Ingredient)) {
         return false;
      } else {
         ItemStack[] matchingStacks = ((Ingredient)input).getMatchingStacks();
         return matchingStacks.length > 0 && matchesItem(matchingStacks[0], ingredient);
      }
   }

   /** @deprecated */
   @Deprecated
   public static String saveToString(Object ingredient) {
      return ingredient == null ? "_" : ingredient.toString();
   }

   /** @deprecated */
   @Deprecated
   public static String saveToString(IIngredient ingredient) {
      return saveToString((Object)ingredient);
   }

   public static String cleanRecipeName(String s) {
      if (s.contains(":")) {
         CraftTweakerAPI.logWarning("Recipe name [" + s + "] may not contain a ':', replacing with '_'!");
      }

      return s.replace(":", "_");
   }

   public static void refreshRecipes() {
      recipes = ForgeRegistries.RECIPES.getEntries();
   }

   public static void cleanUpRecipeList() {
      Iterator<MCRecipeManager.ActionBaseAddRecipe> iterator = recipesToAdd.iterator();

      while(iterator.hasNext()) {
         MCRecipeManager.ActionBaseAddRecipe next = (MCRecipeManager.ActionBaseAddRecipe)iterator.next();
         MCRecipeBase recipe = next.getRecipe();
         if (recipe == null) {
            CraftTweakerAPI.logWarning("Recipe action " + next.getClass().getCanonicalName() + " had null recipe, please report this issue!");
         } else if (!ForgeRegistries.RECIPES.containsKey(recipe.getRegistryName())) {
            CraftTweakerAPI.logWarning("Recipe " + recipe.toCommandString() + " was created but not added to the Recipe Registry, check for other errors in your log!");
            iterator.remove();
         }
      }

   }

   public List<ICraftingRecipe> getRecipesFor(IIngredient ingredient) {
      List<ICraftingRecipe> results = new ArrayList();
      if (recipes == null) {
         recipes = ForgeRegistries.RECIPES.getEntries();
      }

      for(Entry<ResourceLocation, IRecipe> ent : recipes) {
         ItemStack stack = ((IRecipe)ent.getValue()).getRecipeOutput();
         if (!stack.isEmpty() && ingredient.matches(CraftTweakerMC.getIItemStackForMatching(stack))) {
            if (ent.getValue() instanceof MCRecipeBase) {
               results.add((MCRecipeBase)ent.getValue());
            } else {
               results.add(new MCRecipeWrapper((IRecipe)ent.getValue()));
            }
         }
      }

      return results;
   }

   public List<ICraftingRecipe> getAll() {
      List<ICraftingRecipe> results = new ArrayList();
      if (recipes == null) {
         recipes = ForgeRegistries.RECIPES.getEntries();
      }

      for(Entry<ResourceLocation, IRecipe> recipeEntry : recipes) {
         IRecipe recipe = (IRecipe)recipeEntry.getValue();
         if (recipe instanceof MCRecipeBase) {
            results.add((MCRecipeBase)recipe);
         } else {
            results.add(new MCRecipeWrapper(recipe));
         }
      }

      return results;
   }

   public void addShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      recipesToAdd.add(new MCRecipeManager.ActionAddShapedRecipe(output, ingredients, function, action, false, false));
   }

   public void addShaped(String name, IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      recipesToAdd.add(new MCRecipeManager.ActionAddShapedRecipe(name, output, ingredients, function, action, false, false));
   }

   public void addShapedMirrored(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      recipesToAdd.add(new MCRecipeManager.ActionAddShapedRecipe(output, ingredients, function, action, true, false));
   }

   public void addShapedMirrored(String name, IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      recipesToAdd.add(new MCRecipeManager.ActionAddShapedRecipe(name, output, ingredients, function, action, true, false));
   }

   public void addShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      if (!this.checkShapelessNulls(output, ingredients)) {
         recipesToAdd.add(new MCRecipeManager.ActionAddShapelessRecipe(output, ingredients, function, action));
      }
   }

   public void addShapeless(String name, IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      if (!this.checkShapelessNulls(output, ingredients)) {
         recipesToAdd.add(new MCRecipeManager.ActionAddShapelessRecipe(name, output, ingredients, function, action, false));
      }
   }

   public void addHiddenShapeless(String name, IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
      if (!this.checkShapelessNulls(output, ingredients)) {
         recipesToAdd.add(new MCRecipeManager.ActionAddShapelessRecipe(name, output, ingredients, function, action, true));
      }
   }

   private boolean checkShapelessNulls(IItemStack output, IIngredient[] ingredients) {
      boolean valid = output != null;

      for(IIngredient ing : ingredients) {
         if (ing == null) {
            valid = false;
            break;
         }
      }

      if (!valid) {
         CraftTweakerAPI.logError("Null not allowed in shapeless recipes! Recipe for: " + output + " not created!");
         return true;
      } else {
         return false;
      }
   }

   public void addHiddenShaped(String name, IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action, @Optional boolean mirrored) {
      recipesToAdd.add(new MCRecipeManager.ActionAddShapedRecipe(name, output, ingredients, function, action, mirrored, true));
   }

   public void removeAll() {
      recipesToRemove.add(new MCRecipeManager.ActionRemoveAllRecipes());
   }

   public void remove(IIngredient output, @Optional boolean nbtMatch) {
      if (output == null) {
         CraftTweakerAPI.logError("Cannot remove recipes for a null item!");
      } else {
         actionRemoveRecipesNoIngredients.addOutput(output, nbtMatch);
      }
   }

   public void removeByRecipeName(String recipeName, @Optional IItemStack outputFilter) {
      recipesToRemove.add(new MCRecipeManager.ActionRemoveRecipeByRecipeName(recipeName, outputFilter));
   }

   public void removeByRegex(String regexString, @Optional IItemStack outputFilter) {
      recipesToRemove.add(new MCRecipeManager.ActionRemoveRecipeByRegex(regexString, outputFilter));
   }

   public void removeByMod(String modid) {
      recipesToRemove.add(new MCRecipeManager.ActionRemoveRecipeByMod(modid));
   }

   public void removeShaped(IIngredient output, IIngredient[][] ingredients) {
      if (output == null) {
         CraftTweakerAPI.logError("Cannot remove recipes for a null item!");
      } else {
         recipesToRemove.add(new MCRecipeManager.ActionRemoveShapedRecipes(output, ingredients));
      }
   }

   public void removeShapeless(IIngredient output, IIngredient[] ingredients, boolean wildcard) {
      if (output == null) {
         CraftTweakerAPI.logError("Cannot remove recipes for a null item!");
      } else {
         recipesToRemove.add(new MCRecipeManager.ActionRemoveShapelessRecipes(output, ingredients, wildcard));
      }
   }

   public void removeByInput(IIngredient input) {
      if (input == null) {
         CraftTweakerAPI.logError("Cannot remove recipes for a null item!");
      } else {
         recipesToRemove.add(new MCRecipeManager.ActionRemoveRecipesByInput(input));
      }
   }

   public IItemStack craft(IItemStack[][] contents) {
      Container container = new MCRecipeManager.ContainerVirtual();
      int width = 0;
      int height = contents.length;

      for(IItemStack[] row : contents) {
         width = Math.max(width, row.length);
      }

      ItemStack[] iContents = new ItemStack[width * height];
      Arrays.fill(iContents, ItemStack.EMPTY);

      for(int i = 0; i < height; ++i) {
         for(int j = 0; j < contents[i].length; ++j) {
            if (contents[i][j] != null) {
               iContents[i * width + j] = CraftTweakerMC.getItemStack(contents[i][j]);
            }
         }
      }

      InventoryCrafting inventory = new InventoryCrafting(container, width, height);

      for(int i = 0; i < iContents.length; ++i) {
         inventory.setInventorySlotContents(i, iContents[i]);
      }

      ItemStack result = CraftingManager.findMatchingResult(inventory, (World)null);
      if (result.isEmpty()) {
         return null;
      } else {
         return CraftTweakerMC.getIItemStack(result);
      }
   }

   public void replaceAllOccurences(IIngredient toReplace, IIngredient replaceWith, IIngredient forOutput) {
      MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.addSubAction(new MCRecipeManager.SubActionReplaceAllOccurences(toReplace, replaceWith, forOutput));
   }

   private static class ActionAddShapedRecipe extends MCRecipeManager.ActionBaseAddRecipe {
      public ActionAddShapedRecipe(IItemStack output, IIngredient[][] ingredients, IRecipeFunction function, IRecipeAction action, boolean mirrored, boolean hidden) {
         this((String)null, output, ingredients, function, action, mirrored, hidden);
      }

      public ActionAddShapedRecipe(String name, IItemStack output, IIngredient[][] ingredients, IRecipeFunction function, IRecipeAction action, boolean mirrored, boolean hidden) {
         this.setName(name);
      }
   }

   private static class ActionAddShapelessRecipe extends MCRecipeManager.ActionBaseAddRecipe {
      public ActionAddShapelessRecipe(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action) {
         this((String)null, output, ingredients, function, action, false);
      }

      public ActionAddShapelessRecipe(String name, IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action, boolean hidden) {
         this.setName(name);
      }
   }

   public static class ActionBaseAddRecipe implements IAction {
      protected MCRecipeBase recipe;
      protected IItemStack output;
      protected boolean isShaped;
      protected String name;

      /** @deprecated */
      @Deprecated
      public ActionBaseAddRecipe() {
      }

      private ActionBaseAddRecipe(MCRecipeBase recipe, IItemStack output, boolean isShaped) {
         this.recipe = recipe;
         this.output = output;
         this.isShaped = isShaped;
         if (recipe.hasTransformers()) {
            MCRecipeManager.transformerRecipes.add(recipe);
         }

         if (recipe.hasRecipeAction()) {
            MCRecipeManager.actionRecipes.add(recipe);
         }

      }

      public IItemStack getOutput() {
         return this.output;
      }

      public void setOutput(IItemStack output) {
         this.output = output;
      }

      public String getName() {
         return this.name;
      }

      protected void setName(String name) {
         if (name != null) {
            String proposedName = MCRecipeManager.cleanRecipeName(name);
            if (MCRecipeManager.usedRecipeNames.contains(proposedName)) {
               this.name = this.calculateName();
               CraftTweakerAPI.logWarning("Recipe name [" + name + "] has duplicate uses, defaulting to calculated hash!");
            } else {
               this.name = proposedName;
            }
         } else {
            this.name = this.calculateName();
         }

         MCRecipeManager.usedRecipeNames.add(this.name);
      }

      public String calculateName() {
         int hash;
         for(hash = this.recipe.toCommandString().hashCode(); MCRecipeManager.usedHashes.contains(hash); ++hash) {
            ;
         }

         MCRecipeManager.usedHashes.add(hash);
         return (this.isShaped ? "ct_shaped" : "ct_shapeless") + hash;
      }

      public void apply() {
         ForgeRegistries.RECIPES.register(this.recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
      }

      public String describe() {
         return this.output != null ? "Adding " + (this.isShaped ? "shaped" : "shapeless") + " recipe for " + this.output.getDisplayName() + " with name " + this.name : "Trying to add " + (this.isShaped ? "shaped" : "shapeless") + "recipe without correct output";
      }

      public MCRecipeBase getRecipe() {
         return this.recipe;
      }
   }

   public abstract static class ActionBaseRemoveRecipes implements IAction {
      public ActionBaseRemoveRecipes() {
      }

      public void removeRecipes(List<ResourceLocation> removingRecipes) {
         removingRecipes.forEach((recipe) -> {
            RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(recipe);
         });
      }
   }

   public static class ActionRemoveAllRecipes extends MCRecipeManager.ActionBaseRemoveRecipes {
      public ActionRemoveAllRecipes() {
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            toRemove.add(recipe.getKey());
         }

         super.removeRecipes(toRemove);
      }

      public String describe() {
         return "Removing all crafting recipes";
      }
   }

   public static class ActionRemoveRecipeByMod extends MCRecipeManager.ActionBaseRemoveRecipes {
      String modid;

      public ActionRemoveRecipeByMod(String modid) {
         this.modid = modid;
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            if (((ResourceLocation)recipe.getKey()).getResourceDomain().equalsIgnoreCase(this.modid)) {
               toRemove.add(recipe.getKey());
            }
         }

         super.removeRecipes(toRemove);
      }

      public String describe() {
         return "Removing recipes matching: " + this.modid;
      }
   }

   public static class ActionRemoveRecipeByRecipeName extends MCRecipeManager.ActionBaseRemoveRecipes {
      String recipeName;
      IItemStack filter;

      public ActionRemoveRecipeByRecipeName(String recipeName, IItemStack filter) {
         this.recipeName = recipeName;
         this.filter = filter;
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            if (((ResourceLocation)recipe.getKey()).toString().equals(this.recipeName)) {
               if (this.filter != null) {
                  if (this.filter.matches(CraftTweakerMC.getIItemStackForMatching(((IRecipe)recipe.getValue()).getRecipeOutput()))) {
                     toRemove.add(recipe.getKey());
                  }
               } else {
                  toRemove.add(recipe.getKey());
               }
               break;
            }
         }

         super.removeRecipes(toRemove);
      }

      public String describe() {
         if (this.recipeName != null) {
            return this.filter != null ? "Removing recipe with name \"" + this.recipeName + "\", Matching filter: " + this.filter.getDisplayName() : "Removing recipe with name \"" + this.recipeName + "\"";
         } else {
            return "No name for the recipe to remove was given.";
         }
      }
   }

   public static class ActionRemoveRecipeByRegex extends MCRecipeManager.ActionBaseRemoveRecipes {
      String regexCheck;
      IItemStack filter;

      public ActionRemoveRecipeByRegex(String regexCheck, IItemStack filter) {
         this.regexCheck = regexCheck;
         this.filter = filter;
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();
         Pattern p = Pattern.compile(this.regexCheck);

         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            ResourceLocation resourceLocation = (ResourceLocation)recipe.getKey();
            Matcher m = p.matcher(resourceLocation.toString());
            if (m.matches()) {
               if (this.filter != null) {
                  if (this.filter.matches(CraftTweakerMC.getIItemStackForMatching(((IRecipe)recipe.getValue()).getRecipeOutput()))) {
                     toRemove.add(recipe.getKey());
                  }
               } else {
                  toRemove.add(resourceLocation);
               }
            }
         }

         super.removeRecipes(toRemove);
      }

      public String describe() {
         if (this.regexCheck != null) {
            return this.filter != null ? "Removing all recipes matching this regex: \"" + this.regexCheck + "\", Matching filter: " + this.filter.getDisplayName() : "Removing all recipes matching this regex: \"" + this.regexCheck + "\"";
         } else {
            return "No regex String for the recipe to remove was given.";
         }
      }
   }

   public static class ActionRemoveRecipesByInput extends MCRecipeManager.ActionBaseRemoveRecipes {
      private final IIngredient ingredient;

      public ActionRemoveRecipesByInput(IIngredient ingredient) {
         this.ingredient = ingredient;
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         label23:
         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            Iterator var4 = ((IRecipe)recipe.getValue()).getIngredients().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label23;
               }

               Ingredient ingredient = (Ingredient)var4.next();
               if (MCRecipeManager.matches(ingredient, this.ingredient)) {
                  break;
               }
            }

            toRemove.add(recipe.getKey());
         }

         super.removeRecipes(toRemove);
      }

      public String describe() {
         return "Removing recipes for " + this.ingredient + " input";
      }
   }

   public static class ActionRemoveRecipesNoIngredients extends MCRecipeManager.ActionBaseRemoveRecipes {
      private final List<Pair<IIngredient, Boolean>> outputs = new ArrayList();

      public ActionRemoveRecipesNoIngredients() {
      }

      public void addOutput(IIngredient output, @Optional boolean nbtMatch) {
         this.outputs.add(Pair.of(output, Boolean.valueOf(nbtMatch)));
      }

      public void apply() {
         List<ResourceLocation> toRemove = new ArrayList();

         for(Entry<ResourceLocation, IRecipe> recipe : MCRecipeManager.recipes) {
            ItemStack recipeOutput = ((IRecipe)recipe.getValue()).getRecipeOutput();
            IItemStack stack = CraftTweakerMC.getIItemStackForMatching(recipeOutput);
            if (stack != null && this.matches(stack)) {
               toRemove.add(recipe.getKey());
            }
         }

         super.removeRecipes(toRemove);
      }

      private boolean matches(IItemStack stack) {
         Iterator var2 = this.outputs.iterator();

         while(true) {
            if (!var2.hasNext()) {
               return false;
            }

            Pair<IIngredient, Boolean> entry = (Pair)var2.next();
            IIngredient output = (IIngredient)entry.getKey();
            Boolean nbtMatch = (Boolean)entry.getValue();
            if (nbtMatch.booleanValue()) {
               if (output.matchesExact(stack)) {
                  break;
               }
            } else if (output.matches(stack)) {
               break;
            }
         }

         return true;
      }

      public String describe() {
         return "Removing recipes for various outputs";
      }
   }

   public static class ActionRemoveShapedRecipes extends MCRecipeManager.ActionBaseRemoveRecipes {
      IIngredient output;
      IIngredient[][] ingredients;

      public ActionRemoveShapedRecipes(IIngredient output, IIngredient[][] ingredients) {
         this.output = output;
         this.ingredients = ingredients;
      }

      public void apply() {
         int ingredientsWidth = 0;
         int ingredientsHeight = 0;
         if (this.ingredients != null) {
            ingredientsHeight = this.ingredients.length;

            for(IIngredient[] ingredient : this.ingredients) {
               ingredientsWidth = Math.max(ingredientsWidth, ingredient.length);
            }
         }

         List<ResourceLocation> toRemove = new ArrayList();

         label88:
         for(Entry<ResourceLocation, IRecipe> recipeEntry : MCRecipeManager.recipes) {
            IRecipe recipe = (IRecipe)recipeEntry.getValue();
            ItemStack output = recipe.getRecipeOutput();
            if (!output.isEmpty() && this.output.matches(CraftTweakerMC.getIItemStackForMatching(output)) && recipe instanceof IShapedRecipe) {
               if (this.ingredients != null) {
                  IShapedRecipe shapedRecipe = (IShapedRecipe)recipe;
                  int width = shapedRecipe.getRecipeWidth();
                  int height = shapedRecipe.getRecipeHeight();
                  if (ingredientsWidth == width && ingredientsHeight == height) {
                     for(int j = 0; j < ingredientsHeight; ++j) {
                        IIngredient[] row = this.ingredients[j];

                        for(int k = 0; k < ingredientsWidth; ++k) {
                           IIngredient ingredient = k > row.length ? null : row[k];
                           Ingredient ing = (Ingredient)shapedRecipe.getIngredients().get(j * width + k);
                           ItemStack input;
                           if (ing != Ingredient.EMPTY && !ing.test(ItemStack.EMPTY) && ing.getMatchingStacks().length != 0) {
                              input = ing.getMatchingStacks()[0];
                           } else {
                              input = ItemStack.EMPTY;
                           }

                           if (!MCRecipeManager.matchesItem(input, ingredient)) {
                              continue label88;
                           }
                        }
                     }

                     toRemove.add(recipeEntry.getKey());
                  }
               } else {
                  toRemove.add(recipeEntry.getKey());
               }
            }
         }

         CraftTweakerAPI.logInfo(toRemove.size() + " removed");
         super.removeRecipes(toRemove);
      }

      public String describe() {
         return this.output != null ? "Removing Shaped recipes for " + this.output.toString() : "Trying to remove recipes for invalid output";
      }
   }

   public static class ActionRemoveShapelessRecipes extends MCRecipeManager.ActionBaseRemoveRecipes {
      IIngredient output;
      IIngredient[] ingredients;
      boolean wildcard;

      public ActionRemoveShapelessRecipes(IIngredient output, IIngredient[] ingredients, boolean wildcard) {
         this.output = output;
         this.ingredients = ingredients;
         this.wildcard = wildcard;
      }

      public void apply() {
         if (this.output != null) {
            List<ResourceLocation> toRemove = new ArrayList();
            Iterator var2 = MCRecipeManager.recipes.iterator();

            while(true) {
               Entry<ResourceLocation, IRecipe> entry;
               label113:
               while(true) {
                  if (!var2.hasNext()) {
                     CraftTweakerAPI.logInfo("Removing " + toRemove.size() + " Shapeless recipes.");
                     super.removeRecipes(toRemove);
                     return;
                  }

                  entry = (Entry)var2.next();
                  IRecipe recipe = (IRecipe)entry.getValue();
                  if (!((IRecipe)entry.getValue()).getRecipeOutput().isEmpty() && this.output.matches(CraftTweakerMC.getIItemStackForMatching(((IRecipe)entry.getValue()).getRecipeOutput())) && !(recipe instanceof IShapedRecipe) && (recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe)) {
                     if (this.ingredients == null) {
                        break;
                     }

                     if (recipe instanceof ShapelessRecipes) {
                        ShapelessRecipes srecipe = (ShapelessRecipes)recipe;
                        if (this.ingredients.length <= srecipe.getIngredients().size() && (this.wildcard || this.ingredients.length >= srecipe.getIngredients().size())) {
                           IIngredient[] var13 = this.ingredients;
                           int var14 = var13.length;
                           int var15 = 0;

                           while(true) {
                              if (var15 >= var14) {
                                 break label113;
                              }

                              IIngredient ingredient = var13[var15];
                              int k = 0;

                              while(true) {
                                 if (k >= srecipe.getIngredients().size()) {
                                    continue label113;
                                 }

                                 if (MCRecipeManager.matches(srecipe.recipeItems.get(k), ingredient)) {
                                    ++var15;
                                    break;
                                 }

                                 ++k;
                              }
                           }
                        }
                     } else {
                        if (!(recipe instanceof ShapelessOreRecipe)) {
                           break;
                        }

                        ShapelessOreRecipe srecipe = (ShapelessOreRecipe)recipe;
                        NonNullList<Ingredient> inputs = srecipe.getIngredients();
                        if (inputs.size() >= this.ingredients.length && (this.wildcard || inputs.size() <= this.ingredients.length)) {
                           IIngredient[] var7 = this.ingredients;
                           int var8 = var7.length;
                           int ingredient = 0;

                           while(true) {
                              if (ingredient >= var8) {
                                 break label113;
                              }

                              IIngredient ingredient = var7[ingredient];
                              int k = 0;

                              while(true) {
                                 if (k >= srecipe.getIngredients().size()) {
                                    continue label113;
                                 }

                                 if (MCRecipeManager.matches(inputs.get(k), ingredient)) {
                                    ++ingredient;
                                    break;
                                 }

                                 ++k;
                              }
                           }
                        }
                     }
                  }
               }

               toRemove.add(entry.getKey());
            }
         }
      }

      public String describe() {
         return this.output != null ? "Removing Shapeless recipes for " + this.output.toString() : "Trying to remove recipes for invalid output";
      }
   }

   public static enum ActionReplaceAllOccurences implements IAction {
      INSTANCE;

      private MCRecipeManager.SubActionReplaceAllOccurences first;
      private MCRecipeManager.SubActionReplaceAllOccurences last;
      private ICraftingRecipe currentModifiedRecipe;
      private IIngredient[] ingredients1D;
      private IIngredient[][] ingredients2D;
      private boolean recipeModified = false;
      private final List<MCRecipeManager.ActionRemoveRecipeByRecipeName> toRemoveRecipe = new ArrayList();

      private ActionReplaceAllOccurences() {
      }

      public void setCurrentModifiedRecipe(ICraftingRecipe currentModifiedRecipe) {
         if (currentModifiedRecipe != null && currentModifiedRecipe.getOutput() != null) {
            this.currentModifiedRecipe = currentModifiedRecipe;
            if (currentModifiedRecipe.isShaped()) {
               this.ingredients2D = currentModifiedRecipe.getIngredients2D();
            } else {
               this.ingredients1D = currentModifiedRecipe.getIngredients1D();
            }

         }
      }

      public void addSubAction(MCRecipeManager.SubActionReplaceAllOccurences subAction) {
         if (this.first == null) {
            this.first = subAction;
         }

         if (this.last != null) {
            this.last.next = subAction;
         }

         this.last = subAction;
      }

      public boolean hasSubAction() {
         return this.first != null;
      }

      public void clear() {
         this.first = this.last = null;
         this.toRemoveRecipe.clear();
      }

      public void apply() {
         if (this.currentModifiedRecipe != null) {
            for(MCRecipeManager.SubActionReplaceAllOccurences current = this.first; current != null; current = current.next) {
               current.apply();
            }

            if (this.recipeModified) {
               this.toRemoveRecipe.add(new MCRecipeManager.ActionRemoveRecipeByRecipeName(this.currentModifiedRecipe.getFullResourceName(), (IItemStack)null));
               String name = this.currentModifiedRecipe.getResourceDomain() + "_" + this.currentModifiedRecipe.getName() + "_modified";
               IItemStack out = this.currentModifiedRecipe.getOutput();
               if (this.currentModifiedRecipe.isShaped()) {
                  CraftTweakerAPI.recipes.addShaped(name, out, this.ingredients2D, (IRecipeFunction)null, (IRecipeAction)null);
               } else {
                  CraftTweakerAPI.recipes.addShapeless(name, out, this.ingredients1D, (IRecipeFunction)null, (IRecipeAction)null);
               }
            }

            this.currentModifiedRecipe = null;
            this.ingredients1D = null;
            this.ingredients2D = (IIngredient[][])null;
            this.recipeModified = false;
         }
      }

      public void describeSubActions() {
         for(MCRecipeManager.SubActionReplaceAllOccurences current = this.first; current != null; current = current.next) {
            CraftTweakerAPI.logInfo(current.describe());
         }

      }

      public void removeOldRecipes() {
         CraftTweaker.INSTANCE.applyActions(this.toRemoveRecipe, "Removing old recipes for replace occurrences action", "Failed to remove old recipes for replace occurrences action");
      }

      public String describe() {
         return null;
      }
   }

   private static class ContainerVirtual extends Container {
      private ContainerVirtual() {
      }

      public boolean canInteractWith(EntityPlayer var1) {
         return false;
      }
   }

   public static class SubActionReplaceAllOccurences implements IAction {
      private MCRecipeManager.SubActionReplaceAllOccurences next;
      private final IIngredient toReplace;
      private final IIngredient replaceWith;
      private final IIngredient forOutput;

      public SubActionReplaceAllOccurences(IIngredient toReplace, IIngredient replaceWith, IIngredient forOutput) {
         this.toReplace = (IIngredient)(toReplace == null ? MCItemStack.EMPTY : toReplace);
         this.replaceWith = replaceWith;
         this.forOutput = forOutput;
      }

      public void apply() {
         if (MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.currentModifiedRecipe != null) {
            if (this.forOutput == null || this.forOutput.contains(MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.currentModifiedRecipe.getOutput())) {
               this.applyShapedPattern();
               this.applyShapelessPattern();
            }

         }
      }

      public String describe() {
         String replaceWithName = this.replaceWith == null ? MCItemStack.EMPTY.toString() : this.replaceWith.toString();
         return "Removing all occurences of ingredient: " + this.toReplace + " and replacing them with " + replaceWithName;
      }

      private IIngredient changeIngredient(@Nullable IIngredient input) {
         if (input == null) {
            return this.toReplace == MCItemStack.EMPTY ? this.replaceWith : null;
         } else {
            List<IItemStack> inputItems = input.getItems();
            if (inputItems == null) {
               return input;
            } else {
               if (inputItems.size() == 1) {
                  if (this.toReplace.matches((IItemStack)inputItems.get(0))) {
                     return this.replaceWith;
                  }
               } else if (inputItems.size() > 1) {
                  IIngredient var10001 = this.toReplace;
                  this.toReplace.getClass();
                  if (inputItems.removeIf(var10001::matches)) {
                     if (inputItems.isEmpty()) {
                        return this.replaceWith;
                     }

                     IIngredient temp = new IngredientOr((IIngredient[])inputItems.toArray(new IIngredient[0]));
                     return this.replaceWith == null ? temp : temp.or(this.replaceWith);
                  }
               }

               return input;
            }
         }
      }

      private void applyShapelessPattern() {
         if (MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D != null) {
            boolean replaceWithAir = false;

            for(int i = 0; i < MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D.length; ++i) {
               IIngredient ingredient = MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D[i];
               IIngredient changeResult = this.changeIngredient(ingredient);
               if (changeResult == null) {
                  replaceWithAir = true;
               }

               if (changeResult != ingredient) {
                  MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D[i] = changeResult;
                  MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.recipeModified = true;
               }
            }

            if (replaceWithAir) {
               MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D = (IIngredient[])ArrayUtils.removeAllOccurences(MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients1D, (Object)null);
            }
         }

      }

      private void applyShapedPattern() {
         if (MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients2D != null) {
            for(int i = 0; i < MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients2D.length; ++i) {
               for(int j = 0; j < MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients2D[i].length; ++j) {
                  IIngredient ingredient = MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients2D[i][j];
                  IIngredient changeResult = this.changeIngredient(ingredient);
                  if (changeResult != ingredient) {
                     MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.ingredients2D[i][j] = changeResult;
                     MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.recipeModified = true;
                  }
               }
            }
         }

      }
   }
}
