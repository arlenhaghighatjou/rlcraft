package lumien.bloodmoon.config;

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
}
