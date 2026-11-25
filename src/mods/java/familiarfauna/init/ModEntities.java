package familiarfauna.init;

import com.google.common.collect.Maps;
import familiarfauna.config.ConfigurationHandler;
import familiarfauna.core.FamiliarFauna;
import familiarfauna.entities.EntityButterfly;
import familiarfauna.entities.EntityDeer;
import familiarfauna.entities.EntityDragonfly;
import familiarfauna.entities.EntityPixie;
import familiarfauna.entities.EntitySnail;
import familiarfauna.entities.EntityTurkey;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

public class ModEntities {
   public static final Map<Integer, EntityEggInfo> entityEggs = Maps.newLinkedHashMap();
   public static final Map<Integer, String> idToBEEntityName = Maps.newLinkedHashMap();
   private static int nextFFEntityId = 1;

   public ModEntities() {
   }

   public static void init() {
      registerFFEntityWithSpawnEgg(EntityButterfly.class, "familiarfauna.butterfly", 80, 3, true, 2631720, 15691551, EnumCreatureType.AMBIENT, ConfigurationHandler.butterflyWeight, ConfigurationHandler.butterflyMin, ConfigurationHandler.butterflyMax, ModConfiguration.butterflyBiomeList);
      if (ConfigurationHandler.deerReplaceCows) {
         removeSpawn(EntityCow.class, EnumCreatureType.CREATURE, ModConfiguration.deerBiomeList);
      }

      registerFFEntityWithSpawnEgg(EntityDeer.class, "familiarfauna.deer", 80, 3, true, 7754036, 16248806, EnumCreatureType.CREATURE, ConfigurationHandler.deerWeight, ConfigurationHandler.deerMin, ConfigurationHandler.deerMax, ModConfiguration.deerBiomeList);
      registerFFEntityWithSpawnEgg(EntityDragonfly.class, "familiarfauna.dragonfly", 80, 3, true, 3424365, 5349836, EnumCreatureType.AMBIENT, ConfigurationHandler.dragonflyWeight, ConfigurationHandler.dragonflyMin, ConfigurationHandler.dragonflyMax, ModConfiguration.dragonflyBiomeList);
      registerFFEntityWithSpawnEgg(EntityPixie.class, "familiarfauna.pixie", 80, 3, true, 16751081, 16777215, EnumCreatureType.AMBIENT, ConfigurationHandler.pixieWeight, ConfigurationHandler.pixieMin, ConfigurationHandler.pixieMax, ModConfiguration.pixieBiomeList);
      registerFFEntityWithSpawnEgg(EntitySnail.class, "familiarfauna.snail", 80, 3, true, 10917052, 13476462, EnumCreatureType.AMBIENT, ConfigurationHandler.snailWeight, ConfigurationHandler.snailMin, ConfigurationHandler.snailMax, ModConfiguration.snailBiomeList);
      if (ConfigurationHandler.turkeyReplaceChickens) {
         removeSpawn(EntityChicken.class, EnumCreatureType.CREATURE, ModConfiguration.turkeyBiomeList);
      }

      registerFFEntityWithSpawnEgg(EntityTurkey.class, "familiarfauna.turkey", 80, 3, true, 7031086, 14823729, EnumCreatureType.CREATURE, ConfigurationHandler.turkeyWeight, ConfigurationHandler.turkeyMin, ConfigurationHandler.turkeyMax, ModConfiguration.turkeyBiomeList);
   }

   public static int registerFFEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
      int ffEntityId = nextFFEntityId++;
      EntityRegistry.registerModEntity(new ResourceLocation("familiarfauna", entityName), entityClass, entityName, ffEntityId, FamiliarFauna.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
      idToBEEntityName.put(Integer.valueOf(ffEntityId), entityName);
      return ffEntityId;
   }

   public static int registerFFEntityWithSpawnEgg(Class<? extends EntityLiving> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggBackgroundColor, int eggForegroundColor, EnumCreatureType enumCreatureType, int spawnWeight, int spawnMin, int spawnMax, List<String> biomes) {
      int ffEntityId = registerFFEntity(entityClass, entityName, trackingRange, updateFrequency, sendsVelocityUpdates);
      EntityRegistry.registerEgg(new ResourceLocation("familiarfauna", entityName), eggBackgroundColor, eggForegroundColor);
      addSpawn(entityClass, spawnWeight, spawnMin, spawnMax, enumCreatureType, biomes);
      return ffEntityId;
   }

   public static Entity createEntityByID(int tanEntityId, World worldIn) {
      Entity entity = null;
      ModContainer mc = FMLCommonHandler.instance().findContainerFor(FamiliarFauna.instance);
      EntityRegistration er = EntityRegistry.instance().lookupModSpawn(mc, tanEntityId);
      if (er != null) {
         Class<? extends Entity> clazz = er.getEntityClass();

         try {
            if (clazz != null) {
               entity = (Entity)clazz.getConstructor(World.class).newInstance(worldIn);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      if (entity == null) {
         FamiliarFauna.logger.warn("Skipping FF Entity with id " + tanEntityId);
      }

      return entity;
   }

   public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, List<String> biomes) {
      for(String biomeName : biomes) {
         ResourceLocation loc = new ResourceLocation(biomeName);
         if (ForgeRegistries.BIOMES.containsKey(loc)) {
            Biome biome = (Biome)ForgeRegistries.BIOMES.getValue(loc);
            List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);
            boolean found = false;

            for(SpawnListEntry entry : spawns) {
               if (entry.entityClass == entityClass) {
                  entry.itemWeight = weightedProb;
                  entry.minGroupCount = min;
                  entry.maxGroupCount = max;
                  found = true;
                  break;
               }
            }

            if (!found) {
               spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
            }
         }
      }

   }

   public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType typeOfCreature, List<String> biomes) {
      for(String biomeName : biomes) {
         ResourceLocation loc = new ResourceLocation(biomeName);
         if (ForgeRegistries.BIOMES.containsKey(loc)) {
            Biome biome = (Biome)ForgeRegistries.BIOMES.getValue(loc);
            Iterator<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature).iterator();

            while(spawns.hasNext()) {
               SpawnListEntry entry = (SpawnListEntry)spawns.next();
               if (entry.entityClass == entityClass) {
                  spawns.remove();
               }
            }
         }
      }

   }
}
