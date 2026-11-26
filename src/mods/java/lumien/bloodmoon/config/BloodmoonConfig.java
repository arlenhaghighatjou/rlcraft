package lumien.bloodmoon.config;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

@Config(
   modid = "bloodmoon",
   category = "bloodmoon"
)
public class BloodmoonConfig {
   public static BloodmoonConfig.General GENERAL = new BloodmoonConfig.General();
   @Name("appearance")
   public static BloodmoonConfig.Appearance APPEARANCE = new BloodmoonConfig.Appearance();
   @Name("schedule")
   public static BloodmoonConfig.Schedule SCHEDULE = new BloodmoonConfig.Schedule();
   @Name("spawning")
   public static BloodmoonConfig.Spawning SPAWNING = new BloodmoonConfig.Spawning();
   static HashMap<String, String> classToEntityNameMap = new HashMap();

   public BloodmoonConfig() {
   }

   public static boolean canSpawn(Class<? extends Entity> entityClass) {
      if (SPAWNING.SPAWN_WHITELIST.length == 0) {
         if (SPAWNING.SPAWN_BLACKLIST.length == 0) {
            return true;
         } else {
            String className = entityClass.getName();
            String entityName;
            if (classToEntityNameMap.containsKey(className)) {
               entityName = (String)classToEntityNameMap.get(className);
            } else {
               entityName = getEntityName(entityClass);
               classToEntityNameMap.put(className, entityName);
            }

            for(int i = 0; i < SPAWNING.SPAWN_BLACKLIST.length; ++i) {
               if (SPAWNING.SPAWN_BLACKLIST[i].equals(entityName)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         String className = entityClass.getName();
         String entityName;
         if (classToEntityNameMap.containsKey(className)) {
            entityName = (String)classToEntityNameMap.get(className);
         } else {
            entityName = getEntityName(entityClass);
            classToEntityNameMap.put(className, entityName);
         }

         for(int i = 0; i < SPAWNING.SPAWN_WHITELIST.length; ++i) {
            if (SPAWNING.SPAWN_WHITELIST[i].equals(entityName)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getEntityName(Class<? extends Entity> entityClass) {
      String entityName = null;
      entityName = EntityList.getTranslationName(EntityList.getKey(entityClass));
      if (entityName == null) {
         EntityRegistration registration = EntityRegistry.instance().lookupModSpawn(entityClass, false);
         if (registration != null) {
            entityName = registration.getEntityName();
         }
      }

      return entityName;
   }

   public static class Appearance {
      @Name("RedMoon")
      public boolean RED_MOON = true;
      @Name("RedSky")
      public boolean RED_SKY = true;
      @Name("RedLight")
      public boolean RED_LIGHT = true;
      @Name("BlackFog")
      public boolean BLACK_FOG = true;

      public Appearance() {
      }
   }

   public static class General {
      @Name("NoSleep")
      @Comment({"Whether players are not able to sleep on a bloodmoon"})
      public boolean NO_SLEEP = true;
      @Name("Vanish")
      @Comment({"Whether monsters spawned by a bloodmoon should die at dawn"})
      public boolean VANISH = false;
      @Name("RespectGamerule")
      @Comment({"Whether bloodmoons should respect the doMobSpawning gamerule"})
      public boolean RESPECT_GAMERULE = true;
      @Name("SendMessage")
      @Comment({"Whether all players in the overworld should receive a message when the bloodmoon starts"})
      public boolean SEND_MESSAGE = true;

      public General() {
      }
   }

   public static class Schedule {
      @Name("Chance")
      @Comment({"The chance of a bloodmoon occuring at the beginning of a night (0=Never;1=Every night;0.05=5% of all nights)"})
      public double CHANCE = 0.05D;
      @Name("Fullmoon")
      @Comment({"Whether there should be a bloodmoon whenever there is a full moon"})
      public boolean FULLMOON = false;
      @Name("NthNight")
      @Comment({"Every nth night there will be a bloodmoon (0 disables this, 1 would be every night, 2 every second night)"})
      public int NTH_NIGHT = 0;

      public Schedule() {
      }
   }

   public static class Spawning {
      @Name("SpawnSpeed")
      @Comment({"How much faster enemys spawn on a bloodmoon (0=Vanilla)"})
      public int SPAWN_SPEED = 4;
      @Name("SpawnLimitMultiplier")
      @Comment({"With which number should the default entity limit be multiplicated on a blood moon"})
      public int SPAWN_LIMIT_MULT = 4;
      @Name("SpawnRange")
      @Comment({"How close can enemys spawn next to the player on a bloodmoon in blocks? (Vanilla=24)"})
      public int SPAWN_RANGE = 2;
      @Name("WorldSpawnDistance")
      @Comment({"How close can enemys spawn next to the World Spawn (Vanilla=24)"})
      public int SPAWN_DISTANCE = 24;
      @Name("SpawnWhitelist")
      @Comment({"If this isn't empty only monsters which names are in this list will get spawned by the bloodmoon. (Example: \"Skeleton,Spider\")"})
      public String[] SPAWN_WHITELIST = new String[0];
      @Name("SpawnBlacklist")
      @Comment({"Monsters which names are on this list won't get spawned by the bloodmoon (Has no effect when a whitelist is active). (Example: \"Skeleton,Spider\")"})
      public String[] SPAWN_BLACKLIST = new String[0];

      public Spawning() {
      }
   }

   public static void register() {
      ConfigManager.register("bloodmoon", "", "bloodmoon", null,
         ConfigEntry.category("GENERAL",
            ConfigEntry.value("NoSleep", boolean.class, () -> BloodmoonConfig.GENERAL.NO_SLEEP, v -> BloodmoonConfig.GENERAL.NO_SLEEP = (Boolean)v).comment("Whether players are not able to sleep on a bloodmoon").langKey("bloodmoon.bloodmoon.general.no_sleep"),
            ConfigEntry.value("Vanish", boolean.class, () -> BloodmoonConfig.GENERAL.VANISH, v -> BloodmoonConfig.GENERAL.VANISH = (Boolean)v).comment("Whether monsters spawned by a bloodmoon should die at dawn").langKey("bloodmoon.bloodmoon.general.vanish"),
            ConfigEntry.value("RespectGamerule", boolean.class, () -> BloodmoonConfig.GENERAL.RESPECT_GAMERULE, v -> BloodmoonConfig.GENERAL.RESPECT_GAMERULE = (Boolean)v).comment("Whether bloodmoons should respect the doMobSpawning gamerule").langKey("bloodmoon.bloodmoon.general.respect_gamerule"),
            ConfigEntry.value("SendMessage", boolean.class, () -> BloodmoonConfig.GENERAL.SEND_MESSAGE, v -> BloodmoonConfig.GENERAL.SEND_MESSAGE = (Boolean)v).comment("Whether all players in the overworld should receive a message when the bloodmoon starts").langKey("bloodmoon.bloodmoon.general.send_message")),
         ConfigEntry.category("appearance",
            ConfigEntry.value("RedMoon", boolean.class, () -> BloodmoonConfig.APPEARANCE.RED_MOON, v -> BloodmoonConfig.APPEARANCE.RED_MOON = (Boolean)v).langKey("bloodmoon.bloodmoon.appearance.red_moon"),
            ConfigEntry.value("RedSky", boolean.class, () -> BloodmoonConfig.APPEARANCE.RED_SKY, v -> BloodmoonConfig.APPEARANCE.RED_SKY = (Boolean)v).langKey("bloodmoon.bloodmoon.appearance.red_sky"),
            ConfigEntry.value("RedLight", boolean.class, () -> BloodmoonConfig.APPEARANCE.RED_LIGHT, v -> BloodmoonConfig.APPEARANCE.RED_LIGHT = (Boolean)v).langKey("bloodmoon.bloodmoon.appearance.red_light"),
            ConfigEntry.value("BlackFog", boolean.class, () -> BloodmoonConfig.APPEARANCE.BLACK_FOG, v -> BloodmoonConfig.APPEARANCE.BLACK_FOG = (Boolean)v).langKey("bloodmoon.bloodmoon.appearance.black_fog")).langKey("bloodmoon.bloodmoon.appearance"),
         ConfigEntry.category("schedule",
            ConfigEntry.value("Chance", double.class, () -> BloodmoonConfig.SCHEDULE.CHANCE, v -> BloodmoonConfig.SCHEDULE.CHANCE = (Double)v).comment("The chance of a bloodmoon occuring at the beginning of a night (0=Never;1=Every night;0.05=5% of all nights)").langKey("bloodmoon.bloodmoon.schedule.chance"),
            ConfigEntry.value("Fullmoon", boolean.class, () -> BloodmoonConfig.SCHEDULE.FULLMOON, v -> BloodmoonConfig.SCHEDULE.FULLMOON = (Boolean)v).comment("Whether there should be a bloodmoon whenever there is a full moon").langKey("bloodmoon.bloodmoon.schedule.fullmoon"),
            ConfigEntry.value("NthNight", int.class, () -> BloodmoonConfig.SCHEDULE.NTH_NIGHT, v -> BloodmoonConfig.SCHEDULE.NTH_NIGHT = (Integer)v).comment("Every nth night there will be a bloodmoon (0 disables this, 1 would be every night, 2 every second night)").langKey("bloodmoon.bloodmoon.schedule.nth_night")).langKey("bloodmoon.bloodmoon.schedule"),
         ConfigEntry.category("spawning",
            ConfigEntry.value("SpawnSpeed", int.class, () -> BloodmoonConfig.SPAWNING.SPAWN_SPEED, v -> BloodmoonConfig.SPAWNING.SPAWN_SPEED = (Integer)v).comment("How much faster enemys spawn on a bloodmoon (0=Vanilla)").langKey("bloodmoon.bloodmoon.spawning.spawn_speed"),
            ConfigEntry.value("SpawnLimitMultiplier", int.class, () -> BloodmoonConfig.SPAWNING.SPAWN_LIMIT_MULT, v -> BloodmoonConfig.SPAWNING.SPAWN_LIMIT_MULT = (Integer)v).comment("With which number should the default entity limit be multiplicated on a blood moon").langKey("bloodmoon.bloodmoon.spawning.spawn_limit_mult"),
            ConfigEntry.value("SpawnRange", int.class, () -> BloodmoonConfig.SPAWNING.SPAWN_RANGE, v -> BloodmoonConfig.SPAWNING.SPAWN_RANGE = (Integer)v).comment("How close can enemys spawn next to the player on a bloodmoon in blocks? (Vanilla=24)").langKey("bloodmoon.bloodmoon.spawning.spawn_range"),
            ConfigEntry.value("WorldSpawnDistance", int.class, () -> BloodmoonConfig.SPAWNING.SPAWN_DISTANCE, v -> BloodmoonConfig.SPAWNING.SPAWN_DISTANCE = (Integer)v).comment("How close can enemys spawn next to the World Spawn (Vanilla=24)").langKey("bloodmoon.bloodmoon.spawning.spawn_distance"),
            ConfigEntry.value("SpawnWhitelist", String[].class, () -> BloodmoonConfig.SPAWNING.SPAWN_WHITELIST, v -> BloodmoonConfig.SPAWNING.SPAWN_WHITELIST = (String[])v).comment("If this isn't empty only monsters which names are in this list will get spawned by the bloodmoon. (Example: \"Skeleton,Spider\")").langKey("bloodmoon.bloodmoon.spawning.spawn_whitelist"),
            ConfigEntry.value("SpawnBlacklist", String[].class, () -> BloodmoonConfig.SPAWNING.SPAWN_BLACKLIST, v -> BloodmoonConfig.SPAWNING.SPAWN_BLACKLIST = (String[])v).comment("Monsters which names are on this list won't get spawned by the bloodmoon (Has no effect when a whitelist is active). (Example: \"Skeleton,Spider\")").langKey("bloodmoon.bloodmoon.spawning.spawn_blacklist")).langKey("bloodmoon.bloodmoon.spawning"));
   }
}
