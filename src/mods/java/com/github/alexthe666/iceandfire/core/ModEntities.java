package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ChickenEntityProperties;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.MiscPlayerProperties;
import com.github.alexthe666.iceandfire.entity.SirenEntityProperties;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import java.util.List;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ModEntities {
   public ModEntities() {
   }

   public static void init() {
      EntityPropertiesHandler.INSTANCE.registerProperties(StoneEntityProperties.class);
      EntityPropertiesHandler.INSTANCE.registerProperties(MiscPlayerProperties.class);
      EntityPropertiesHandler.INSTANCE.registerProperties(FrozenEntityProperties.class);
      EntityPropertiesHandler.INSTANCE.registerProperties(SirenEntityProperties.class);
      EntityPropertiesHandler.INSTANCE.registerProperties(ChickenEntityProperties.class);
      if (IceAndFire.CONFIG.spawnHippogryphs) {
         for(EnumHippogryphTypes type : EnumHippogryphTypes.values()) {
            if (!type.developer) {
               for(Biome biome : Biome.REGISTRY) {
                  if (biome != null && BiomeDictionary.hasType(biome, Type.HILLS)) {
                     List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
                     spawnList.add(new SpawnListEntry(EntityHippogryph.class, IceAndFire.CONFIG.hippogryphSpawnRate, 1, 1));
                  }
               }
            }
         }
      }

      if (IceAndFire.CONFIG.spawnDeathWorm) {
         for(Biome biome : Biome.REGISTRY) {
            if (biome != null && BiomeDictionary.hasType(biome, Type.SANDY) && BiomeDictionary.hasType(biome, Type.DRY) && !BiomeDictionary.hasType(biome, Type.BEACH) && !BiomeDictionary.hasType(biome, Type.MESA)) {
               List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
               spawnList.add(new SpawnListEntry(EntityDeathWorm.class, IceAndFire.CONFIG.deathWormSpawnRate, 1, 3));
            }
         }
      }

      if (IceAndFire.CONFIG.spawnTrolls) {
         for(EnumTroll type : EnumTroll.values()) {
            for(Biome biome : Biome.REGISTRY) {
               if (biome != null && BiomeDictionary.hasType(biome, type.spawnBiome)) {
                  List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.MONSTER);
                  spawnList.add(new SpawnListEntry(EntityTroll.class, IceAndFire.CONFIG.trollSpawnRate, 1, 1));
               }
            }
         }
      }

      if (IceAndFire.CONFIG.spawnCockatrices) {
         for(Biome biome : Biome.REGISTRY) {
            if (biome != null && BiomeDictionary.hasType(biome, Type.SAVANNA) && BiomeDictionary.hasType(biome, Type.SPARSE)) {
               List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
               spawnList.add(new SpawnListEntry(EntityCockatrice.class, IceAndFire.CONFIG.cockatriceSpawnRate, 1, 2));
            }
         }
      }

      if (IceAndFire.CONFIG.spawnAmphitheres) {
         for(Biome biome : Biome.REGISTRY) {
            if (biome != null && BiomeDictionary.hasType(biome, Type.JUNGLE)) {
               List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
               spawnList.add(new SpawnListEntry(EntityAmphithere.class, IceAndFire.CONFIG.amphithereSpawnRate, 1, 3));
            }
         }
      }

   }
}
