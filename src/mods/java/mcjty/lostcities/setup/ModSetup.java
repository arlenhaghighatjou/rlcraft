package mcjty.lostcities.setup;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import mcjty.lostcities.ForgeEventHandlers;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.TerrainEventHandlers;
import mcjty.lostcities.config.ConfigSetup;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.dimensions.ModDimensions;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.network.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModSetup {
   public static boolean chisel = false;
   public static boolean biomesoplenty = false;
   public static boolean atg = false;
   public static boolean neid = false;
   public static boolean jeid = false;
   private Logger logger;
   public static VFile2 modConfigDir;

   public ModSetup() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      this.logger = e.getModLog();
      PacketHandler.registerMessages("lostcities");
      this.setupModCompat();
      modConfigDir = e.getModConfigurationDirectory();
      ConfigSetup.init();
      ModDimensions.init();
      LootTableList.register(new ResourceLocation("lostcities", "chests/lostcitychest"));
      LootTableList.register(new ResourceLocation("lostcities", "chests/raildungeonchest"));
   }

   private void setupModCompat() {
      chisel = Loader.isModLoaded("chisel");
      biomesoplenty = Loader.isModLoaded("biomesoplenty") || Loader.isModLoaded("BiomesOPlenty");
      neid = Loader.isModLoaded("neid");
      jeid = Loader.isModLoaded("jeid");
   }

   public Logger getLogger() {
      return this.logger;
   }

   public void init(FMLInitializationEvent e) {
      (new ForgeEventHandlers()).register(MinecraftForge.EVENT_BUS);
      (new TerrainEventHandlers()).register(MinecraftForge.TERRAIN_GEN_BUS);
   }

   public void postInit(FMLPostInitializationEvent e) {
      ConfigSetup.postInit();
      ConfigSetup.profileConfigs.clear();
      AssetRegistries.reset();

      for(String path : LostCityConfiguration.ASSETS) {
         if (path.startsWith("/")) {
            try {
               InputStream inputstream = EagRuntime.getResourceStream("/lostcities" + path);
               Throwable var7 = null;

               try {
                  AssetRegistries.load(inputstream, path);
               } catch (Throwable var17) {
                  var7 = var17;
                  throw var17;
               } finally {
                  if (inputstream != null) {
                     if (var7 != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable var16) {
                           var7.addSuppressed(var16);
                        }
                     } else {
                        inputstream.close();
                     }
                  }

               }
            } catch (IOException var19) {
               throw new UncheckedIOException(var19);
            }
         } else {
            if (!path.startsWith("$")) {
               throw new RuntimeException("Invalid path for lostcity resource in 'assets' config!");
            }

            VFile2 file = new VFile2(modConfigDir, path.substring(1));
            AssetRegistries.load(file);
         }
      }

      if (LostCityConfiguration.DEBUG) {
         this.logger.info("Asset parts loaded: " + AssetRegistries.PARTS.getCount());
         AssetRegistries.showStatistics();
      }

   }
}
