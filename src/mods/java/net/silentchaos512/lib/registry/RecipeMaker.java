package net.silentchaos512.lib.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.silentchaos512.lib.item.IEnumItems;
import net.silentchaos512.lib.recipe.IRecipeSerializer;
import net.silentchaos512.lib.recipe.RecipeJsonHell;

public final class RecipeMaker {
   private final String modId;
   private final String resourcePrefix;
   private int lastRecipeIndex = -1;
   private boolean jsonHellMode = false;
   private int oldRecipeRegisterCount;
   private final Map<ResourceLocation, IRecipeSerializer> customSerializers = new HashMap();

   public RecipeMaker(String modId) {
      this.modId = modId;
      this.resourcePrefix = modId + ":";
   }

   private String getRecipeKey(String key) {
      if (key.isEmpty()) {
         key = "recipe" + ++this.lastRecipeIndex;
      }

      return this.resourcePrefix + key.toLowerCase(Locale.ROOT);
   }

   private void tryJsonHell(String name, boolean shaped, ItemStack result, Object... inputs) {
      if (this.jsonHellMode) {
         name = name.replaceFirst(this.resourcePrefix, "");

         try {
            if (this.customSerializers.containsKey(result.getItem().getRegistryName())) {
               RecipeJsonHell.createRecipe(name, (IRecipeSerializer)this.customSerializers.get(result.getItem().getRegistryName()), result, inputs);
            } else if (shaped) {
               RecipeJsonHell.createShapedRecipe(name, result, inputs);
            } else {
               RecipeJsonHell.createShapelessRecipe(name, result, inputs);
            }
         } catch (IllegalArgumentException var6) {
            System.out.println("Failed to make JSON recipe \"" + name + "\" (" + var6.getMessage() + ")");
         }

      }
   }

   public void setRecipeSerializer(Item item, IRecipeSerializer serializer) {
      this.customSerializers.put(item.getRegistryName(), serializer);
   }

   public IRecipe addShapeless(String key, ItemStack result, Object... inputs) {
      return this.addShapeless(this.modId, key, result, this.makeStackArray(inputs));
   }

   public IRecipe addShapeless(String group, String key, ItemStack result, Object... inputs) {
      return this.addShapeless(group, key, result, this.makeStackArray(inputs));
   }

   public IRecipe addShapeless(String key, ItemStack result, ItemStack... inputs) {
      return this.addShapeless(this.modId, key, result, inputs);
   }

   public IRecipe addShapeless(String group, String key, ItemStack result, ItemStack... inputs) {
      key = this.getRecipeKey(key);
      IRecipe recipe = this.makeShapeless(result, inputs);
      this.registerRecipe(new ResourceLocation(key), recipe);
      this.tryJsonHell(key, false, result, (Object[])inputs);
      return recipe;
   }

   public IRecipe makeShapeless(ItemStack result, Object... inputs) {
      return this.makeShapeless(result, this.makeStackArray(inputs));
   }

   public IRecipe makeShapeless(String group, ItemStack result, Object... inputs) {
      return this.makeShapeless(result, this.makeStackArray(inputs));
   }

   public IRecipe makeShapeless(ItemStack result, ItemStack... inputs) {
      return this.makeShapeless(this.modId, result, inputs);
   }

   public IRecipe makeShapeless(String group, ItemStack result, ItemStack... inputs) {
      NonNullList<Ingredient> list = NonNullList.create();

      for(ItemStack stack : inputs) {
         list.add(Ingredient.fromStacks(new ItemStack[]{stack}));
      }

      return new ShapelessRecipes(group, result, list);
   }

   public IRecipe makeShapeless(ItemStack result, Ingredient... inputs) {
      return this.makeShapeless(this.modId, result, inputs);
   }

   public IRecipe makeShapeless(String group, ItemStack result, Ingredient... inputs) {
      NonNullList<Ingredient> list = NonNullList.create();
      Collections.addAll(list, inputs);
      return new ShapelessRecipes(group, result, list);
   }

   public IRecipe addShaped(String key, ItemStack result, Object... inputs) {
      return this.addShaped(this.modId, key, result, inputs);
   }

