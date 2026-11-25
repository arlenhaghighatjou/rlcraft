package com.oblivioussp.spartanweaponry.util;

import com.google.gson.stream.JsonWriter;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.Loader;

public class JsonRecipeGenerator {
   public JsonRecipeGenerator() {
   }

   public static void generateRecipes() {
      String subDir = "recipes";
      List<ToolMaterialEx> materials = new ArrayList();
      materials.add(ToolMaterialEx.WOOD);
      materials.add(ToolMaterialEx.STONE);
      materials.add(ToolMaterialEx.IRON);
      materials.add(ToolMaterialEx.GOLD);
      materials.add(ToolMaterialEx.DIAMOND);
      materials.addAll(ItemRegistrySW.customMaterials);
      Log.info("Generating .json Recipes!");

      for(ToolMaterialEx material : materials) {
         generateOneRecipe("dagger_" + material.getUnlocName(), subDir, material, "spartanweaponry:dagger_" + material.getUnlocName(), new String[]{"i", "h"}, true, false, false);
         generateOneRecipe("longsword_" + material.getUnlocName(), subDir, material, "spartanweaponry:longsword_" + material.getUnlocName(), new String[]{" i ", " i ", "ihi"}, true, false, false);
         generateOneRecipe("katana_" + material.getUnlocName(), subDir, material, "spartanweaponry:katana_" + material.getUnlocName(), new String[]{"  i", " i ", "h  "}, true, false, false);
         generateOneRecipe("saber_" + material.getUnlocName(), subDir, material, "spartanweaponry:saber_" + material.getUnlocName(), new String[]{" i", " i", "ih"}, true, false, false);
         generateOneRecipe("rapier_" + material.getUnlocName(), subDir, material, "spartanweaponry:rapier_" + material.getUnlocName(), new String[]{"  i", "ii ", "hi "}, true, false, false);
         generateOneRecipe("greatsword_" + material.getUnlocName(), subDir, material, "spartanweaponry:greatsword_" + material.getUnlocName(), new String[]{" i ", "iii", "ihi"}, true, false, false);
         generateOneRecipe("hammer_" + material.getUnlocName(), subDir, material, "spartanweaponry:hammer_" + material.getUnlocName(), new String[]{"iii", "iii", " h "}, true, false, false);
         generateOneRecipe("warhammer_" + material.getUnlocName(), subDir, material, "spartanweaponry:warhammer_" + material.getUnlocName(), new String[]{" i", "ii", " h"}, true, false, false);
         generateOneRecipe("spear_" + material.getUnlocName(), subDir, material, "spartanweaponry:spear_" + material.getUnlocName(), new String[]{"i", "p"}, false, true, false);
         generateOneRecipe("halberd_" + material.getUnlocName(), subDir, material, "spartanweaponry:halberd_" + material.getUnlocName(), new String[]{" i", "ii", "ip"}, false, true, false);
         generateOneRecipe("pike_" + material.getUnlocName(), subDir, material, "spartanweaponry:pike_" + material.getUnlocName(), new String[]{"i", "p", "p"}, false, true, false);
         generateOneRecipe("lance_" + material.getUnlocName(), subDir, material, "spartanweaponry:lance_" + material.getUnlocName(), new String[]{"i", "p", "h"}, true, true, false);
         generateLongbowRecipe("longbow_" + material.getUnlocName(), subDir, material, "spartanweaponry:longbow_" + material.getUnlocName(), new String[]{"hwi", "w s", "iss"});
         generateCrossbowRecipe("crossbow_" + material.getUnlocName(), subDir, material, "spartanweaponry:crossbow_" + material.getUnlocName(), new String[]{"bsi", "sw ", "i h"});
         generateOneRecipe("throwing_knife_" + material.getUnlocName(), subDir, material, "spartanweaponry:throwing_knife_" + material.getUnlocName(), new String[]{"hi"}, true, false, false);
         generateOneRecipe("throwing_axe_" + material.getUnlocName(), subDir, material, "spartanweaponry:throwing_axe_" + material.getUnlocName(), new String[]{"hi", " i"}, true, false, false);
         generateOneRecipe("javelin_" + material.getUnlocName(), subDir, material, "spartanweaponry:javelin_" + material.getUnlocName(), new String[]{"pi"}, false, true, false);
         generateOneRecipe("parrying_dagger_" + material.getUnlocName(), subDir, material, "spartanweaponry:parrying_dagger_" + material.getUnlocName(), new String[]{" i", "ih"}, true, false, false);
         generateBoomerangRecipe("boomerang_" + material.getUnlocName(), subDir, material, "spartanweaponry:boomerang_" + material.getUnlocName(), new String[]{"iww", "w  ", "w  "});
         generateOneRecipe("battleaxe_" + material.getUnlocName(), subDir, material, "spartanweaponry:battleaxe_" + material.getUnlocName(), new String[]{"iii", "isi", " h "}, true, false, true);
         generateOneRecipe("mace_" + material.getUnlocName(), subDir, material, "spartanweaponry:mace_" + material.getUnlocName(), new String[]{" ii", " si", "h  "}, true, false, true);
         generateOneRecipe("glaive_" + material.getUnlocName(), subDir, material, "spartanweaponry:glaive_" + material.getUnlocName(), new String[]{" i", " i", "ip"}, false, true, false);
         generateOneRecipe("quarterstaff_" + material.getUnlocName(), subDir, material, "spartanweaponry:staff_" + material.getUnlocName(), new String[]{"  i", " p ", "i  "}, false, true, false);
         if (material == ToolMaterialEx.DIAMOND) {
            subDir = "recipes/modded";
         }
      }

      throw new IllegalArgumentException("Why crash the game? JSON Recipe generation complete. The game doesn't need to be run anymore!");
   }

