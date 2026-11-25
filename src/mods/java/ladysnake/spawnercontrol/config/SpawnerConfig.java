package ladysnake.spawnercontrol.config;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Arrays;
import java.util.List;
import ladysnake.spawnercontrol.SpawnerControl;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.RangeInt;

public class SpawnerConfig {
   @Comment({"Regroups config options aiming to alter mob spawners spawning conditions"})
   public SpawnerConfig.SpawnConditions spawnConditions = new SpawnerConfig.SpawnConditions();
   @Comment({"Groups config options related to custom drops from mobs spawned by this spawner type"})
   public SpawnerConfig.MobLoot mobLoot = new SpawnerConfig.MobLoot();
   @Comment({"When a spawner has spawned this number of mobs over this lifetime, it will get broken automatically"})
   public int mobThreshold = 100;
   @Comment({"If set to true, spawners will count mobs when they are killed rather than when they are spawned"})
   public boolean incrementOnMobDeath = false;
   @Comment({"This multiplier is applied on the spawner's spawn delay each time a mob is spawned. \nCan be used to make mobs spawned exponentially faster or slower."})
   public float spawnRateModifier = 1.0F;
   @Comment({"If set to false, spawners will only be disabled when expired, not broken"})
   public boolean breakSpawner = true;
   @Comment({"The minimum amount of xp dropped by a spawner when broken"})
   public int xpDropped = 15;
   @RangeInt(
      min = 0
   )
   @Comment({"The formula used to calculate xp dropped is 'xpDropped + rand(this number) + rand(this number)'"})
   public int randXpVariation = 15;
   @Comment({"A list of item ids that a mob spawner drops when broken", "Format: 'modid:item(:count(:meta(:chance)))' (count, meta and chance are optional)"})
   public String[] itemsDropped = new String[0];

   public SpawnerConfig() {
   }

   public static class MobLoot {
      public SpawnerConfig.MobLoot.MobLootEntry defaultValues = new SpawnerConfig.MobLoot.MobLootEntry(1.0F, 0, false, new String[0], new String[0]);
      @Comment({"Individual xp drop multiplier configuration for mobs spawned by this spawner type", "Format: 'modid:entity:xpMultiplier(:flatXp)' (flatXp is optional)"})
      public String[] xpMultipliers = new String[0];
      @Comment({"Individual item drop removal configuration for mobs spawned by this spawner type", "Format: 'modid:entity(,modid:item(:meta))(,modid:item(:meta))...'", "Anything in parenthesis is optional, and you can enter as many items as you want", "If don't enter any items, all item drops are removed from the mob (itemDropAdditions are added afterwards)"})
      public String[] itemDropRemovals = new String[0];
      @Comment({"Individual item drop addition configuration for mobs spawned by this spawner type", "Format: 'modid:entity,modid:item(:count(:meta(:chance)))(,modid:item(:count(:meta(:chance))))...'", "Anything in parenthesis is optional, and you can enter as many items as you want", "Eg: minecraft:skeleton,minecraft:dye:100:15:0.5,minecraft:bone:1:1:1"})
      public String[] itemDropAdditions = new String[0];
      @Ignore
      public LoadingCache<ResourceLocation, SpawnerConfig.MobLoot.MobLootEntry> lootEntries = CacheBuilder.newBuilder().build(CacheLoader.from((rl) -> {
         if (rl == null) {
            return this.defaultValues;
         } else {
            float xpMultiplier = this.defaultValues.xpMultiplier;
            int flatXpIncrease = this.defaultValues.flatXpIncrease;
            boolean removeAllItems = this.defaultValues.removeAllItems;
            List<String> removedItems = Arrays.asList(this.defaultValues.removedItems);
            List<String> addedItems = Arrays.asList(this.defaultValues.addedItems);

            for(String s : this.xpMultipliers) {
               String[] split = s.split(":");
               if (split[0].equals(rl.getResourcePath()) && split[1].equals(rl.getResourceDomain())) {
                  try {
                     xpMultiplier = Float.parseFloat(split[2]);
                     flatXpIncrease = split.length > 3 ? Integer.parseInt(split[3]) : this.defaultValues.flatXpIncrease;
                  } catch (NumberFormatException var16) {
                     SpawnerControl.LOGGER.warn("Bad mob spawner loot config option : {}", s);
                  }
                  break;
               }
            }

            for(String s : this.itemDropRemovals) {
               String[] split = s.split(",");
               String[] mobSplit = split[0].split(":");
               if (mobSplit[0].equals(rl.getResourcePath()) && mobSplit[1].equals(rl.getResourceDomain())) {
                  try {
                     removedItems = Arrays.asList(split);
                     removedItems.remove(0);
                     removeAllItems = removedItems.size() == 0;
                  } catch (Exception var15) {
                     SpawnerControl.LOGGER.warn("Bad mob spawner loot config option : {}", s);
                  }
                  break;
               }
            }

            for(String s : this.itemDropAdditions) {
               String[] split = s.split(":");
               if (split[0].equals(rl.getResourcePath()) && split[1].equals(rl.getResourceDomain())) {
                  try {
                     addedItems = Arrays.asList(split);
                     addedItems.remove(0);
                  } catch (Exception var14) {
                     SpawnerControl.LOGGER.warn("Bad mob spawner loot config option : {}", s);
                  }
                  break;
               }
            }

            return new SpawnerConfig.MobLoot.MobLootEntry(xpMultiplier, flatXpIncrease, removeAllItems, (String[])removedItems.toArray(new String[0]), (String[])addedItems.toArray(new String[0]));
         }
      }));

      public MobLoot() {
      }

      public static class MobLootEntry {
         @Comment({"xp drop multiplier for mobs spawned by this spawner type"})
         public float xpMultiplier;
         @Comment({"Flat xp modifier that will be added to mobs spawned by this spawner type"})
         public int flatXpIncrease;
         @Comment({"Remove all existing item drops from the mobs spawned by this spawner", "'Added Items' are added afterwards"})
         public boolean removeAllItems;
         @Comment({"Which items to remove from the drops of the mobs spawned", "Format: 'modid:item(:meta)' (meta is optional)", "If 'Remove All Items' is true, this does nothing"})
         public String[] removedItems;
         @Comment({"Which items to add to the drops of the mobs spawned", "Format: 'modid:item(:count(:meta(:chance)))' (count, meta and chance are optional)"})
         public String[] addedItems;

         public MobLootEntry(float defaultXpMultiplier, int flatXpIncrease, boolean removeAllItems, String[] removedItems, String[] addedItems) {
            this.xpMultiplier = defaultXpMultiplier;
            this.flatXpIncrease = flatXpIncrease;
            this.removeAllItems = removeAllItems;
            this.removedItems = removedItems;
            this.addedItems = addedItems;
         }
      }
   }

   public static class SpawnConditions {
      @Comment({"If set to true, spawners will not run mob-specific checks on any entity"})
      public boolean forceSpawnerAllSpawns = false;
      @Comment({"If set to true, spawners will not run mob-specific checks on hostile mob entities", "This most notably prevents players from disabling a spawner with torches or other light sources"})
      public boolean forceSpawnerMobSpawns = false;
      @Comment({"If forceSpawnerMobSpawns is enabled, will prevent hostile mobs from spawning in daylight"})
      public boolean checkSunlight = true;

      public SpawnConditions() {
      }
   }
}
