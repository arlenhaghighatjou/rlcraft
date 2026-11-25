package ladysnake.spawnercontrol.config;

import java.util.Arrays;
import java.util.List;
import ladysnake.spawnercontrol.SpawnerControl;
import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigEntry;

public class SpawnerConfig {
   public SpawnerConfig.SpawnConditions spawnConditions = new SpawnerConfig.SpawnConditions();
   public SpawnerConfig.MobLoot mobLoot = new SpawnerConfig.MobLoot();
   public int mobThreshold = 100;
   public boolean incrementOnMobDeath = false;
   public float spawnRateModifier = 1.0F;
   public boolean breakSpawner = true;
   public int xpDropped = 15;
   public int randXpVariation = 15;
   public String[] itemsDropped = new String[0];

   public SpawnerConfig() {
   }

   public ConfigEntry[] entries() {
      return new ConfigEntry[]{
         ConfigEntry.category("spawnConditions", this.spawnConditions.entries()).comment("Regroups config options aiming to alter mob spawners spawning conditions"),
         ConfigEntry.category("mobLoot", this.mobLoot.entries()).comment("Groups config options related to custom drops from mobs spawned by this spawner type"),
         ConfigEntry.value("mobThreshold", int.class, () -> this.mobThreshold, v -> this.mobThreshold = (Integer)v).comment("When a spawner has spawned this number of mobs over this lifetime, it will get broken automatically"),
         ConfigEntry.value("incrementOnMobDeath", boolean.class, () -> this.incrementOnMobDeath, v -> this.incrementOnMobDeath = (Boolean)v).comment("If set to true, spawners will count mobs when they are killed rather than when they are spawned"),
         ConfigEntry.value("spawnRateModifier", float.class, () -> this.spawnRateModifier, v -> this.spawnRateModifier = (Float)v).comment("This multiplier is applied on the spawner's spawn delay each time a mob is spawned. \nCan be used to make mobs spawned exponentially faster or slower."),
         ConfigEntry.value("breakSpawner", boolean.class, () -> this.breakSpawner, v -> this.breakSpawner = (Boolean)v).comment("If set to false, spawners will only be disabled when expired, not broken"),
         ConfigEntry.value("xpDropped", int.class, () -> this.xpDropped, v -> this.xpDropped = (Integer)v).comment("The minimum amount of xp dropped by a spawner when broken"),
         ConfigEntry.value("randXpVariation", int.class, () -> this.randXpVariation, v -> this.randXpVariation = (Integer)v).comment("The formula used to calculate xp dropped is 'xpDropped + rand(this number) + rand(this number)'").range(0, Integer.MAX_VALUE),
         ConfigEntry.value("itemsDropped", String[].class, () -> this.itemsDropped, v -> this.itemsDropped = (String[])v).comment("A list of item ids that a mob spawner drops when broken", "Format: 'modid:item(:count(:meta(:chance)))' (count, meta and chance are optional)")};
   }

   public static class MobLoot {
      public SpawnerConfig.MobLoot.MobLootEntry defaultValues = new SpawnerConfig.MobLoot.MobLootEntry(1.0F, 0, false, new String[0], new String[0]);
      public String[] xpMultipliers = new String[0];
      public String[] itemDropRemovals = new String[0];
      public String[] itemDropAdditions = new String[0];
      public EaglerLoadingCache<ResourceLocation, SpawnerConfig.MobLoot.MobLootEntry> lootEntries = new EaglerLoadingCache<>((rl) -> {
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
      });

      public MobLoot() {
      }