   public IRecipe addShaped(String group, String key, ItemStack result, Object... inputs) {
      key = this.getRecipeKey(key);
      IRecipe recipe = this.makeShaped(group, result, inputs);
      this.registerRecipe(new ResourceLocation(key), recipe);
      this.tryJsonHell(key, true, result, inputs);
      return recipe;
   }

   public IRecipe makeShaped(ItemStack result, Object... inputs) {
      return this.makeShaped(this.modId, result, inputs);
   }

   public IRecipe makeShaped(String group, ItemStack result, Object... inputs) {
      ShapedPrimer primer = CraftingHelper.parseShaped(inputs);
      return new ShapedRecipes(group, primer.width, primer.height, primer.input, result);
   }

   public IRecipe addShapelessOre(String key, ItemStack result, Object... inputs) {
      return this.addShapelessOre(this.modId, key, result, inputs);
   }

   public IRecipe addShapelessOre(String group, String key, ItemStack result, Object... inputs) {
      key = this.getRecipeKey(key);
      IRecipe recipe = this.makeShapelessOre(group, result, inputs);
      this.registerRecipe(new ResourceLocation(key), recipe);
      this.tryJsonHell(key, false, result, inputs);
      return recipe;
   }

   public IRecipe makeShapelessOre(ItemStack result, Object... inputs) {
      return this.makeShapelessOre(this.modId, result, inputs);
   }

   public IRecipe makeShapelessOre(String group, ItemStack result, Object... inputs) {
      return new ShapelessOreRecipe(new ResourceLocation(group), result, inputs);
   }

   public IRecipe addShapedOre(String key, ItemStack result, Object... inputs) {
      return this.addShapedOre(this.modId, key, result, inputs);
   }

   public IRecipe addShapedOre(String group, String key, ItemStack result, Object... inputs) {
      key = this.getRecipeKey(key);
      IRecipe recipe = this.makeShapedOre(group, result, inputs);
      this.registerRecipe(new ResourceLocation(key), recipe);
      this.tryJsonHell(key, true, result, inputs);
      return recipe;
   }

   public IRecipe makeShapedOre(ItemStack result, Object... inputs) {
      return this.makeShapedOre(this.modId, result, inputs);
   }

   public IRecipe makeShapedOre(String group, ItemStack result, Object... inputs) {
      return new ShapedOreRecipe(new ResourceLocation(group), result, inputs);
   }

   public void addSmelting(Block input, ItemStack output, float xp) {
      GameRegistry.addSmelting(input, output, xp);
   }

   public void addSmelting(Item input, ItemStack output, float xp) {
      GameRegistry.addSmelting(input, output, xp);
   }

   public void addSmelting(ItemStack input, ItemStack output, float xp) {
      GameRegistry.addSmelting(input, output, xp);
   }

   public IRecipe addGenericRecipe(String key, IRecipe recipe) {
      key = this.getRecipeKey(key);
      this.registerRecipe(new ResourceLocation(key), recipe);
      return recipe;
   }

   public IRecipe addCustomRecipe(String key, IRecipe recipe) {
      key = this.getRecipeKey(key);
      this.registerRecipe(new ResourceLocation(key), recipe);
      return recipe;
   }

   public IRecipe[] addCompression(String key, ItemStack small, ItemStack big, int count) {
      return this.addCompression(this.modId, key, small, big, count);
   }

   public IRecipe[] addCompression(String group, String key, ItemStack small, ItemStack big, int count) {
      count = MathHelper.clamp(count, 1, 9);
      ItemStack[] smallArray = new ItemStack[count];

      for(int i = 0; i < count; ++i) {
         smallArray[i] = small;
      }

      IRecipe[] ret = new IRecipe[2];
      ret[0] = this.addShapeless(group, key + "_compress", big, smallArray);
      ItemStack smallCopy = small.copy();
      smallCopy.setCount(count);
      ret[1] = this.addShapeless(group, key + "_decompress", smallCopy, big);
      return ret;
   }

