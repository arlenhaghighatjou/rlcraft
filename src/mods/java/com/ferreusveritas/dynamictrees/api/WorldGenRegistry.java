package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.events.PopulateDataBaseEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.util.JsonHelper;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBasePopulatorJson;
import com.ferreusveritas.dynamictrees.worldgen.MultiDimensionalPopulator;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
import com.ferreusveritas.dynamictrees.worldgen.json.IJsonBiomeApplier;
import com.ferreusveritas.dynamictrees.worldgen.json.IJsonBiomeSelector;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WorldGenRegistry {
   private static final String RESOURCEPATH = "worldgen/default.json";
   private static final String CONFIGPATH = "/dynamictrees";
   private static final String WORLDGENCONFIGPATH = "/dynamictrees/worldgen.json";
   private static final String DIMGENCONFIGPATH = "/dynamictrees/dimensions.json";

   public WorldGenRegistry() {
   }

   public static boolean isWorldGenEnabled() {
      return ModConfigs.worldGen;
   }

   private static IBiomeDataBasePopulator collectDataBasePopulators() {
      WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent event = new WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent();
      event.register(new BiomeDataBasePopulatorJson(new ResourceLocation("dynamictrees", "worldgen/default.json")));
      MinecraftForge.EVENT_BUS.post(event);
      loadCustomDefaultPopulator(event);
      return event.getPopulator();
   }

   private static void loadCustomDefaultPopulator(WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent event) {
      File file = new File(ModConfigs.configDirectory.getAbsolutePath() + "/dynamictrees/worldgen.json");
      if (!file.exists()) {
         writeBlankJsonArrayToFile(file);
      } else {
         event.register(new BiomeDataBasePopulatorJson(JsonHelper.load(file)));
      }

   }

   private static void loadMultiDimensionalPopulator(IBiomeDataBasePopulator populator) {
      File file = new File(ModConfigs.configDirectory.getAbsolutePath() + "/dynamictrees/dimensions.json");
      if (!file.exists()) {
         writeBlankJsonArrayToFile(file);
      } else {
         new MultiDimensionalPopulator(JsonHelper.load(file), populator);
      }

   }

   private static void writeBlankJsonArrayToFile(File file) {
      try {
         (new File(ModConfigs.configDirectory.getAbsolutePath() + "/dynamictrees")).mkdirs();
         BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
         writer.write("[]");
         writer.close();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static void populateDataBase() {
      if (isWorldGenEnabled()) {
         WorldGenRegistry.BiomeDataBaseJsonCapabilityRegistryEvent capabilityEvent = new WorldGenRegistry.BiomeDataBaseJsonCapabilityRegistryEvent();
         BiomeDataBasePopulatorJson.registerJsonCapabilities(capabilityEvent);
         MinecraftForge.EVENT_BUS.post(capabilityEvent);
         TreeGenerator.getTreeGenerator().clearAllBiomeDataBases();
         BiomeDataBase database = TreeGenerator.getTreeGenerator().getDefaultBiomeDataBase();
         IBiomeDataBasePopulator biomePopulator = collectDataBasePopulators();
         biomePopulator.populate(database);
         MinecraftForge.EVENT_BUS.post(new PopulateDataBaseEvent(database, biomePopulator));
         loadMultiDimensionalPopulator(biomePopulator);
         ModConfigs.dimensionBlacklist.forEach((d) -> {
            TreeGenerator.getTreeGenerator().BlackListDimension(d.intValue());
         });
         BiomeDataBasePopulatorJson.cleanup();
      }

   }

   public static boolean validateBiomeDataBases() {
      return isWorldGenEnabled() ? TreeGenerator.getTreeGenerator().validateBiomeDataBases() : true;
   }

   public static class BiomeDataBaseJsonCapabilityRegistryEvent extends Event {
      public BiomeDataBaseJsonCapabilityRegistryEvent() {
      }

      public void register(String name, IJsonBiomeSelector selector) {
         BiomeDataBasePopulatorJson.addJsonBiomeSelector(name, selector);
      }

      public void register(String name, IJsonBiomeApplier applier) {
         BiomeDataBasePopulatorJson.addJsonBiomeApplier(name, applier);
      }
   }

   public static class BiomeDataBasePopulatorRegistryEvent extends Event {
      private final List<IBiomeDataBasePopulator> biomePopulators = new ArrayList();

      public BiomeDataBasePopulatorRegistryEvent() {
      }

      public void register(IBiomeDataBasePopulator populator) {
         this.biomePopulators.add(populator);
      }

      private IBiomeDataBasePopulator getPopulator() {
         return (biomeDataBase) -> {
            this.biomePopulators.forEach((p) -> {
               p.populate(biomeDataBase);
            });
         };
      }
   }
}