      public ConfigEntry[] entries() {
         return new ConfigEntry[]{
            ConfigEntry.category("defaultValues", this.defaultValues.entries()),
            ConfigEntry.value("xpMultipliers", String[].class, () -> this.xpMultipliers, v -> this.xpMultipliers = (String[])v).comment("Individual xp drop multiplier configuration for mobs spawned by this spawner type", "Format: 'modid:entity:xpMultiplier(:flatXp)' (flatXp is optional)"),
            ConfigEntry.value("itemDropRemovals", String[].class, () -> this.itemDropRemovals, v -> this.itemDropRemovals = (String[])v).comment("Individual item drop removal configuration for mobs spawned by this spawner type", "Format: 'modid:entity(,modid:item(:meta))(,modid:item(:meta))...'", "Anything in parenthesis is optional, and you can enter as many items as you want", "If don't enter any items, all item drops are removed from the mob (itemDropAdditions are added afterwards)"),
            ConfigEntry.value("itemDropAdditions", String[].class, () -> this.itemDropAdditions, v -> this.itemDropAdditions = (String[])v).comment("Individual item drop addition configuration for mobs spawned by this spawner type", "Format: 'modid:entity,modid:item(:count(:meta(:chance)))(,modid:item(:count(:meta(:chance))))...'", "Anything in parenthesis is optional, and you can enter as many items as you want", "Eg: minecraft:skeleton,minecraft:dye:100:15:0.5,minecraft:bone:1:1:1")};
      }

      public static class MobLootEntry {
         public float xpMultiplier;
         public int flatXpIncrease;
         public boolean removeAllItems;
         public String[] removedItems;
         public String[] addedItems;

         public MobLootEntry(float defaultXpMultiplier, int flatXpIncrease, boolean removeAllItems, String[] removedItems, String[] addedItems) {
            this.xpMultiplier = defaultXpMultiplier;
            this.flatXpIncrease = flatXpIncrease;
            this.removeAllItems = removeAllItems;
            this.removedItems = removedItems;
            this.addedItems = addedItems;
         }

         public ConfigEntry[] entries() {
            return new ConfigEntry[]{
               ConfigEntry.value("xpMultiplier", float.class, () -> this.xpMultiplier, v -> this.xpMultiplier = (Float)v).comment("xp drop multiplier for mobs spawned by this spawner type"),
               ConfigEntry.value("flatXpIncrease", int.class, () -> this.flatXpIncrease, v -> this.flatXpIncrease = (Integer)v).comment("Flat xp modifier that will be added to mobs spawned by this spawner type"),
               ConfigEntry.value("removeAllItems", boolean.class, () -> this.removeAllItems, v -> this.removeAllItems = (Boolean)v).comment("Remove all existing item drops from the mobs spawned by this spawner", "'Added Items' are added afterwards"),
               ConfigEntry.value("removedItems", String[].class, () -> this.removedItems, v -> this.removedItems = (String[])v).comment("Which items to remove from the drops of the mobs spawned", "Format: 'modid:item(:meta)' (meta is optional)", "If 'Remove All Items' is true, this does nothing"),
               ConfigEntry.value("addedItems", String[].class, () -> this.addedItems, v -> this.addedItems = (String[])v).comment("Which items to add to the drops of the mobs spawned", "Format: 'modid:item(:count(:meta(:chance)))' (count, meta and chance are optional)")};
         }
      }
   }

   public static class SpawnConditions {
      public boolean forceSpawnerAllSpawns = false;
      public boolean forceSpawnerMobSpawns = false;
      public boolean checkSunlight = true;

      public SpawnConditions() {
      }

      public ConfigEntry[] entries() {
         return new ConfigEntry[]{
            ConfigEntry.value("forceSpawnerAllSpawns", boolean.class, () -> this.forceSpawnerAllSpawns, v -> this.forceSpawnerAllSpawns = (Boolean)v).comment("If set to true, spawners will not run mob-specific checks on any entity"),
            ConfigEntry.value("forceSpawnerMobSpawns", boolean.class, () -> this.forceSpawnerMobSpawns, v -> this.forceSpawnerMobSpawns = (Boolean)v).comment("If set to true, spawners will not run mob-specific checks on hostile mob entities", "This most notably prevents players from disabling a spawner with torches or other light sources"),
            ConfigEntry.value("checkSunlight", boolean.class, () -> this.checkSunlight, v -> this.checkSunlight = (Boolean)v).comment("If forceSpawnerMobSpawns is enabled, will prevent hostile mobs from spawning in daylight")};
      }
   }
}