   protected static void generateOneRecipe(String recipeName, String subDir, ToolMaterialEx material, String result, String[] pattern, boolean hasHandle, boolean hasPole, boolean hasStick) {
      File dir = new File(Loader.instance().getConfigDir(), subDir);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, recipeName + ".json");

      try {
         JsonWriter writer = new JsonWriter(new FileWriter(file));
         writer.setIndent("  ");
         writer.beginObject();
         writer.name("type").value("minecraft:crafting_shaped");
         writer.name("pattern").beginArray();

         for(String row : pattern) {
            writer.value(row);
         }

         writer.endArray();
         writer.name("key").beginObject();
         writer.name("i").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value(material.getOreDictForRepairMaterial());
         writer.endObject();
         if (hasHandle) {
            writer.name("h").beginObject();
            writer.name("item").value("spartanweaponry:material");
            writer.name("data").value(0L);
            writer.endObject();
         }

         if (hasPole) {
            writer.name("p").beginObject();
            writer.name("item").value("spartanweaponry:material");
            writer.name("data").value(1L);
            writer.endObject();
         }

         if (hasStick) {
            writer.name("s").beginObject();
            writer.name("type").value("forge:ore_dict");
            writer.name("ore").value("stickWood");
            writer.endObject();
         }

         writer.endObject();
         writer.name("result").beginObject();
         writer.name("item").value(result);
         writer.endObject();
         writer.name("conditions").beginArray();
         writer.beginObject();
         writer.name("type").value("item_exists");
         writer.name("item").value(result);
         writer.endObject();
         writer.endArray();
         writer.endObject();
         writer.close();
      } catch (IOException var15) {
         Log.error("Couldn't properly close file: " + file.getName() + " - " + var15.getMessage());
      }

   }

   protected static void generateLongbowRecipe(String recipeName, String subDir, ToolMaterialEx material, String result, String[] pattern) {
      File dir = new File(Loader.instance().getConfigDir(), subDir);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, recipeName + ".json");

      try {
         JsonWriter writer = new JsonWriter(new FileWriter(file));
         writer.setIndent("  ");
         writer.beginObject();
         writer.name("type").value("minecraft:crafting_shaped");
         writer.name("pattern").beginArray();

         for(String row : pattern) {
            writer.value(row);
         }

         writer.endArray();
         writer.name("key").beginObject();
         writer.name("i").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value(material.getOreDictForRepairMaterial());
         writer.endObject();
         writer.name("h").beginObject();
         writer.name("item").value("spartanweaponry:material");
         writer.name("data").value(0L);
         writer.endObject();
         writer.name("s").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value("string");
         writer.endObject();
         writer.name("w").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value("stickWood");
         writer.endObject();
         writer.endObject();
         writer.name("result").beginObject();
         writer.name("item").value(result);
         writer.endObject();
         writer.name("conditions").beginArray();
         writer.beginObject();
         writer.name("type").value("item_exists");
         writer.name("item").value(result);
         writer.endObject();
         writer.endArray();
         writer.endObject();
         writer.close();
      } catch (IOException var12) {
         Log.error("Couldn't properly close file: " + file.getName());
      }

   }

   protected static void generateCrossbowRecipe(String recipeName, String subDir, ToolMaterialEx material, String result, String[] pattern) {
      File dir = new File(Loader.instance().getConfigDir(), subDir);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, recipeName + ".json");

      try {
         JsonWriter writer = new JsonWriter(new FileWriter(file));
         writer.setIndent("  ");
         writer.beginObject();
         writer.name("type").value("minecraft:crafting_shaped");
         writer.name("pattern").beginArray();

         for(String row : pattern) {
            writer.value(row);
         }

         writer.endArray();
         writer.name("key").beginObject();
         writer.name("i").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value(material.getOreDictForRepairMaterial());
         writer.endObject();
         writer.name("h").beginObject();
         writer.name("item").value("spartanweaponry:material");
         writer.name("data").value(0L);
         writer.endObject();
         writer.name("b").beginObject();
         writer.name("item").value("minecraft:bow");
         writer.endObject();
         writer.name("s").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value("string");
         writer.endObject();
         writer.name("w").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value("logWood");
         writer.endObject();
         writer.endObject();
         writer.name("result").beginObject();
         writer.name("item").value(result);
         writer.endObject();
         writer.name("conditions").beginArray();
         writer.beginObject();
         writer.name("type").value("item_exists");
         writer.name("item").value(result);
         writer.endObject();
         writer.endArray();
         writer.endObject();
         writer.close();
      } catch (IOException var12) {
         Log.error("Couldn't properly close file: " + file.getName());
      }

   }

   protected static void generateBoomerangRecipe(String recipeName, String subDir, ToolMaterialEx material, String result, String[] pattern) {
      File dir = new File(Loader.instance().getConfigDir(), subDir);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File file = new File(dir, recipeName + ".json");

      try {
         JsonWriter writer = new JsonWriter(new FileWriter(file));
         writer.setIndent("  ");
         writer.beginObject();
         writer.name("type").value("minecraft:crafting_shaped");
         writer.name("pattern").beginArray();

         for(String row : pattern) {
            writer.value(row);
         }

         writer.endArray();
         writer.name("key").beginObject();
         writer.name("i").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value(material.getOreDictForRepairMaterial());
         writer.endObject();
         writer.name("w").beginObject();
         writer.name("type").value("forge:ore_dict");
         writer.name("ore").value("plankWood");
         writer.endObject();
         writer.endObject();
         writer.name("result").beginObject();
         writer.name("item").value(result);
         writer.endObject();
         writer.name("conditions").beginArray();
         writer.beginObject();
         writer.name("type").value("item_exists");
         writer.name("item").value(result);
         writer.endObject();
         writer.endArray();
         writer.endObject();
         writer.close();
      } catch (IOException var12) {
         Log.error("Couldn't properly close file: " + file.getName());
      }

   }
}
