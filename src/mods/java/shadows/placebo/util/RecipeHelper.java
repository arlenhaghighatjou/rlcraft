package shadows.placebo.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeHelper {
   private int j = 0;
   private final String modid;
   private final String modname;
   private final List<IRecipe> recipes;

   public RecipeHelper(String modid, String modname, List<IRecipe> recipes) {
      this.modid = modid;
      this.modname = modname;
      this.recipes = recipes;
   }

   public void addRecipe(int j, IRecipe rec) {
      if (rec.getRegistryName() == null) {
         this.recipes.add(rec.setRegistryName(new ResourceLocation(this.modid, "recipe" + j)));
      } else {
         this.recipes.add(rec);
      }

   }

   public void addRecipe(String name, IRecipe rec) {
      if (rec.getRegistryName() == null) {
         this.recipes.add(rec.setRegistryName(new ResourceLocation(this.modid, name)));
      } else {
         this.recipes.add(rec);
      }

   }

   public void addShapeless(ItemStack output, Object... inputs) {
      this.addRecipe(this.j++, new FastShapelessRecipe(this.modid + ":" + this.j, output, this.createInput(inputs)));
   }

   public <T extends IForgeRegistryEntry<?>> void addShapeless(T output, Object... inputs) {
      this.addShapeless(makeStack(output), inputs);
   }

   public void addShapeless(String group, ItemStack output, Object... inputs) {
      this.addRecipe(this.j++, new FastShapelessRecipe(this.modid + ":" + group, output, this.createInput(inputs)));
   }

   public <T extends IForgeRegistryEntry<?>> void addShapeless(String group, T output, Object... inputs) {
      this.addShapeless(group, makeStack(output), inputs);
   }

   public void addShaped(ItemStack output, int width, int height, Object... input) {
      this.addRecipe(this.j++, this.genShaped(output, width, height, input));
   }

   public <T extends IForgeRegistryEntry<?>> void addShaped(T output, int width, int height, Object... input) {
      this.addShaped(makeStack(output), width, height, input);
   }

   public void addShaped(String group, ItemStack output, int width, int height, Object... input) {
      this.addRecipe(this.j++, this.genShaped(this.modid + ":" + group, output, width, height, input));
   }

   public <T extends IForgeRegistryEntry<?>> void addShaped(String group, T output, int width, int height, Object... input) {
      this.addShaped(group, makeStack(output), width, height, input);
   }

   public void addForgeShaped(ItemStack output, Object... input) {
      ShapedPrimer primer = CraftingHelper.parseShaped(input);
      this.addRecipe(this.j++, new ShapedRecipes((new ResourceLocation(this.modid, "recipe" + this.j)).toString(), primer.width, primer.height, primer.input, output));
   }

   public <T extends IForgeRegistryEntry<?>> void addForgeShaped(T output, Object... input) {
      this.addForgeShaped(makeStack(output), input);
   }

   public void addForgeShaped(String group, ItemStack output, Object... input) {
      ShapedPrimer primer = CraftingHelper.parseShaped(input);
      this.addRecipe(this.j++, new ShapedRecipes((new ResourceLocation(this.modid, group)).toString(), primer.width, primer.height, primer.input, output));
   }

   public void addForgeShaped(String name, String group, ItemStack output, Object... input) {
      ShapedPrimer primer = CraftingHelper.parseShaped(input);
      this.addRecipe(this.j++, (IRecipe)(new ShapedRecipes((new ResourceLocation(this.modid, group)).toString(), primer.width, primer.height, primer.input, output)).setRegistryName(this.modid, name));
   }

   public void addForgeShapeless(ItemStack output, Object... input) {
      this.addRecipe(this.j++, new FastShapelessRecipe((new ResourceLocation(this.modid, "recipe" + this.j)).toString(), output, this.createInput(input)));
   }

   public <T extends IForgeRegistryEntry<?>> void addForgeShapeless(T output, Object... input) {
      this.addForgeShapeless(makeStack(output), input);
   }

   public void addForgeShapeless(String group, ItemStack output, Object... input) {
      this.addRecipe(this.j++, new FastShapelessRecipe((new ResourceLocation(this.modid, group)).toString(), output, this.createInput(input)));
   }

   public void addForgeShapeless(String name, String group, ItemStack output, Object... input) {
      this.addRecipe(this.j++, (IRecipe)(new FastShapelessRecipe((new ResourceLocation(this.modid, group)).toString(), output, this.createInput(input))).setRegistryName(this.modid, name));
   }

   public ShapedRecipes genShaped(String group, ItemStack output, int l, int w, Object... input) {
      if (input[0] instanceof List) {
         input = ((List)input[0]).toArray();
      }

      if (l * w != input.length) {
         throw new UnsupportedOperationException("Attempted to add invalid shaped recipe.  Complain to the author of " + this.modname);
      } else {
         NonNullList<Ingredient> inputL = NonNullList.create();

         for(int i = 0; i < input.length; ++i) {
            Object k = input[i];
            if (k instanceof String) {
               inputL.add(i, RecipeHelper.CachedOreIngredient.create((String)k));
            } else if (k instanceof ItemStack && !((ItemStack)k).isEmpty()) {
               inputL.add(i, RecipeHelper.CachedIngredient.create((ItemStack)k));
            } else if (k instanceof IForgeRegistryEntry) {
               inputL.add(i, RecipeHelper.CachedIngredient.create(makeStack((IForgeRegistryEntry)k)));
            } else if (k instanceof Ingredient) {
               inputL.add(i, (Ingredient)k);
            } else {
               inputL.add(i, Ingredient.EMPTY);
            }
         }

         return new ShapedRecipes(group, l, w, inputL, output);
      }
   }

   public ShapedRecipes genShaped(ItemStack output, int width, int height, Object... input) {
      return this.genShaped(this.modid + ":" + this.j, output, width, height, input);
   }

   public NonNullList<Ingredient> createInput(Object... input) {
      if (input[0] instanceof List) {
         input = ((List)input[0]).toArray();
      } else if (input[0] instanceof Object[]) {
         input = input[0];
      }

      NonNullList<Ingredient> inputL = NonNullList.create();

      for(int i = 0; i < input.length; ++i) {
         Object k = input[i];
         if (k instanceof String) {
            inputL.add(i, RecipeHelper.CachedOreIngredient.create((String)k));
         } else if (k instanceof ItemStack && !((ItemStack)k).isEmpty()) {
            inputL.add(i, RecipeHelper.CachedIngredient.create((ItemStack)k));
         } else if (k instanceof IForgeRegistryEntry) {
            inputL.add(i, RecipeHelper.CachedIngredient.create(makeStack((IForgeRegistryEntry)k)));
         } else {
            if (!(k instanceof Ingredient)) {
               throw new UnsupportedOperationException("Attempted to add invalid shapeless recipe.  Complain to the author of " + this.modname);
            }

            inputL.add(i, (Ingredient)k);
         }
      }

      return inputL;
   }

   public void addSimpleShapeless(ItemStack output, ItemStack input, int numInputs) {
      this.addShapeless(output, NonNullList.withSize(numInputs, input));
   }

   public <T extends IForgeRegistryEntry<?>> void addSimpleShapeless(T output, T input, int numInputs) {
      this.addSimpleShapeless(makeStack(output), makeStack(input), numInputs);
   }

   public <T extends IForgeRegistryEntry<?>> void addSimpleShapeless(T output, ItemStack input, int numInputs) {
      this.addSimpleShapeless(makeStack(output), input, numInputs);
   }

   public <T extends IForgeRegistryEntry<?>> void addSimpleShapeless(ItemStack output, T input, int numInputs) {
      this.addSimpleShapeless(output, makeStack(input), numInputs);
   }

   public void register(IForgeRegistry<IRecipe> reg) {
      reg.registerAll((IForgeRegistryEntry[])this.recipes.toArray(new IRecipe[0]));
   }

   public static <T extends IForgeRegistryEntry<?>> ItemStack makeStack(T thing, int size, int meta) {
      return thing instanceof Item ? new ItemStack((Item)thing, size, meta) : new ItemStack((Block)thing, size, meta);
   }

   public static <T extends IForgeRegistryEntry<?>> ItemStack makeStack(T thing, int size) {
      return makeStack(thing, size, 0);
   }

   public static <T extends IForgeRegistryEntry<?>> ItemStack makeStack(T thing) {
      return makeStack(thing, 1, 0);
   }

   public static void replaceInAllRecipes(ItemStack old, Ingredient newThing) {
      for(IRecipe rec : ForgeRegistries.RECIPES) {
         if (rec instanceof IShapedRecipe) {
            NonNullList<Ingredient> list = NonNullList.create();

            for(Ingredient ing : rec.getIngredients()) {
               if (ing.getMatchingStacks().length == 1 && OreDictionary.itemMatches(ing.getMatchingStacks()[0], old, false)) {
                  list.add(newThing);
               } else {
                  list.add(ing);
               }
            }

            ResourceLocation regname = rec.getRegistryName();
            int width = ((IShapedRecipe)rec).getRecipeWidth();
            int height = ((IShapedRecipe)rec).getRecipeHeight();
            ForgeRegistries.RECIPES.register((new ShapedRecipes(rec.getGroup(), width, height, list, rec.getRecipeOutput())).setRegistryName(regname));
         } else {
            NonNullList<Ingredient> list = NonNullList.create();

            for(Ingredient ing : rec.getIngredients()) {
               if (ing.getMatchingStacks().length == 1 && OreDictionary.itemMatches(ing.getMatchingStacks()[0], old, false)) {
                  list.add(newThing);
               } else {
                  list.add(ing);
               }
            }

            ResourceLocation regname = rec.getRegistryName();
            ForgeRegistries.RECIPES.register((new FastShapelessRecipe(rec.getGroup(), rec.getRecipeOutput(), list)).setRegistryName(regname));
         }
      }

   }

   public static void addPotionRecipe(ItemStack input, PotionType inputPot, ItemStack reagent, ItemStack output, PotionType ontputPot) {
      BrewingRecipeRegistry.addRecipe(PotionUtils.addPotionToItemStack(input, inputPot), reagent, PotionUtils.addPotionToItemStack(output, ontputPot));
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(ItemStack input, PotionType inputPot, ItemStack reagent, T output, PotionType outputPot) {
      addPotionRecipe(input, inputPot, reagent, makeStack(output), outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(ItemStack input, PotionType inputPot, T reagent, T output, PotionType outputPot) {
      addPotionRecipe(input, inputPot, makeStack(reagent), makeStack(output), outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(T input, PotionType inputPot, ItemStack reagent, ItemStack output, PotionType outputPot) {
      addPotionRecipe(makeStack(input), inputPot, reagent, output, outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(T input, PotionType inputPot, T reagent, ItemStack output, PotionType outputPot) {
      addPotionRecipe(makeStack(input), inputPot, makeStack(reagent), output, outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(ItemStack input, PotionType inputPot, T reagent, ItemStack output, PotionType outputPot) {
      addPotionRecipe(input, inputPot, makeStack(reagent), output, outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(T input, PotionType inputPot, ItemStack reagent, T output, PotionType outputPot) {
      addPotionRecipe(makeStack(input), inputPot, reagent, makeStack(output), outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(PotionType inputPot, ItemStack reagent, PotionType outputPot) {
      addPotionRecipe(Items.POTIONITEM, inputPot, reagent, Items.POTIONITEM, outputPot);
   }

   public static <T extends IForgeRegistryEntry<?>> void addPotionRecipe(PotionType inputPot, T reagent, PotionType outputPot) {
      addPotionRecipe(Items.POTIONITEM, inputPot, makeStack(reagent), Items.POTIONITEM, outputPot);
   }

   public static class CachedIngredient extends Ingredient {
      public static Int2ObjectMap<RecipeHelper.CachedIngredient> ing = new Int2ObjectOpenHashMap();

      protected CachedIngredient(ItemStack... matches) {
         super(matches);
         if (matches.length == 1) {
            ing.put(RecipeItemHelper.pack(matches[0]), this);
         }

      }

      public static RecipeHelper.CachedIngredient create(ItemStack... matches) {
         if (matches.length == 1) {
            RecipeHelper.CachedIngredient coi = (RecipeHelper.CachedIngredient)ing.get(RecipeItemHelper.pack(matches[0]));
            return coi != null ? coi : new RecipeHelper.CachedIngredient(matches);
         } else {
            return new RecipeHelper.CachedIngredient(matches);
         }
      }
   }

   public static class CachedOreIngredient extends OreIngredient {
      public static HashMap<String, RecipeHelper.CachedOreIngredient> ing = new HashMap();

      protected CachedOreIngredient(String ore) {
         super(ore);
         ing.put(ore, this);
      }

      public static RecipeHelper.CachedOreIngredient create(String ore) {
         RecipeHelper.CachedOreIngredient coi = (RecipeHelper.CachedOreIngredient)ing.get(ore);
         return coi != null ? coi : new RecipeHelper.CachedOreIngredient(ore);
      }
   }
}
