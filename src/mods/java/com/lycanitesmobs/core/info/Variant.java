package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class Variant {
   public static int BASE_WEIGHT = 400;
   public static Map<String, Integer> COMMON_WEIGHTS = new HashMap<String, Integer>() {
      {
         this.put("common", Integer.valueOf(100));
         this.put("uncommon", Integer.valueOf(20));
         this.put("rare", Integer.valueOf(2));
         this.put("legendary", Integer.valueOf(1));
      }
   };
   public static Map<String, Double> STAT_MULTIPLIERS = new HashMap();
   public static int UNCOMMON_DROP_SCALE = 2;
   public static int RARE_DROP_SCALE = 5;
   public static double UNCOMMON_EXPERIENCE_SCALE = 2.0D;
   public static double RARE_EXPERIENCE_SCALE = 10.0D;
   public static int UNCOMMON_SPAWN_DAY_MIN = 0;
   public static int RARE_SPAWN_DAY_MIN = 0;
   public static boolean RARE_DESPAWNING = false;
   public static boolean RARE_HEALTH_BARS = false;
   public int index;
   public double scale = 1.0D;
   public String color;
   public String rarity = "uncommon";
   public int weight;

   public static void loadGlobalSettings(ConfigBase config) {
      BASE_WEIGHT = config.getInt("Mob Variations", "Variant Base Weight", BASE_WEIGHT, "The weight of base variants (regular mob colors).");
      COMMON_WEIGHTS.put("uncommon", Integer.valueOf(config.getInt("Mob Variations", "Variant Uncommon Weight", ((Integer)COMMON_WEIGHTS.get("uncommon")).intValue(), "The weight of uncommon variants (such as Azure, Verdant, Scarlet, etc).")));
      COMMON_WEIGHTS.put("rare", Integer.valueOf(config.getInt("Mob Variations", "Variant Rare Weight", ((Integer)COMMON_WEIGHTS.get("rare")).intValue(), "The weight of rare variants (such as Lunar or Celestial).")));
      String[] subspeciesNames = new String[]{"uncommon", "rare"};
      STAT_MULTIPLIERS = new HashMap();
      boolean foundOldStatsCategory = config.setCategoryReplacedComment("Subspecies Multipliers", "Variant Multipliers");
      config.setCategoryComment("Variant Multipliers", "Here you can scale the stats of every mob on a per variant basis.");

      for(String subspeciesName : subspeciesNames) {
         for(String statName : CreatureStats.STAT_NAMES) {
            double defaultValue = 1.0D;
            if ("uncommon".equals(subspeciesName) && "health".equals(statName)) {
               defaultValue = 2.0D;
            }

            if ("rare".equals(subspeciesName)) {
               if ("health".equals(statName)) {
                  defaultValue = 20.0D;
               } else if ("attackSpeed".equals(statName)) {
                  defaultValue = 2.0D;
               } else if ("rangedSpeed".equals(statName)) {
                  defaultValue = 2.0D;
               } else if ("effect".equals(statName)) {
                  defaultValue = 2.0D;
               }
            }

            if (foundOldStatsCategory) {
               defaultValue = config.getDouble("Subspecies Multipliers", subspeciesName + " " + statName, defaultValue);
            }

            STAT_MULTIPLIERS.put((subspeciesName + "-" + statName).toUpperCase(Locale.ENGLISH), Double.valueOf(config.getDouble("Variant Multipliers", subspeciesName + " " + statName, defaultValue)));
         }
      }

      UNCOMMON_DROP_SCALE = config.getInt("Mob Variations", "Variant Uncommon Item Drops Scale", UNCOMMON_DROP_SCALE, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its item drops amount is multiplied by this value.");
      RARE_DROP_SCALE = config.getInt("Mob Variations", "Variant Rare Item Drops Scale", RARE_DROP_SCALE, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its item drops amount is multiplied by this value.");
      UNCOMMON_EXPERIENCE_SCALE = config.getDouble("Mob Variations", "Variant Uncommon Experience Scale", UNCOMMON_EXPERIENCE_SCALE, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its experience amount is multiplied by this value.");
      RARE_EXPERIENCE_SCALE = config.getDouble("Mob Variations", "Variant Rare Experience Scale", RARE_EXPERIENCE_SCALE, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its experience amount is multiplied by this value.");
      UNCOMMON_SPAWN_DAY_MIN = config.getInt("Mob Variations", "Variant Uncommon Spawn Day Min", UNCOMMON_SPAWN_DAY_MIN, "The minimum amount of days before uncommon species start to spawn.");
      RARE_SPAWN_DAY_MIN = config.getInt("Mob Variations", "Variant Rare Spawn Day Min", RARE_SPAWN_DAY_MIN, "The minimum amount of days before rare species start to spawn.");
      RARE_DESPAWNING = config.getBool("Mob Variations", "Variant Rare Despawning", RARE_DESPAWNING, "If set to true, rare subspecies such as the Lunar Grue will despawn naturally over time.");
      RARE_HEALTH_BARS = config.getBool("Mob Variations", "Variant Rare Health Bars", RARE_HEALTH_BARS, "If set to true, rare subspecies such as the Lunar Grue or Celestial Geonach will display boss health bars.");
   }

   public static Variant createFromJSON(CreatureInfo creatureInfo, JsonObject json) {
      String rarity = "uncommon";
      if (json.has("rarity")) {
         rarity = json.get("rarity").getAsString().toLowerCase();
      } else if (json.has("type")) {
         rarity = json.get("type").getAsString().toLowerCase();
      }

      String color = null;
      if (json.has("color")) {
         color = json.get("color").getAsString().toLowerCase();
      } else if (json.has("name")) {
         color = json.get("name").getAsString().toLowerCase();
      }

      if (color == null) {
         throw new RuntimeException("Invalid subspecies variant added with no color defined!");
      } else {
         Variant variant = new Variant(color, rarity, json.get("index").getAsInt());
         if (json.has("scale")) {
            variant.scale = json.get("scale").getAsDouble();
         }

         return variant;
      }
   }

   public Variant(String color, String rarity, int index) {
      this.weight = BASE_WEIGHT;
      this.color = color;
      this.rarity = rarity;
      this.index = index;
      this.weight = ((Integer)COMMON_WEIGHTS.get(rarity)).intValue();
   }

   public String getTitle() {
      return LanguageManager.translate("subspecies." + this.color + ".name");
   }

   public double getScale() {
      return this.scale;
   }

   public boolean canSpawn(EntityLivingBase entityLiving) {
      if (entityLiving != null) {
         World world = entityLiving.getEntityWorld();
         int day = (int)Math.floor((double)world.getTotalWorldTime() / 23999.0D);
         int spawnDayMin = 0;
         if ("uncommon".equalsIgnoreCase(this.rarity)) {
            spawnDayMin = UNCOMMON_SPAWN_DAY_MIN;
         } else if ("rare".equalsIgnoreCase(this.rarity)) {
            spawnDayMin = RARE_SPAWN_DAY_MIN;
         }

         if (day < spawnDayMin) {
            return false;
         }
      }

      return true;
   }

   public String toString() {
      return this.color + " - " + this.weight;
   }

   public static int getIndexFromOld(int oldIndex) {
      return oldIndex % 4;
   }
}
