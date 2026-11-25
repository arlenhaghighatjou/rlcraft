package net.silentchaos512.lib.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.silentchaos512.lib.SilentLib;

public final class RecipeJsonHell {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

   private RecipeJsonHell() {
      throw new IllegalAccessError("Utility class");
   }

   public static void createRecipe(String name, IRecipeSerializer serializer, ItemStack result, Object... components) {
      createRecipeFile(serializer.serialize(result, components), name);
   }

   public static void createShapedRecipe(String name, ItemStack result, Object... components) {
      createRecipe(name, RecipeJsonHell.ShapedSerializer.INSTANCE, result, components);
   }

   public static void createShapelessRecipe(String name, ItemStack result, Object... components) {
      createRecipe(name, RecipeJsonHell.ShapelessSerializer.INSTANCE, result, components);
   }

   private static JsonObject serializeComponent(Object component) {
      if (component instanceof Item) {
         return serializeComponent(new ItemStack((Item)component));
      } else if (component instanceof Block) {
         return serializeComponent(new ItemStack((Block)component));
      } else if (!(component instanceof ItemStack)) {
         if (component instanceof String) {
            JsonObject ret = new JsonObject();
            ret.addProperty("type", "forge:ore_dict");
            ret.addProperty("ore", (String)component);
            return ret;
         } else {
            throw new IllegalArgumentException("Could not serialize the unsupported type " + component.getClass().getName());
         }
      } else {
         ItemStack stack = (ItemStack)component;
         JsonObject ret = new JsonObject();
         ret.addProperty("item", stack.getItem().getRegistryName().toString());
         if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
            ret.addProperty("data", Integer.valueOf(stack.getItemDamage()));
         }

         if (stack.getCount() > 1) {
            ret.addProperty("count", Integer.valueOf(stack.getCount()));
         }

         if (stack.hasTagCompound()) {
            SilentLib.LOGGER.warn("Recipe component contains NBT and cannot be serialized properly: {}", component);
         }

         return ret;
      }
   }

   private static void createRecipeFile(JsonObject json, String fileName) {
      fileName = fileName.replaceAll(":", "_");
      File directory = new File("recipes/" + Loader.instance().activeModContainer().getModId());
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File output = new File(directory, fileName + ".json");

      try {
         FileWriter writer = new FileWriter(output);
         Throwable var5 = null;

         try {
            GSON.toJson(json, writer);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (writer != null) {
               if (var5 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (IOException var17) {
         var17.printStackTrace();
      }

   }

   public static final class ShapedSerializer implements IRecipeSerializer {
      public static final RecipeJsonHell.ShapedSerializer INSTANCE = new RecipeJsonHell.ShapedSerializer();

      private ShapedSerializer() {
      }

      public JsonObject serialize(ItemStack result, Object... components) {
         JsonObject json = new JsonObject();
         List<String> pattern = new ArrayList();
         Map<String, JsonObject> inputMap = new HashMap();
         boolean isOreDict = false;
         Character curKey = null;

         int i;
         for(i = 0; i < components.length && components[i] instanceof String; ++i) {
            pattern.add((String)components[i]);
         }

         for(; i < components.length; ++i) {
            Object component = components[i];
            if (component instanceof Character) {
               if (curKey != null) {
                  throw new IllegalArgumentException("Provided two char keys in a row");
               }

               curKey = (Character)component;
            } else {
               if (curKey == null) {
                  throw new IllegalArgumentException("Providing object without a char key");
               }

               if (component instanceof String) {
                  isOreDict = true;
               }

               inputMap.put(Character.toString(curKey.charValue()), RecipeJsonHell.serializeComponent(component));
               curKey = null;
            }
         }

         JsonArray patternArray = new JsonArray();
         pattern.forEach(patternArray::add);
         JsonObject inputKeys = new JsonObject();
         inputMap.forEach(inputKeys::add);
         json.addProperty("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
         json.add("pattern", patternArray);
         json.add("key", inputKeys);
         json.add("result", RecipeJsonHell.serializeComponent(result));
         return json;
      }
   }

   public static final class ShapelessSerializer implements IRecipeSerializer {
      public static final RecipeJsonHell.ShapelessSerializer INSTANCE = new RecipeJsonHell.ShapelessSerializer();

      private ShapelessSerializer() {
      }

      public JsonObject serialize(ItemStack result, Object... components) {
         JsonObject json = new JsonObject();
         List<JsonObject> ingredients = new ArrayList();
         boolean isOreDict = false;

         for(Object component : components) {
            if (component instanceof String) {
               isOreDict = true;
            }

            ingredients.add(RecipeJsonHell.serializeComponent(component));
         }

         JsonArray ingredientArray = new JsonArray();
         ingredients.forEach(ingredientArray::add);
         json.add("ingredients", ingredientArray);
         json.addProperty("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
         json.add("result", RecipeJsonHell.serializeComponent(result));
         return json;
      }
   }
}