   public IRecipe[] addCompressionOre(String key, ItemStack small, ItemStack big, @Nullable String smallOreName, @Nullable String bigOreName, int count) {
      count = MathHelper.clamp(count, 1, 9);
      Object[] smallArray = new Object[count];

      for(int i = 0; i < count; ++i) {
         smallArray[i] = smallOreName != null && !smallOreName.isEmpty() ? smallOreName : small;
      }

      IRecipe[] ret = new IRecipe[2];
      ret[0] = this.addShapelessOre(this.modId, key + "_oredict_compress", big, smallArray);
      ItemStack smallCopy = small.copy();
      smallCopy.setCount(count);
      Object bigObj = bigOreName != null && !bigOreName.isEmpty() ? bigOreName : big;
      ret[1] = this.addShapelessOre(this.modId, key + "_oredict_decompress", smallCopy, bigObj);
      return ret;
   }

   public IRecipe addSurround(String key, ItemStack output, ItemStack middleStack, Object... surrounding) {
      return this.addSurround(this.modId, key, output, middleStack, surrounding);
   }

   public IRecipe addSurround(String group, String key, ItemStack output, ItemStack middleStack, Object... surrounding) {
      ItemStack[] stacks = new ItemStack[surrounding.length];
      int i = -1;

      for(Object obj : surrounding) {
         ++i;
         if (obj instanceof Block) {
            stacks[i] = new ItemStack((Block)obj);
         } else if (obj instanceof Item) {
            stacks[i] = new ItemStack((Item)obj);
         } else if (obj instanceof ItemStack) {
            stacks[i] = (ItemStack)obj;
         }
      }

      switch(surrounding.length) {
      case 0:
         throw new IllegalArgumentException("No surrounding items!");
      case 1:
         return this.addShaped(group, key, output, "xxx", "xcx", "xxx", 'c', middleStack, 'x', stacks[0]);
      case 2:
         return this.addShaped(group, key, output, "xyx", "ycy", "xyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1]);
      case 3:
         return this.addShaped(group, key, output, " xy", "zcz", "yx ", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2]);
      case 4:
         return this.addShaped(group, key, output, "xyz", "dcd", "zyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2], 'd', stacks[3]);
      default:
         throw new IllegalArgumentException("Too many items!");
      }
   }

   public IRecipe addSurroundOre(String key, ItemStack output, Object middle, Object... surrounding) {
      return this.addSurroundOre(this.modId, key, output, middle, surrounding);
   }

   public IRecipe addSurroundOre(String group, String key, ItemStack output, Object middle, Object... surrounding) {
      switch(surrounding.length) {
      case 0:
         throw new IllegalArgumentException("No surrounding items!");
      case 1:
         return this.addShapedOre(group, key, output, "xxx", "xcx", "xxx", 'c', middle, 'x', surrounding[0]);
      case 2:
         return this.addShapedOre(group, key, output, "xyx", "ycy", "xyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1]);
      case 3:
         return this.addShapedOre(group, key, output, " xy", "zcz", "yx ", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2]);
      case 4:
         return this.addShapedOre(group, key, output, "xyz", "dcd", "zyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2], 'd', surrounding[3]);
      default:
         throw new IllegalArgumentException("Too many items!");
      }
   }

   private void registerRecipe(ResourceLocation name, IRecipe recipe) {
      if (recipe.getRegistryName() == null) {
         recipe.setRegistryName(name);
      }

      ForgeRegistries.RECIPES.register(recipe);
      ++this.oldRecipeRegisterCount;
   }

   private ItemStack[] makeStackArray(Object... params) {
      ItemStack[] result = new ItemStack[params.length];

      for(int i = 0; i < params.length; ++i) {
         Object obj = params[i];
         if (obj instanceof ItemStack) {
            result[i] = (ItemStack)obj;
         } else if (obj instanceof Item) {
            result[i] = new ItemStack((Item)obj);
         } else if (obj instanceof Block) {
            result[i] = new ItemStack((Block)obj);
         } else {
            if (!(obj instanceof IEnumItems)) {
               throw new IllegalArgumentException("Can't make object of type " + obj.getClass() + " into an ItemStack! Index " + i + ", obj=" + obj);
            }

            result[i] = ((IEnumItems)obj).getStack();
         }
      }

      return result;
   }

   public void setJsonHellMode(boolean jsonHellMode) {
      this.jsonHellMode = jsonHellMode;
   }

   public int getOldRecipeRegisterCount() {
      return this.oldRecipeRegisterCount;
   }
}
