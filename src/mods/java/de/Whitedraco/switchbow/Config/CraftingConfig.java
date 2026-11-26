package de.Whitedraco.switchbow.Config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import java.util.List;
import java.io.StringReader;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.Whitedraco.switchbow.Initial;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;

public class CraftingConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static VFile2 ConfigPathCraftingRecipes = new VFile2("config/SwitchBow/Crafting-Recipes");

   public CraftingConfig() {
   }

   static String[] getResourceListing(String path) {
      String prefix = "switchbow/" + path;
      List<String> files = EagRuntime.listResources(prefix);
      Set<String> result = new HashSet();

      for(int i = 0, l = files.size(); i < l; ++i) {
         String entry = files.get(i).substring(prefix.length());
         int checkSubdir = entry.indexOf("/");
         if (checkSubdir >= 0) {
            entry = entry.substring(0, checkSubdir);
         }

         result.add(entry);
      }

      return (String[])result.toArray(new String[result.size()]);
   }

   public static void writeConfigCrafting() throws IOException {
      String[] fileNames = getResourceListing("assets/switchbow/recipes_template/");

      for(int i = 0; i < fileNames.length; ++i) {
         if ("json".equals(FilenameUtils.getExtension(fileNames[i]))) {
            String recipeName = fileNames[i];
            InputStream reader = EagRuntime.getResourceStream("/switchbow/assets/switchbow/recipes_template/" + recipeName);
            copy(reader, "config/SwitchBow/Crafting-Recipes/" + recipeName);
         }
      }

   }

   public static void readCraftingConfig() throws IOException {
      Iterator<String> iterator = ConfigPathCraftingRecipes.listFilenames(true).iterator();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

      while(iterator.hasNext()) {
         String path1 = (String)iterator.next();
         if ("json".equals(FilenameUtils.getExtension(path1))) {
            String path2 = path1.substring(ConfigPathCraftingRecipes.getPath().length() + 1);
            String s = FilenameUtils.removeExtension(path2).replaceAll("\\\\", "/");
            ResourceLocation resourcelocation = new ResourceLocation("switchbow".toLowerCase(), s);
            BufferedReader bufferedreader = null;

            try {
               String text = (new VFile2(path1)).getAllChars();
               if (text == null) {
                  throw new FileNotFoundException(path1);
               }

               bufferedreader = new BufferedReader(new StringReader(text));
               Object recipe = parseRecipeJson((JsonObject)JsonUtils.fromJson(gson, bufferedreader, JsonObject.class), s);
               if (recipe != null) {
                  ForgeRegistries.RECIPES.register((IRecipe)recipe);
               }
            } catch (JsonParseException var13) {
               System.out.println("Parsing error loading recipe " + resourcelocation);
               System.out.println(var13.getLocalizedMessage());
               boolean flag = false;
            } catch (IOException var14) {
               System.out.println("Couldn't read recipe " + resourcelocation + " from " + path1);
               boolean flag = false;
            }
         }
      }

   }

   private static Object parseRecipeJson(JsonObject fromJson, String s2) {
      String s = JsonUtils.getString(fromJson, "type");
      String activ = JsonUtils.getString(fromJson, "activ");
      boolean active = true;
      if (activ.equals("false")) {
         return null;
      } else if (s.contains("crafting_shaped")) {
         ShapedRecipes recipe = ShapedRecipes.deserialize(fromJson);
         recipe.setRegistryName("switchbow".toLowerCase(), s2);
         return recipe;
      } else if (s.contains("crafting_shapeless")) {
         ShapelessRecipes recipe = ShapelessRecipes.deserialize(fromJson);
         recipe.setRegistryName("switchbow".toLowerCase(), s2);
         return recipe;
      } else {
         throw new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      }
   }

   public static boolean copy(InputStream source, String destination) {
      boolean succeess = true;
      VFile2 tester = new VFile2(destination);

      try {
         if (!tester.exists() && source != null) {
            System.out.println("Copying Recipe ->" + source + "\n\tto ->" + destination);
            tester.setAllBytes(EaglerInputStream.inputStreamToBytes(source));
         }
      } catch (IOException var5) {
         succeess = false;
      }

      return succeess;
   }

   public static VFile2 getPath() {
      return ConfigPathCraftingRecipes;
   }
}
