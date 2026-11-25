package com.lycanitesmobs.core.config;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ConfigSpawning extends ConfigBase {
   public static ConfigSpawning getConfig(ModInfo group, String configName) {
      String configFileName = configName.toLowerCase();
      if (!"lycanitesmobs".equalsIgnoreCase(group.modid)) {
         configFileName = group.modid + "-" + configFileName;
      }

      if (!configs.containsKey(configFileName)) {
         registerConfig(new ConfigSpawning(group, configName, configFileName));
      }

      ConfigBase config = (ConfigBase)ConfigBase.configs.get(configFileName);
      if (config instanceof ConfigSpawning) {
         return (ConfigSpawning)config;
      } else {
         LycanitesMobs.logWarning("", "[Config] Tried to access the Base Config: " + configName + " as a Spawning Config from group: " + group.name + "!");
         return null;
      }
   }

   public ConfigSpawning(ModInfo group, String name, String filename) {
      super(group, name, filename);
   }

   public Type[] getAllBiomeTypes() {
      return new Type[]{Type.HOT, Type.COLD, Type.SPARSE, Type.DENSE, Type.WET, Type.DRY, Type.SAVANNA, Type.CONIFEROUS, Type.JUNGLE, Type.SPOOKY, Type.DEAD, Type.LUSH, Type.NETHER, Type.END, Type.MUSHROOM, Type.MAGICAL, Type.RARE, Type.OCEAN, Type.RIVER, Type.WATER, Type.MESA, Type.FOREST, Type.PLAINS, Type.MOUNTAIN, Type.HILLS, Type.SWAMP, Type.SANDY, Type.SNOWY, Type.WASTELAND, Type.BEACH, Type.VOID};
   }

   public ConfigSpawning.SpawnTypeSet getTypes(String category, String key) {
      return this.getTypes(category, key, "");
   }

   public ConfigSpawning.SpawnTypeSet getTypes(String category, String key, String defaultValue) {
      return this.getTypes(category, key, defaultValue, (String)null);
   }

   public ConfigSpawning.SpawnTypeSet getTypes(String category, String key, String defaultValue, String comment) {
      String spawnTypeEntries = this.getString(category, key, defaultValue);
      spawnTypeEntries = spawnTypeEntries.replace(" ", "");
      String[] spawners = spawnTypeEntries.split(",");
      List<EnumCreatureType> creatureTypeList = new ArrayList();

      for(String spawnTypeEntry : spawnTypeEntries.split(",")) {
         if ("MONSTER".equalsIgnoreCase(spawnTypeEntry)) {
            creatureTypeList.add(EnumCreatureType.MONSTER);
         } else if (!"CREATURE".equalsIgnoreCase(spawnTypeEntry) && !"ANIMAL".equalsIgnoreCase(spawnTypeEntry)) {
            if ("WATERCREATURE".equalsIgnoreCase(spawnTypeEntry)) {
               creatureTypeList.add(EnumCreatureType.WATER_CREATURE);
            } else if ("AMBIENT".equalsIgnoreCase(spawnTypeEntry)) {
               creatureTypeList.add(EnumCreatureType.AMBIENT);
            }
         } else {
            creatureTypeList.add(EnumCreatureType.CREATURE);
         }
      }

      EnumCreatureType[] creatureTypes = (EnumCreatureType[])creatureTypeList.toArray(new EnumCreatureType[creatureTypeList.size()]);
      return new ConfigSpawning.SpawnTypeSet(spawners, creatureTypes);
   }

   public ConfigSpawning.SpawnDimensionSet getDimensions(String category, String key) {
      return this.getDimensions(category, key, "");
   }

   public ConfigSpawning.SpawnDimensionSet getDimensions(String category, String key, String defaultValue) {
      return this.getDimensions(category, key, defaultValue, (String)null);
   }

   public ConfigSpawning.SpawnDimensionSet getDimensions(String category, String key, String defaultValue, String comment) {
      String dimensionEntries = this.getString(category, key, defaultValue);
      dimensionEntries = dimensionEntries.replace(" ", "");
      List<Integer> dimensionIDList = new ArrayList();
      List<String> dimensionTypeList = new ArrayList();

      for(String dimensionEntry : dimensionEntries.split(",")) {
         if (NumberUtils.isCreatable(dimensionEntry)) {
            dimensionIDList.add(Integer.valueOf(Integer.parseInt(dimensionEntry.replace("+", ""))));
         } else {
            dimensionTypeList.add(dimensionEntry.replace("+", ""));
         }
      }

      int[] dimensionIDs = ArrayUtils.toPrimitive((Integer[])dimensionIDList.toArray(new Integer[dimensionIDList.size()]));
      String[] dimensionTypes = (String[])dimensionTypeList.toArray(new String[dimensionTypeList.size()]);
      return new ConfigSpawning.SpawnDimensionSet(dimensionIDs, dimensionTypes);
   }

   public Biome[] getBiomes(String category, String key) {
      return this.getBiomes(category, key, "");
   }

   public Biome[] getBiomes(String category, String key, String defaultValue) {
      return this.getBiomes(category, key, defaultValue, (String)null);
   }

   public Biome[] getBiomes(String category, String key, String defaultValue, String comment) {
      String biomeEntries = this.getString(category, key, defaultValue, comment);
      biomeEntries = biomeEntries.replace(" ", "");
      List<Biome> biomeList = new ArrayList();

      for(String biomeEntry : biomeEntries.split(",")) {
         if (!"".equals(biomeEntry)) {
            boolean additive = true;
            if (biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
               if (biomeEntry.charAt(0) == '-') {
                  additive = false;
               }

               biomeEntry = biomeEntry.substring(1);
            }

            Biome[] selectedBiomes = null;
            if ("ALL".equals(biomeEntry)) {
               for(Type biomeType : this.getAllBiomeTypes()) {
                  if (selectedBiomes == null) {
                     Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
                     selectedBiomes = (Biome[])selectedBiomesSet.toArray(new Biome[selectedBiomesSet.size()]);
                  } else {
                     Set<Biome> typeBiomesSet = BiomeDictionary.getBiomes(biomeType);
                     Biome[] typeBiomes = (Biome[])typeBiomesSet.toArray(new Biome[typeBiomesSet.size()]);
                     if (typeBiomes != null) {
                        selectedBiomes = (Biome[])ArrayUtils.addAll(selectedBiomes, typeBiomes);
                     }
                  }
               }
            } else if ("GROUP".equals(biomeEntry)) {
               selectedBiomes = this.group.biomes;
            } else if (!"NONE".equals(biomeEntry)) {
               Type biomeType;
               try {
                  biomeType = Type.getType(biomeEntry, new Type[0]);
               } catch (Exception var19) {
                  biomeType = null;
                  LycanitesMobs.logWarning("", "[Config] Unknown biome type " + biomeEntry + " specified for " + defaultValue + "this will be ignored and treated as NONE.");
               }

               if (biomeType != null) {
                  Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
                  selectedBiomes = (Biome[])selectedBiomesSet.toArray(new Biome[selectedBiomesSet.size()]);
               }
            }

            if (selectedBiomes != null) {
               for(Biome biome : selectedBiomes) {
                  if (additive && !biomeList.contains(biome)) {
                     biomeList.add(biome);
                  } else if (!additive && biomeList.contains(biome)) {
                     biomeList.remove(biome);
                  }
               }
            }
         }
      }

      return (Biome[])biomeList.toArray(new Biome[biomeList.size()]);
   }

   public Type[] getBiomeTypes(boolean additive, String category, String key) {
      return this.getBiomeTypes(additive, category, key, "");
   }

   public Type[] getBiomeTypes(boolean additive, String category, String key, String defaultValue) {
      return this.getBiomeTypes(additive, category, key, defaultValue, (String)null);
   }

   public Type[] getBiomeTypes(boolean additive, String category, String key, String defaultValue, String comment) {
      String biomeEntries = this.getString(category, key, defaultValue, comment);
      biomeEntries = biomeEntries.replace(" ", "");
      List<Type> biomeTypeList = new ArrayList();

      for(String biomeEntry : biomeEntries.split(",")) {
         if (!"".equals(biomeEntry)) {
            if (biomeEntry.charAt(0) != '-' && biomeEntry.charAt(0) != '+') {
               if (!additive) {
                  continue;
               }
            } else {
               if (biomeEntry.charAt(0) == '-' && additive || biomeEntry.charAt(0) == '+' && !additive) {
                  continue;
               }

               biomeEntry = biomeEntry.substring(1);
            }

            if ("ALL".equals(biomeEntry)) {
               biomeTypeList.addAll(Arrays.asList(this.getAllBiomeTypes()));
            } else if ("GROUP".equals(biomeEntry)) {
               if (additive) {
                  biomeTypeList.addAll(Arrays.asList(this.group.biomeTypesAllowed));
               } else {
                  biomeTypeList.addAll(Arrays.asList(this.group.biomeTypesDenied));
               }
            } else if (!"NONE".equals(biomeEntry)) {
               Type biomeType;
               try {
                  biomeType = Type.getType(biomeEntry, new Type[0]);
               } catch (Exception var14) {
                  biomeType = null;
                  LycanitesMobs.logWarning("", "[Config] Unknown biome type " + biomeEntry + " specified for " + defaultValue + "this will be ignored and treated as NONE.");
               }

               if (biomeType != null) {
                  biomeTypeList.add(biomeType);
               }
            }
         }
      }

      return (Type[])biomeTypeList.toArray(new Type[biomeTypeList.size()]);
   }

   public class SpawnDimensionSet {
      public int[] dimensionIDs;
      public String[] dimensionTypes;

      public SpawnDimensionSet(int[] dimensionIDs, String[] dimensionTypes) {
         this.dimensionIDs = dimensionIDs;
         this.dimensionTypes = dimensionTypes;
      }
   }

   public class SpawnTypeSet {
      public String[] spawners;
      public EnumCreatureType[] creatureTypes;

      public SpawnTypeSet(String[] spawners, EnumCreatureType[] creatureTypes) {
         this.spawners = spawners;
         this.creatureTypes = creatureTypes;
      }
   }
}
