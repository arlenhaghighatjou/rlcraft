package com.endertech.minecraft.forge.world;

import com.endertech.common.CommonCollect;
import com.endertech.common.CommonString;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.UnitId;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public enum Biomes implements IForgeEnum {
   PLAINS(1),
   DESERT(2),
   EXTREME_HILLS(3),
   FOREST(4),
   TAIGA(5),
   SWAMPLAND(6),
   RIVER(7),
   HELL(8),
   THE_END(9),
   BEACHES(16),
   JUNGLE(21),
   SAVANNA(35),
   LOSTCITIES(111);

   public final int id;
   public static final String BIOMES_LIST_SYNTAX = "Biome is specified either by its numeric ID or by name (case-insensitive)\nEach ID must be on a separate line without any delimiters.\nIf the whitelist is set, the blacklist will be ignored.";

   private Biomes(int id) {
      this.id = id;
   }

   public static UnitConfig createConfigFor(ForgeMod mod, Biome biome) {
      String fileName = UnitId.from(biome.getRegistryName(), 0).toConfigName();
      int biomeId = Biome.getIdForBiome(biome);
      UnitConfig config = new UnitConfig(getConfigsBaseDir(mod), fileName);
      readId(config, biomeId);
      ForgeConfig.readConfigEnabled(config, false);
      return config;
   }

   public static Path getConfigsBaseDir(ForgeMod mod) {
      return mod.getConfig().getConfigDir().resolve(Biomes.class.getSimpleName());
   }

   protected static int readId(ForgeConfig config, int defaultId) {
      int id = ForgeConfig.getInt(config, "Biome", "id", defaultId, IntBounds.INTEGER, "Biome identifier.");
      return id;
   }

   public static int getId(ForgeConfig config) {
      return readId(config, Integer.MAX_VALUE);
   }

   public static boolean isConfigEnabled(ForgeConfig config) {
      return ForgeConfig.readConfigEnabled(config, false);
   }

   public static void logMissingId(ForgeMod mod, ForgeConfig config) {
      mod.getLogger().debug("Missing biome ID = {} in {}", Integer.valueOf(getId(config)), config.getConfigFile());
   }

   public String getName() {
      return this.getLowerCase(this.name());
   }

   public static CommonCollect.BlackWhiteList<Biome> from(ForgeConfig config, String category, String comment) {
      category = IHaveConfig.expandClassCategory(Biomes.class, category);
      if (config != null) {
         config.setCategoryComment(category, comment + "\n" + "Biome is specified either by its numeric ID or by name (case-insensitive)\nEach ID must be on a separate line without any delimiters.\nIf the whitelist is set, the blacklist will be ignored.");
      }

      List<Biome> black = readFrom(config, category, "blackList", "");
      List<Biome> white = readFrom(config, category, "whiteList", "");
      return new CommonCollect.BlackWhiteList<Biome>(black, white);
   }

   public static List<Biome> readFrom(ForgeConfig config, String category, String key, String comment) {
      if (config == null) {
         return Collections.emptyList();
      } else {
         String[] strArray = config.getStrArray(category, key, new String[0], comment);
         List<Biome> list = new ArrayList();

         for(String str : strArray) {
            str = str.trim();
            if (!str.isEmpty()) {
               try {
                  int id = Integer.parseInt(str);
                  Biome biome = Biome.getBiomeForId(id);
                  if (biome != null) {
                     list.add(biome);
                  } else {
                     ForgeMain.instance.getLogger().debug("No such biome with ID = " + id);
                  }
               } catch (Exception var13) {
                  ResourceLocation loc = new ResourceLocation(str);
                  Biome biome = (Biome)ForgeRegistries.BIOMES.getValue(loc);
                  if (biome != null) {
                     list.add(biome);
                  } else {
                     ForgeMain.instance.getLogger().debug("No such biome with name " + CommonString.quoted(loc.toString()));
                  }
               }
            }
         }

         return list;
      }
   }
}
