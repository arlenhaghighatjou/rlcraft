package de.Whitedraco.switchbow.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.Whitedraco.switchbow.Initial;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class CraftingConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File ConfigPathCraftingRecipes = new File("config/SwitchBow/Crafting-Recipes");
   private static FileSystem filesystem;

   public CraftingConfig() {
   }

   static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
      URL dirURL = clazz.getClassLoader().getResource(path);
      if (dirURL != null && dirURL.getProtocol().equals("file")) {
         return (new File(dirURL.toURI())).list();
      } else {
         if (dirURL == null) {
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
         }

         if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();
            Set<String> result = new HashSet();

            while(entries.hasMoreElements()) {
               String name = ((JarEntry)entries.nextElement()).getName();
               if (name.startsWith(path)) {
                  String entry = name.substring(path.length());
                  int checkSubdir = entry.indexOf("/");
                  if (checkSubdir >= 0) {
                     entry = entry.substring(0, checkSubdir);
                  }

                  result.add(entry);
               }
            }

            return (String[])result.toArray(new String[result.size()]);
         } else {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
         }
      }
   }

   public static void writeConfigCrafting() throws IOException, URISyntaxException {
      String[] fileNames = getResourceListing(Initial.class, "assets/switchbow/recipes_template/");

      for(int i = 0; i < fileNames.length; ++i) {
         if ("json".equals(FilenameUtils.getExtension(fileNames[i]))) {
            String recipeName = fileNames[i];
            InputStream reader = Initial.class.getResourceAsStream("/assets/switchbow/recipes_template/" + recipeName);
            copy(reader, "config/SwitchBow/Crafting-Recipes/" + recipeName);
         }
      }

   }

   public static void readCraftingConfig() throws IOException {
      Iterator<Path> iterator = Files.walk(ConfigPathCraftingRecipes.toPath()).iterator();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

      while(iterator.hasNext()) {
         Path path1 = (Path)iterator.next();
         if ("json".equals(FilenameUtils.getExtension(path1.toString()))) {
            Path path2 = ConfigPathCraftingRecipes.toPath().relativize(path1);
            String s = FilenameUtils.removeExtension(path2.toString()).replaceAll("\\\\", "/");
            ResourceLocation resourcelocation = new ResourceLocation("switchbow".toLowerCase(), s);
            BufferedReader bufferedreader = null;

            try {
               bufferedreader = Files.newBufferedReader(path1);
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
            } finally {
               IOUtils.closeQuietly(bufferedreader);
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
      File tester = new File(destination);

      try {
         if (!tester.exists() && source != null) {
            System.out.println("Copying Recipe ->" + source + "\n\tto ->" + destination);
            Files.copy(source, Paths.get(destination), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         }
      } catch (IOException var5) {
         succeess = false;
      }

      return succeess;
   }

   public static File getPath() {
      return ConfigPathCraftingRecipes;
   }
}
