package com.dhanantry.scapeandrunparasites.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeInfestedPlain;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent.Register;

public class SRPBiomes {
   public static final BiomeParasite biomeInfested = new BiomeInfestedPlain();

   public SRPBiomes() {
   }

   private static void register(Register<Biome> event, Biome biome, BiomeType type, String name, int weight, Type... biomeDictTypes) {
      biome.setRegistryName(new ResourceLocation("srparasites", name));
      event.getRegistry().register(biome);

      for(Type biomeDictType : biomeDictTypes) {
         BiomeDictionary.addTypes(biome, new Type[]{biomeDictType});
      }

      BiomeManager.addBiome(type, new BiomeEntry(biome, weight));
   }

   public static void clearMobSpawnList() {
      biomeInfested.mobListClear();
   }

   public static class RegistrationHandler {
      public RegistrationHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Biome.class, RegistrationHandler::onEvent);
      }

      public static void onEvent(Register<Biome> event) {
         if (SRPConfigWorld.biomeRegster) {
            SRPBiomes.register(event, SRPBiomes.biomeInfested, BiomeType.COOL, "biome_parasite", SRPConfigWorld.biomeweight, Type.SPOOKY);
         }

      }
   }
}
