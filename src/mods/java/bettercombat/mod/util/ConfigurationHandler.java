package bettercombat.mod.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {
   public static Configuration config;
   private static final int VERSION = 5;
   public static boolean requireFullEnergy = false;
   public static boolean hitSound = true;
   public static boolean critSound = true;
   public static boolean moreSprint = false;
   public static boolean moreSweep = false;
   public static boolean randomCrits = true;
   public static boolean weakerOffhand = true;
   public static boolean enableOffHandAttack = true;
   public static float offHandEfficiency = 0.5F;
   public static float critChance = 0.2F;
   public static boolean requireEnergyToRandomCrit = true;
   public static boolean requireEnergyToJumpCrit = true;
   public static float distanceToJumpCrit = 2.0F;
   private static String[] itemClassWhitelist = new String[]{"net.minecraft.item.ItemSword", "net.minecraft.item.ItemAxe", "net.minecraft.item.ItemSpade", "net.minecraft.item.ItemPickaxe", "net.minecraft.item.ItemHoe"};
   private static String[] itemInstWhitelist = new String[0];
   private static String[] entityBlacklist = new String[]{"net.minecraft.entity.passive.EntityHorse", "net.minecraft.entity.item.EntityArmorStand", "net.minecraft.entity.passive.EntityVillager", "net.minecraft.entity.item.EntityItemFrame"};
   private static final String[] ICW_DEF = (String[])Arrays.copyOf(itemClassWhitelist, itemClassWhitelist.length);
   private static final String[] IIW_DEF = (String[])Arrays.copyOf(itemInstWhitelist, itemInstWhitelist.length);
   private static final String[] EB_DEF = (String[])Arrays.copyOf(entityBlacklist, entityBlacklist.length);
   private static Class<?>[] itemClassWhiteArray;
   private static Item[] itemInstWhiteArray;
   private static Class<?>[] entityBlackArray;

   public ConfigurationHandler() {
   }

   public static void init(File configFile) {
      if (config == null) {
         config = new Configuration(configFile, Integer.toString(5));
         loadConfiguration();
      }

   }

   private static void loadConfiguration() {
      String ver = config.getLoadedConfigVersion();
      int loadedVer = 0;

      try {
         if (ver != null) {
            loadedVer = Integer.parseInt(ver);
         }
      } catch (NumberFormatException var3) {
         ;
      }

      randomCrits = config.getBoolean("Random Crits", "general", true, "Melee attacks have now 30% chance to critically strike, critical strikes can no longer be forced by falling");
      weakerOffhand = config.getBoolean("Weaker Left Arm", "general", true, "Attacks with the Off-hand does 50% less damage");
      requireFullEnergy = config.getBoolean("Attacks require full energy", "general", false, "You may only attack if your energy is full");
      moreSprint = config.getBoolean("Attack and Sprint", "general", false, "Attacking an enemy while sprinting will no longer interrupt your sprint");
      moreSweep = config.getBoolean("More swipe animation", "general", false, "Every items can spawn the swipe animation");
      hitSound = config.getBoolean("Additional hit sound", "general", true, "Add an additional sound when striking a target");
      critSound = config.getBoolean("Additional crit sound", "general", true, "Add an additional sound when a critical strike happens");
      itemClassWhitelist = config.getStringList("Item Class Whitelist", "general", ICW_DEF, "Whitelisted item classes for attacking.");
      itemInstWhitelist = config.getStringList("Item Whitelist", "general", IIW_DEF, "Whitelisted items in the format \"domain:itemname\" for attacking.");
      enableOffHandAttack = config.getBoolean("Enable Offhand Attacks", "general", true, "Enables the capability to attack with your off-hand");
      offHandEfficiency = config.getFloat("Offhand Efficiency", "general", 0.5F, 0.0F, 1.0F, "The efficiency of an attack with offhanded weapon in percent (attack damage * efficiency)");
      critChance = config.getFloat("Random Crit Chance", "general", 0.2F, 0.0F, 1.0F, "How likely it is to land a critical hit in percent");
      entityBlacklist = config.getStringList("Entity Blacklist", "general", EB_DEF, "Blacklisted entity classes for attacking. You will not be able to attack any entity that extends this class! Please note that entities extending IEntityOwnable are by default blacklisted, when the entity is owned by the attacker.");
      requireEnergyToRandomCrit = config.getBoolean("Require Full Energy to Random Crit", "general", true, "Whether or not full/nearly full energy should be required to randomly crit");
      requireEnergyToJumpCrit = config.getBoolean("Require Full Energy to Jump Crit", "general", true, "Whether or not full/nearly full energy should be required to jump crit");
      distanceToJumpCrit = config.getFloat("Maximum Distance to Jump Crit", "general", 2.0F, 0.0F, 10.0F, "Maximum distance from a target to allow jump crits.");
      if (loadedVer < 5) {
         config.getCategory("general").remove("Item Class Blacklist");
         config.getCategory("general").remove("Item Blacklist");
      }

      if (config.hasChanged()) {
         config.save();
      }

   }

   public static void createInstLists() {
      List<Class<?>> classList = new ArrayList();

      for(String className : itemClassWhitelist) {
         try {
            classList.add(Class.forName(className));
         } catch (ClassNotFoundException var8) {
            ;
         }
      }

      itemClassWhiteArray = (Class[])classList.toArray(new Class[0]);
      classList.clear();

      for(String className : entityBlacklist) {
         try {
            classList.add(Class.forName(className));
         } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
         }
      }

      entityBlackArray = (Class[])classList.toArray(new Class[0]);
      List<Item> itemList = new ArrayList();

      for(String itemName : itemInstWhitelist) {
         Item itm = (Item)Item.REGISTRY.getObject(new ResourceLocation(itemName));
         if (itm != null) {
            itemList.add(itm);
         }
      }

      itemInstWhiteArray = (Item[])itemList.toArray(new Item[0]);
   }

   public static boolean isItemAttackUsable(Item item) {
      return Arrays.stream(itemInstWhiteArray).anyMatch((blItem) -> {
         return blItem == item;
      }) ? true : Arrays.stream(itemClassWhiteArray).anyMatch((wlClass) -> {
         return wlClass.isInstance(item);
      });
   }

   public static boolean isEntityAttackable(Entity entity) {
      return Arrays.stream(entityBlackArray).noneMatch((eClass) -> {
         return eClass.isInstance(entity);
      });
   }

   @SubscribeEvent
   public void onConfigurationChangedEvent(OnConfigChangedEvent event) {
      if ("bettercombatmod".equalsIgnoreCase(event.getModID())) {
         loadConfiguration();
         createInstLists();
      }

   }
}
