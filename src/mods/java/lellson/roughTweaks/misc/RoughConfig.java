package lellson.roughTweaks.misc;

import net.minecraftforge.common.config.Configuration;

public class RoughConfig {
   public static final String ITEMS = "items";
   public static final String HEALAMOUNT = "heal_amount";
   public static final String GENERAL = "general";
   public static int salveHealCount;
   public static int plasterHealCount;
   public static int bandageHealCount;
   public static int medikitHealCount;
   public static int medikitEnchantedHealCount;
   public static float salveHealAmount;
   public static float plasterHealAmount;
   public static float bandageHealAmount;
   public static float medikitHealAmount;
   public static float medikitEnchantedHealAmount;
   public static int sleepHealAmount;
   public static boolean healthRegenOff;
   public static int dropChance;

   public RoughConfig(Configuration config) {
      config.load();
      config.addCustomCategoryComment("items", "Defines how often you're able to rightclick the item to heal yourself. Set the use count to 0 to disable the item.");
      salveHealCount = config.getInt("salveUseCount", "items", 2, 0, 32767, "Use count for the salve item");
      plasterHealCount = config.getInt("plasterUseCount", "items", 4, 0, 32767, "Use count for the plaster item");
      bandageHealCount = config.getInt("bandageUseCount", "items", 6, 0, 32767, "Use count for the bandage item");
      medikitHealCount = config.getInt("medikitUseCount", "items", 16, 0, 32767, "Use count for the medikit item");
      medikitEnchantedHealCount = config.getInt("medikitEnchantedUseCount", "items", 16, 0, 32767, "Use count for the enchanted medikit item");
      config.addCustomCategoryComment("heal_amount", "Defines how much health the item restores on rightclick.");
      salveHealAmount = config.getFloat("salveHealAmount", "heal_amount", 1.0F, 1.0F, 32767.0F, "Salve heal amount");
      plasterHealAmount = config.getFloat("plasterHealAmount", "heal_amount", 1.0F, 1.0F, 32767.0F, "Plaster heal amount");
      bandageHealAmount = config.getFloat("bandageHealAmount", "heal_amount", 1.0F, 1.0F, 32767.0F, "Bandage heal amount");
      medikitHealAmount = config.getFloat("medikitHealAmount", "heal_amount", 1.0F, 1.0F, 32767.0F, "Medikit heal amount");
      medikitEnchantedHealAmount = config.getFloat("medikitEnchantedHealAmount", "heal_amount", 1.0F, 1.0F, 32767.0F, "Enchanted Medikit heal amount");
      sleepHealAmount = config.getInt("sleepHealAmount", "general", 2, 0, 32767, "Amount of half hearts sleeping heals. Set to 0 to disable this feature");
      healthRegenOff = config.getBoolean("healthRegenOff", "general", true, "Set to false to prevent this mod from setting the natural health regeneration gamerule to false on entering a world");
      dropChance = config.getInt("dropChance", "general", 30, 0, 32767, "Chance 1 in X that a hostile entity drops either salve, a plaster or a bandage\nSet this to 0 if you want to disable entity drops");
      if (config.hasChanged()) {
         config.save();
      }

   }
}
