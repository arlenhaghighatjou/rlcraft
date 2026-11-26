package jaredbgreat.dldungeons;

import java.io.FileNotFoundException;
import java.io.StringReader;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveHandler;
import jaredbgreat.dldungeons.builder.Builder;
import jaredbgreat.dldungeons.themes.ThemeReader;
import java.io.BufferedReader;
import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;

public class ReadAPI {
   public ReadAPI() {
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ) {
      if (!ConfigHandler.disableAPI) {
         try {
            Builder.placeDungeon(new EaglercraftRandom(), chunkX, chunkZ, world);
         } catch (Throwable var4) {
            System.err.println("[DLDUNGEONS] Danger!  Failed to finalize a dungeon after building!");
            var4.printStackTrace();
         }

      }
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ, long seed) {
      if (!ConfigHandler.disableAPI) {
         try {
            Builder.placeDungeon(new EaglercraftRandom(seed), chunkX, chunkZ, world);
         } catch (Throwable var6) {
            System.err.println("[DLDUNGEONS] Danger!  Failed to finalize a dungeon after building!");
            var6.printStackTrace();
         }

      }
   }

   public static void spawnDungeon(World world, int chunkX, int chunkZ, EaglercraftRandom random) {
      if (!ConfigHandler.disableAPI) {
         try {
            Builder.placeDungeon(random, chunkX, chunkZ, world);
         } catch (Throwable var5) {
            System.err.println("[DLDUNGEONS] Danger!  Failed to finalize a dungeon after building!");
            var5.printStackTrace();
         }

      }
   }

   public static void addDimension(byte dim) {
      if (!ConfigHandler.disableAPI) {
         GenerationHandler.addDimension(dim);
      }
   }

   public static void subDimension(int dim) {
      if (!ConfigHandler.disableAPI) {
         GenerationHandler.subDimension(dim);
      }
   }

   public static void setDimensions(int[] dims) {
      if (!ConfigHandler.disableAPI) {
         GenerationHandler.setDimensions(dims);
      }
   }

   public static void setDifficulty(int diff) {
      if (!ConfigHandler.disableAPI) {
         ConfigHandler.parseDiff(diff);
      }
   }

   public static void loadTheme(String file) {
      if (!ConfigHandler.disableAPI) {
         BufferedReader instream = null;

         try {
            instream = new BufferedReader(new StringReader(readText(new VFile2(ConfigHandler.configDir, file))));
            System.out.println("[DLDUNGEONS] Loading theme file " + file.toString());
            ThemeReader.parseTheme(instream, file.toString());
            if (instream != null) {
               instream.close();
            }
         } catch (IOException var4) {
            var4.printStackTrace();
         }

      }
   }

   public static void blacklistDimensions() {
      if (!ConfigHandler.disableAPI) {
         ConfigHandler.positiveDims = false;
      }
   }

   public static void whitelistDimensions() {
      if (!ConfigHandler.disableAPI) {
         ConfigHandler.positiveDims = true;
      }
   }

   public static VFile2 saveWorldData(World world) {
      return world.getSaveHandler() instanceof EaglerSaveHandler ? ((EaglerSaveHandler)world.getSaveHandler()).getWorldPath() : null;
   }

   private static String readText(VFile2 file) throws IOException {
      String text = file.getAllChars();
      if (text == null) {
         throw new FileNotFoundException(file.getPath());
      } else {
         return text;
      }
   }

   public static void reloadConfig() {
      if (!ConfigHandler.disableAPI) {
         ConfigHandler.reload();
      }
   }

   public static boolean apiOn() {
      return !ConfigHandler.disableAPI;
   }
}
