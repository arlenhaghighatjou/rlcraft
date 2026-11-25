package com.lothrazar.playerbosses;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {
   public static boolean renderDebugHitboxes;
   public static Configuration config;
   public static boolean canDespawn;
   public static double fireballChance;
   public static double thirdPhaseHealth;
   public static double secondPhaseHealth;
   public static boolean arrowPotions;
   public static boolean thirdPhaseRunaway;
   public static double arrowChance;

   public ConfigManager() {
   }

   private static void load() {
      String category = "playerbosses";
      renderDebugHitboxes = config.getBoolean("debugHitboxes", category, false, "Show hitboxes for testing");
      canDespawn = config.getBoolean("canDespawnNaturally", category, false, "Natural Despawning");
      EntityPlayerBoss.bossName = config.getString("name", category, "Severed", "Name in boss bar");
      EntityPlayerBoss.expDropped = config.getInt("expDropped", category, 500, 0, 99999, "Exp when killed");
      EntityPlayerBoss.immuneFire = config.getBoolean("immune to fire", category, true, "is immune to fire");
      EntityPlayerBoss.armor = (double)config.getInt("armor", category, 2, 0, 20, "Armor default when not equipped");
      EntityPlayerBoss.armor = (double)config.getInt("armor", category, 2, 0, 20, "Armor default when not equipped");
      EntityPlayerBoss.health = (double)config.getInt("health", category, 250, 0, 99999, "Health value");
      EntityPlayerBoss.speed = (double)config.getFloat("speed", category, 0.5F, 0.0F, 2.0F, "Movement speed value");
      EntityPlayerBoss.damage = (double)config.getInt("damage", category, 5, 0, 999, "Attack damage without weapons");
      EntityPlayerBoss.mainHand = config.getString("MainHand", category, "minecraft:iron_sword", "Main hand holding");
      EntityPlayerBoss.offHand = config.getString("OffHand", category, "minecraft:shield", "Off hand holding");
      category = "playerbosses.firstPhase";
      config.addCustomCategoryComment(category, "Initial phase is always bow.  ");
      arrowPotions = config.getBoolean("arrowPotions", category, true, "In first phase, do the arrows have potion effects");
      arrowChance = (double)config.getFloat("arrowChance", category, 0.03F, 0.0F, 0.99F, "Chance of bow shots in a tick, approximate");
      category = "playerbosses.secondphase";
      config.addCustomCategoryComment(category, "Melee Phase ");
      secondPhaseHealth = (double)config.getFloat("healthTrigger", category, 0.6F, 0.0F, 0.99F, "At which percentage of health should it switch to second phase of melee attacks");
      category = "playerbosses.thirdphase";
      config.addCustomCategoryComment(category, "Final Magic Phase ");
      thirdPhaseHealth = (double)config.getFloat("healthTrigger", category, 0.15F, 0.0F, 0.99F, "At which percentage of health should it switch to third phase of ghast fireballs");
      thirdPhaseRunaway = config.getBoolean("doRunaway", category, true, "Does it run away while shooting fire in phase three");
      fireballChance = (double)config.getFloat("fireballChance", category, 0.2F, 0.0F, 0.99F, "In third phase, how often to fireballs shoot");
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static void init(File configFile) {
      config = new Configuration(configFile);
      load();
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase("playerbosses")) {
         load();
      }

   }
}
