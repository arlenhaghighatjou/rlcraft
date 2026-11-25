package jaredbgreat.dldungeons.api;

import jaredbgreat.dldungeons.ReadAPI;
import jaredbgreat.dldungeons.themes.ThemeType;
import java.util.Random;
import net.minecraft.world.World;

public class DLDungeonsAPI {
   public DLDungeonsAPI() {
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ) {
      ReadAPI.spawnDungeon(world, chunkX, chunkZ);
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ, long seed) {
      ReadAPI.spawnDungeon(world, chunkX, chunkZ, seed);
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ, Random random) {
      ReadAPI.spawnDungeon(world, chunkX, chunkZ, random);
   }

   public static void addDimension(byte dim) {
      ReadAPI.addDimension(dim);
   }

   public static void subDimension(byte dim) {
      ReadAPI.subDimension(dim);
   }

   public static void setDimensions(int[] dims) {
      ReadAPI.setDimensions(dims);
   }

   public static void setDifficulty(byte diff) {
      ReadAPI.setDifficulty(diff);
   }

   public static void loadTheme(String file) {
      ReadAPI.loadTheme(file);
   }

   /** @deprecated */
   @Deprecated
   public static void blacklistThemes() {
      ReadAPI.blacklistDimensions();
   }

   public static void blacklistDimensions() {
      ReadAPI.blacklistDimensions();
   }

   public static void whitelistDimensions() {
      ReadAPI.whitelistDimensions();
   }

   public static void reloadConfig() {
      ReadAPI.whitelistDimensions();
   }

   public static void addMob(String name, int level, String... types) {
      for(String type : types) {
         ThemeType.addMobToType(name, level, type);
      }

   }

   public static void removeMob(String name, int level, String... types) {
      for(String type : types) {
         ThemeType.removeMobFromType(name, level, type);
      }

   }

   public static void removeMob(String name, String... types) {
      for(String type : types) {
         for(int i = 0; i < 5; ++i) {
            ThemeType.removeMobFromType(name, i, type);
         }
      }

   }
}
