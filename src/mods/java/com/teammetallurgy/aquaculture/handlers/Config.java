package com.teammetallurgy.aquaculture.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class Config {
   public static Configuration config;
   public static final String categoryBasic = "BASIC_OPTIONS";
   public static boolean assignRandomWeight;
   public static boolean enableNeptuniumArmor;
   public static boolean enableNeptuniumTools;
   public static boolean enableNeptuniumLoot;
   public static final String categoryFishRarity = "FISH_RARITY";
   public static HashMap<String, Integer> fishRarity = new HashMap();
   public static final String categoryJunkRarity = "JUNK_RARITY";
   public static HashMap<String, Integer> junkRarity = new HashMap();

   public Config() {
   }

   public static void init(File file) {
      if (config == null) {
         config = new Configuration(file);
         loadConfiguration();
      }

   }

   private static void loadConfiguration() {
      assignRandomWeight = config.get("BASIC_OPTIONS", "Enable fish weight", true).getBoolean();
      enableNeptuniumArmor = config.get("BASIC_OPTIONS", "Enable Neptunium armor", true).getBoolean();
      enableNeptuniumTools = config.get("BASIC_OPTIONS", "Enable Neptunium tools", true).getBoolean();
      enableNeptuniumLoot = config.get("BASIC_OPTIONS", "Enable Neptunium loot", true).getBoolean();

      for(Entry<String, Integer> entry : fishRarity.entrySet()) {
         int rarity = ((Integer)entry.getValue()).intValue();
         String name = ((String)entry.getKey()).replace(' ', '_').toLowerCase(Locale.US);
         rarity = config.getInt(name, "FISH_RARITY", rarity, 1, 100, "");
         entry.setValue(Integer.valueOf(rarity));
      }

      for(Entry<String, Integer> entry : junkRarity.entrySet()) {
         int rarity = ((Integer)entry.getValue()).intValue();
         String name = ((String)entry.getKey()).replace(' ', '_').toLowerCase(Locale.US);
         rarity = config.getInt(name, "JUNK_RARITY", rarity, 1, 100, "");
         entry.setValue(Integer.valueOf(rarity));
      }

      if (config.hasChanged()) {
         config.save();
      }

   }

   @SubscribeEvent
   public static void onConfigurationChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase("aquaculture")) {
         loadConfiguration();
      }

   }

   static {
      fishRarity.put("Bluegill", Integer.valueOf(30));
      fishRarity.put("Perch", Integer.valueOf(30));
      fishRarity.put("Gar", Integer.valueOf(20));
      fishRarity.put("Bass", Integer.valueOf(10));
      fishRarity.put("Muskellunge", Integer.valueOf(10));
      fishRarity.put("Brown Trout", Integer.valueOf(10));
      fishRarity.put("Catfish", Integer.valueOf(5));
      fishRarity.put("Carp", Integer.valueOf(1));
      fishRarity.put("Blowfish", Integer.valueOf(60));
      fishRarity.put("Red Grouper", Integer.valueOf(30));
      fishRarity.put("Salmon", Integer.valueOf(10));
      fishRarity.put("Tuna", Integer.valueOf(10));
      fishRarity.put("Swordfish", Integer.valueOf(5));
      fishRarity.put("Shark", Integer.valueOf(1));
      fishRarity.put("Whale", Integer.valueOf(1));
      fishRarity.put("Squid", Integer.valueOf(40));
      fishRarity.put("Jellyfish", Integer.valueOf(40));
      fishRarity.put("Frog", Integer.valueOf(1));
      fishRarity.put("Turtle", Integer.valueOf(1));
      fishRarity.put("Leech", Integer.valueOf(30));
      fishRarity.put("Pirahna", Integer.valueOf(60));
      fishRarity.put("Electric Eel", Integer.valueOf(25));
      fishRarity.put("Tambaqui", Integer.valueOf(5));
      fishRarity.put("Arapaima", Integer.valueOf(1));
      fishRarity.put("Cod", Integer.valueOf(10));
      fishRarity.put("Pollock", Integer.valueOf(25));
      fishRarity.put("Herring", Integer.valueOf(60));
      fishRarity.put("Halibut", Integer.valueOf(1));
      fishRarity.put("Pink Salmon", Integer.valueOf(10));
      fishRarity.put("Rainbow Trout", Integer.valueOf(20));
      fishRarity.put("Blackfish", Integer.valueOf(45));
      fishRarity.put("Capitaine", Integer.valueOf(1));
      fishRarity.put("Boulti", Integer.valueOf(30));
      fishRarity.put("Bagrid", Integer.valueOf(20));
      fishRarity.put("Syndontis", Integer.valueOf(60));
      fishRarity.put("Red Shrooma", Integer.valueOf(20));
      fishRarity.put("Brown Shrooma", Integer.valueOf(20));
      junkRarity.put("Seaweed", Integer.valueOf(25));
      junkRarity.put("Algae", Integer.valueOf(25));
      junkRarity.put("Driftwood", Integer.valueOf(25));
      junkRarity.put("Tin Can", Integer.valueOf(30));
      junkRarity.put("Box", Integer.valueOf(25));
      junkRarity.put("Lockbox", Integer.valueOf(23));
      junkRarity.put("Treasure Chest", Integer.valueOf(10));
      junkRarity.put("Stick", Integer.valueOf(20));
      junkRarity.put("Bone", Integer.valueOf(20));
      junkRarity.put("Leather Boots", Integer.valueOf(20));
      junkRarity.put("Apple", Integer.valueOf(20));
      junkRarity.put("Goldfish", Integer.valueOf(10));
      junkRarity.put("Message In A Bottle", Integer.valueOf(23));
      junkRarity.put("Neptunes Bounty", Integer.valueOf(1));
   }
}
