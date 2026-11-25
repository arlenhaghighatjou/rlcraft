package lykrast.defiledlands.common.init;

import lykrast.defiledlands.common.util.Config;
import lykrast.defiledlands.common.world.biome.BiomeDefiled;
import lykrast.defiledlands.common.world.biome.BiomeDesertDefiled;
import lykrast.defiledlands.common.world.biome.BiomeForestTenebra;
import lykrast.defiledlands.common.world.biome.BiomeForestVilespine;
import lykrast.defiledlands.common.world.biome.BiomeHillsDefiled;
import lykrast.defiledlands.common.world.biome.BiomeIcePlainsDefiled;
import lykrast.defiledlands.common.world.biome.BiomePlainsDefiled;
import lykrast.defiledlands.common.world.biome.BiomeSwampDefiled;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ModBiomes {
   public static BiomeDefiled desertDefiled = new BiomeDesertDefiled();
   public static BiomeDefiled plainsDefiled = new BiomePlainsDefiled();
   public static BiomeDefiled forestTenebra = new BiomeForestTenebra();
   public static BiomeDefiled forestVilespine = new BiomeForestVilespine();
   public static BiomeDefiled hillsDefiled = new BiomeHillsDefiled();
   public static BiomeDefiled swampDefiled = new BiomeSwampDefiled();
   public static BiomeDefiled icePlainsDefiled = new BiomeIcePlainsDefiled();

   public ModBiomes() {
   }

   @SubscribeEvent
   public static void registerBiomes(Register<Biome> event) {
      register(event, desertDefiled, BiomeType.DESERT, "desert_defiled", Config.weightDesertDefiled, Type.SPOOKY, Type.HOT, Type.DRY, Type.SANDY);
      register(event, plainsDefiled, BiomeType.WARM, "plains_defiled", Config.weightPlainsDefiled, Type.SPOOKY, Type.PLAINS);
      register(event, forestTenebra, BiomeType.WARM, "forest_tenebra", Config.weightForestTenebra, Type.SPOOKY, Type.FOREST);
      register(event, forestVilespine, BiomeType.WARM, "forest_vilespine", Config.weightForestVilespine, Type.SPOOKY, Type.FOREST);
      register(event, hillsDefiled, BiomeType.COOL, "hills_defiled", Config.weightHillsDefiled, Type.SPOOKY, Type.MOUNTAIN, Type.HILLS);
      register(event, swampDefiled, BiomeType.WARM, "swamp_defiled", Config.weightSwampDefiled, Type.SPOOKY, Type.WET, Type.SWAMP);
      register(event, icePlainsDefiled, BiomeType.ICY, "ice_plains_defiled", Config.weightIcePlainsDefiled, Type.SPOOKY, Type.COLD, Type.SNOWY, Type.WASTELAND);
   }

   public static void register(Register<Biome> event, Biome biome, BiomeType type, String name, int weight, Type... biomeDictTypes) {
      biome.setRegistryName(new ResourceLocation("defiledlands", name));
      event.getRegistry().register(biome);

      for(Type biomeDictType : biomeDictTypes) {
         BiomeDictionary.addTypes(biome, new Type[]{biomeDictType});
      }

      BiomeManager.addBiome(type, new BiomeEntry(biome, weight));
   }

   public static void cleanSpawnLists() {
      desertDefiled.cleanSpawnLists();
      plainsDefiled.cleanSpawnLists();
      forestTenebra.cleanSpawnLists();
      forestVilespine.cleanSpawnLists();
      hillsDefiled.cleanSpawnLists();
      swampDefiled.cleanSpawnLists();
      icePlainsDefiled.cleanSpawnLists();
   }
}
