package net.silentchaos512.scalinghealth.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.silentchaos512.lib.config.ConfigBaseNew;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;
import net.silentchaos512.lib.config.ConfigOption;
import net.silentchaos512.lib.config.ConfigOption.BooleanDefault;
import net.silentchaos512.lib.config.ConfigOption.Comment;
import net.silentchaos512.lib.config.ConfigOption.RangeFloat;
import net.silentchaos512.lib.config.ConfigOption.RangeInt;
import net.silentchaos512.lib.event.Greetings;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.HeartDisplayHandler;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import net.silentchaos512.scalinghealth.lib.EnumAreaDifficultyMode;
import net.silentchaos512.scalinghealth.lib.EnumHealthModMode;
import net.silentchaos512.scalinghealth.lib.EnumResetTime;
import net.silentchaos512.scalinghealth.lib.SimpleExpression;
import net.silentchaos512.scalinghealth.lib.module.ModuleAprilTricks;
import net.silentchaos512.scalinghealth.utils.EntityDifficultyChangeList;
import net.silentchaos512.scalinghealth.utils.EntityMatchList;
import net.silentchaos512.scalinghealth.utils.PlayerMatchList;

public class Config extends ConfigBaseNew {
   public static int BLIGHT_EQUIPMENT_HIGHEST_COMMON_TIER = 1;
   public static float BLIGHT_EQUIPMENT_TIER_UP_CHANCE = 0.095F;
   public static float BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE = 0.5F;
   public static float BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE = 0.5F;
   public static int PET_REGEN_DELAY = 600;
   public static int PACKET_DELAY = 20;
   public static float HEART_CRYSTAL_ORE_VEIN_COUNT = 0.42857143F;
   public static int HEART_CRYSTAL_ORE_VEIN_SIZE = 6;
   public static int HEART_CRYSTAL_ORE_MIN_HEIGHT = 10;
   public static int HEART_CRYSTAL_ORE_MAX_HEIGHT = 35;
   public static float HEART_CRYSTAL_ORE_EXTRA_VEIN_CAP = 3.0F;
   public static float HEART_CRYSTAL_ORE_EXTRA_VEIN_RATE = HEART_CRYSTAL_ORE_EXTRA_VEIN_CAP / 3125.0F;
   public static int HEART_CRYSTAL_ORE_QUANTITY_DROPPED = 1;
   public static boolean MORPHEUS_OVERRIDE;
   static final String split = ".";
   public static final String CAT_MAIN = "main";
   public static final String CAT_DEBUG = "main.debug";
   public static final String CAT_CLIENT = "main.client";
   public static final String CAT_CLIENT_WIT = "main.client.wit";
   public static final String CAT_PLAYER = "main.player";
   public static final String CAT_FAKE_PLAYER = "main.fake_players";
   public static final String CAT_PLAYER_DAMAGE = "main.player.damage";
   public static final String CAT_PLAYER_HEALTH = "main.player.health";
   public static final String CAT_PLAYER_REGEN = "main.player.regen";
   public static final String CAT_MOB = "main.mob";
   public static final String CAT_MOB_HEALTH = "main.mob.health";
   public static final String CAT_MOB_BLIGHT = "main.mob.blights";
   public static final String CAT_MOB_POTION = "main.mob.potion";
   public static final String CAT_MOB_BLIGHT_EQUIP = "main.mob.blights.equipment";
   public static final String CAT_PETS = "main.pets";
   public static final String CAT_ITEMS = "main.items";
   public static final String CAT_DIFFICULTY = "main.difficulty";
   public static final String CAT_DIFFICULTY_LUNAR_PHASES = "main.difficulty.lunar_phases";
   public static final String CAT_NETWORK = "main.network";
   public static final String CAT_WORLD = "main.world";
   public static final String CAT_COMPAT = "main.compatibility";
   public static final String CAT_HOLIDAYS = "main.holidays";
   public static final Config INSTANCE = new Config();

   public Config() {
      super("scalinghealth");
   }

   public void init(File file) {
      String path = file.getPath().replaceFirst("\\.cfg$", "/main.cfg");
      super.init(new File(path));
   }

   public I18nHelper i18n() {
      return ScalingHealth.i18n;
   }

   public LogHelper log() {
      return ScalingHealth.logHelper;
   }

   public void load() {
      try {
         super.load();
         this.config.setCategoryRequiresMcRestart("main.client.wit", true);
         int maxHealthCap = this.loadInt("Max Health Cap", "main", 2048, 2, Integer.MAX_VALUE, "Max health cap for all entities, players and mobs (vanilla is 1024)");

         try {
            ScalingHealth.logHelper.info("Trying to change max health cap to {}", new Object[]{maxHealthCap});
            Field field = ObfuscationReflectionHelper.findField(RangedAttribute.class, "maximumValue");
            field.setDouble(SharedMonsterAttributes.MAX_HEALTH, (double)maxHealthCap);
         } catch (Exception var10) {
            ScalingHealth.logHelper.warn(var10, "Failed to change max health cap", new Object[0]);
         }

         Config.Client.Hearts.textStyle = HeartDisplayHandler.TextStyle.loadFromConfig(this, "Health Text Style", HeartDisplayHandler.TextStyle.ROWS);
         Config.Client.Hearts.textColor = HeartDisplayHandler.TextColor.loadFromConfig(this, "Health Text Color", HeartDisplayHandler.TextColor.GREEN_TO_RED);
         Config.Client.Hearts.textSolidColor = Color.parse(this.config.getString("Health Text Solid Color", "main.client", "FFFFFF", "Text color is color style is SOLID")).getColor();
         Config.Client.Hearts.absorptionStyle = HeartDisplayHandler.AbsorptionHeartStyle.loadDromConfig(this);
         Config.Client.Hearts.absorbTextStyle = HeartDisplayHandler.TextStyle.loadFromConfig(this, "Absorption Text Style", HeartDisplayHandler.TextStyle.DISABLED);
         Config.Client.Hearts.absorbTextColor = HeartDisplayHandler.TextColor.loadFromConfig(this, "Absorption Text Color", HeartDisplayHandler.TextColor.WHITE);
         loadHeartColors(this.config);
         Config.Player.Health.resetTime = EnumResetTime.loadFromConfig(this.config, Config.Player.Health.resetTime, "main.player.health");
         Config.Player.Health.byXP.clear();
         ConfigMultiValueLineParser parser = new ConfigMultiValueLineParser("Set Health By XP", ScalingHealth.logHelper, "\\s+", new Class[]{Integer.class, Integer.class});

         for(String str : this.config.getStringList("Set Health By XP", "main.player.health", new String[0], "Allows the player's health to be set according to XP level. Each line will have the level, then the max health after a space. For example, \"10 30\" would give the player 15 hearts (30 health) at level 10. Note this is the \"target health\" for a player of this XP level. The actual bonus heart will be the value you enter minus starting health. The highest level the player has passed will be selected. The health bonus will stack with heart containers (just remember heart containers consume XP by default).")) {
            Object[] array = parser.parse(str);
            if (array != null) {
               Config.Player.Health.byXP.put(Integer.valueOf(((Integer)array[0]).intValue()), Integer.valueOf(((Integer)array[1]).intValue()));
            }
         }

         DamageScaling.INSTANCE.loadConfig(this.config);
         Config.Mob.damageBonusBlacklist.clear();

         for(String str : this.config.getStringList("Damage Bonus Blacklist", "main.mob", new String[0], "Mobs listed here will not receive extra attack damage, but can still get extra health or become blights")) {
            Config.Mob.damageBonusBlacklist.add(str);
         }

         Config.Mob.Health.healthScalingMode = EnumHealthModMode.loadFromConfig(this.config, Config.Mob.Health.healthScalingMode);
         String[] dimList = this.config.getStringList("Dimension Blacklist", "main.mob.health", new String[0], "Mobs will not gain extra health or become blights in these dimensions. Integers only, any other entries will be silently ignored.");
         Config.Mob.Health.dimensionBlacklist.clear();

         for(String str : dimList) {
            if (this.canParseInt(str)) {
               Config.Mob.Health.dimensionBlacklist.add(Integer.valueOf(Integer.parseInt(str)));
            }
         }

         Config.Mob.Health.mobBlacklist.clear();

         for(String str : this.config.getStringList("Blacklist", "main.mob.health", Config.Mob.Health.mobBlacklistDefaults, "Mobs listed here will never receive extra health, and will not become blights. There is also a separate blacklist for blights, if you still want the mob in question to have extra health.")) {
            Config.Mob.Health.mobBlacklist.add(str);
         }

         Config.Mob.Blight.blightAllList.loadConfig(this.config, "Always Blight", "main.mob.blights", new String[0], false, "If \"All Mobs Are Blights\" is enabled, this list can be used to filter mobs.");
         Config.Mob.Blight.blacklist.clear();

         for(String str : this.config.getStringList("Blacklist", "main.mob.blights", Config.Mob.Blight.BLIGHT_BLACKLIST_DEFAULTS, "Mobs listed here will never become blights, but can still receive extra health. There is also a blacklist for extra health.")) {
            Config.Mob.Blight.blacklist.add(str);
         }

         BLIGHT_EQUIPMENT_HIGHEST_COMMON_TIER = this.loadInt("Highest Common Tier", "main.mob.blights.equipment", BLIGHT_EQUIPMENT_HIGHEST_COMMON_TIER, 0, 4, "The highest commonly-occuring equipment tier for blights. This goes from 0 to 4 inclusive. For armor, the defaults (tiers 0 to 4) are leather, gold, chainmail, iron, and diamond.");
         BLIGHT_EQUIPMENT_TIER_UP_CHANCE = this.config.getFloat("Tier Up Chance", "main.mob.blights.equipment", BLIGHT_EQUIPMENT_TIER_UP_CHANCE, 0.0F, 1.0F, "The chance that a higher tier will be selected for a blight. A common tier is chosen first, then it has a few chances to increase.");
         BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE = this.config.getFloat("Armor Piece Chance", "main.mob.blights.equipment", BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE, 0.0F, 1.0F, "The chance of an additional armor piece being given. Every blight receives a helmet, then has this probability of receiving a chestplate. If it gets a chestplate, it has this probability of receiving leggings, etc.");
         BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE = this.config.getFloat("Hand Piece Chance", "main.mob.blights.equipment", BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE, 0.0F, 1.0F, "The chance that a blight will receive equipment in their hands (swords, etc.) They only get a chance at an offhand item if a main hand item is selected. Depending on the mods you have installed, there may not be any hand equipment to chose from.");
         this.config.setCategoryComment("main.mob.potion", "Potion effects applied to non-blights.");
         this.config.setCategoryRequiresMcRestart("main.mob.potion", true);
         PET_REGEN_DELAY = this.loadInt("Regen Delay", "main.pets", PET_REGEN_DELAY, 0, 72000, "The number of ticks between regen ticks on pets. Set to 0 to disable pet regen.");
         if (Config.Items.Heart.bossMax < Config.Items.Heart.bossMin) {
            Config.Items.Heart.bossMax = Config.Items.Heart.bossMin;
         }

         if (Config.Items.Heart.blightMax < Config.Items.Heart.blightMin) {
            Config.Items.Heart.blightMax = Config.Items.Heart.blightMin;
         }

         Config.Client.Difficulty.sleepMessageOverride = this.config.getString("Warn When Sleeping - Message", "main.client", "", "If not empty, this replaces the default 'warn when sleeping' message. Leaving this empty will pull the usual message from the lang file.");
         Config.Difficulty.DIFFICULTY_EXEMPT_PLAYERS.clear();

         for(String name : this.config.getStringList("Exempt Players", "main.difficulty", new String[0], "Players listed here will be \"exempt\" from the difficulty system. Exempt players are still part of difficulty calculations, but are treated as having zero difficulty.")) {
            Config.Difficulty.DIFFICULTY_EXEMPT_PLAYERS.add(name);
         }

         parser = new ConfigMultiValueLineParser("Difficulty Per Kill By Mob", ScalingHealth.logHelper, "\\s", new Class[]{String.class, Float.class, Float.class});
         Config.Difficulty.DIFFICULTY_PER_KILL_BY_MOB.clear();

         for(String str : this.config.getStringList("Difficulty Per Kill By Mob", "main.difficulty", new String[0], "Lets you set difficulty changes for individual mobs. Each line has 3 values separated by spaces: entity ID, standard kill change, blight kill change. For example, entering \"minecraft:zombie 0.1 -20\" will cause zombie kills to add 0.1 difficulty, but killing a blight zombie will remove 20 difficulty instead.")) {
            Object[] values = parser.parse(str);
            if (values != null) {
               Config.Difficulty.DIFFICULTY_PER_KILL_BY_MOB.put((String)values[0], ((Float)values[1]).floatValue(), ((Float)values[2]).floatValue());
            }
         }

         parser = new ConfigMultiValueLineParser("Difficulty Dimension Multiplier", ScalingHealth.logHelper, "\\s", new Class[]{Integer.class, Float.class});

         for(String str : this.config.getStringList("Difficulty Dimension Multiplier", "main.difficulty", new String[0], "Allows difficulty multipliers to be set for specific dimensions. Use the dimension ID and the multiplier you want, separated by a space. For example, \"-1 1.5\" would make difficulty increase 1.5x faster in the Nether.")) {
            Object[] values = parser.parse(str);
            if (values != null) {
               Config.Difficulty.DIMENSION_INCREASE_MULTIPLIER.put(Integer.valueOf(((Integer)values[0]).intValue()), Float.valueOf(((Float)values[1]).floatValue()));
            }
         }

         parser = new ConfigMultiValueLineParser("Dimension Value Factor", ScalingHealth.logHelper, "\\s", new Class[]{Integer.class, String.class});

         for(String str : this.config.getStringList("Dimension Value Factor", "main.difficulty", new String[0], "Apply a simple change to the area difficulty in a given dimension. Use the dimension ID, then a space, then an operator (+-*/) followed by a number. For example, \"-1 *2.0\" would make difficulty 2x higher in the Nether. \"1 +20\" would increase difficulty by 20 in The End")) {
            Object[] values = parser.parse(str);
            if (values != null) {
               SimpleExpression.from((String)values[1]).ifPresent((exp) -> {
                  SimpleExpression var10000 = (SimpleExpression)Config.Difficulty.DIMENSION_VALUE_FACTOR.put(Integer.valueOf(((Integer)values[0]).intValue()), exp);
               });
            }
         }

         Config.Difficulty.DIFFICULTY_LUNAR_MULTIPLIERS_ENABLED = this.loadBoolean("Lunar Phases Enabled", "main.difficulty.lunar_phases", false, "Enable lunar phase difficulty multipliers. Difficulty will receive a multiplier based on the phase of the moon.");
         parser = new ConfigMultiValueLineParser("Lunar Phase Multipliers", ScalingHealth.logHelper, "\\s", new Class[]{Float.class});
         int lunarPhaseIndex = 0;

         for(String str : this.config.getStringList("Lunar Phase Multipliers", "main.difficulty.lunar_phases", Config.Difficulty.DEFAULT_DIFFICULTY_LUNAR_MULTIPLIERS, "Difficulty multipliers for each lunar phase. There must be exactly 8 values. The first is full moon, the fifth is new moon.")) {
            Object[] values = parser.parse(str);
            if (values != null && lunarPhaseIndex < 8) {
               Config.Difficulty.DIFFICULTY_LUNAR_MULTIPLIERS[lunarPhaseIndex] = ((Float)values[0]).floatValue();
            }

            ++lunarPhaseIndex;
         }

         if (lunarPhaseIndex != 8) {
            ScalingHealth.logHelper.warn("Config \"Lunar Phase Multipliers\" has the wrong number of values. Needs 8, has " + lunarPhaseIndex, new Object[0]);
         }

         Config.Difficulty.DIFFICULTY_BY_GAME_STAGES.clear();
         parser = new ConfigMultiValueLineParser("Game Stages", ScalingHealth.logHelper, "\\s+", new Class[]{String.class, Integer.class});

         for(String str : this.config.getStringList("Game Stages", "main.difficulty", new String[0], "Allows difficulty to be set via Game Stages. Each line should consist of the stage key, followed by a space and the difficulty value (integers only). Example: \"my_stage_key 100\"")) {
            Object[] values = parser.parse(str);
            if (values != null) {
               Config.Difficulty.DIFFICULTY_BY_GAME_STAGES.put((String)values[0], (Integer)values[1]);
            }
         }

         Config.Difficulty.AREA_DIFFICULTY_MODE = EnumAreaDifficultyMode.loadFromConfig(this.config, Config.Difficulty.AREA_DIFFICULTY_MODE);
         Config.Difficulty.DIFFFICULTY_RESET_TIME = EnumResetTime.loadFromConfig(this.config, Config.Difficulty.DIFFFICULTY_RESET_TIME, "main.difficulty");
         PACKET_DELAY = this.loadInt("Packet Delay", "main.network", PACKET_DELAY, 1, 1200, "The number of ticks between update packets. Smaller numbers mean more packets (and more bandwidth and processing power used), but also less client-server desynconfig.");
         String cat = "main.world.heart_crystal_ore";
         HEART_CRYSTAL_ORE_VEIN_COUNT = this.config.getFloat("Veins Per Chunk", cat, HEART_CRYSTAL_ORE_VEIN_COUNT, 0.0F, 10000.0F, "The number of veins per chunk. The fractional part is a probability of an extra vein in each chunk.");
         HEART_CRYSTAL_ORE_VEIN_SIZE = this.loadInt("Vein Size", cat, HEART_CRYSTAL_ORE_VEIN_SIZE, 0, 10000, "The size of each vein.");
         HEART_CRYSTAL_ORE_MIN_HEIGHT = this.loadInt("Min Height", cat, HEART_CRYSTAL_ORE_MIN_HEIGHT, 0, 255, "The lowest y-level the ore can be found at. Must be less than Max Height");
         HEART_CRYSTAL_ORE_MAX_HEIGHT = this.loadInt("Max Height", cat, HEART_CRYSTAL_ORE_MAX_HEIGHT, 0, 255, "The highest y-level the ore can be found at. Must be greater than Min Height");
         if (HEART_CRYSTAL_ORE_MAX_HEIGHT <= HEART_CRYSTAL_ORE_MIN_HEIGHT) {
            HEART_CRYSTAL_ORE_MAX_HEIGHT = 35;
            HEART_CRYSTAL_ORE_MIN_HEIGHT = 10;
         }

         HEART_CRYSTAL_ORE_EXTRA_VEIN_RATE = this.config.getFloat("Extra Vein Rate", cat, HEART_CRYSTAL_ORE_EXTRA_VEIN_RATE, 0.0F, 1.0F, "The number of extra possible veins per chunk away from spawn. The default value will reach the cap at 50,000 blocks from spawn.");
         HEART_CRYSTAL_ORE_EXTRA_VEIN_CAP = this.config.getFloat("Extra Vein Cap", cat, HEART_CRYSTAL_ORE_EXTRA_VEIN_CAP, 0.0F, 1000.0F, "The maximum number of extra veins created by distance from spawn.");
         HEART_CRYSTAL_ORE_QUANTITY_DROPPED = this.loadInt("Quantity Dropped", cat, HEART_CRYSTAL_ORE_QUANTITY_DROPPED, 1, 64, "The base number of heart crystal shards dropped by the ore. Fortune III can double this value at most.");
         this.config.setCategoryRequiresMcRestart("main.compatibility", true);
         MORPHEUS_OVERRIDE = this.config.getBoolean("Morpheus Support", "main.compatibility", true, "Override the Morpheus new day handler to fire sleep events. Without this, difficulty will not increase when sleeping.");
         ModuleAprilTricks.instance.loadConfig(this.config);
         ScalingHealth.logHelper.info("Config successfully loaded!", new Object[0]);
      } catch (Exception var11) {
         ScalingHealth.logHelper.fatal("Could not load configuration file!", new Object[0]);
         Greetings.addMessage(() -> {
            return new TextComponentString(TextFormatting.RED + "[Scaling Health] Could not load configuration file! The mod will not work correctly. See log for details.");
         });
         var11.printStackTrace();
      }

   }

   private static void loadHeartColors(Configuration c) {
      String[] defaults = new String[Config.Client.Hearts.defaultHeartColors.length];

      for(int i = 0; i < defaults.length; ++i) {
         defaults[i] = String.format("%06x", Config.Client.Hearts.defaultHeartColors[i]);
      }

      String[] list = c.getStringList("Heart Colors", "main.client", defaults, "The colors for each additional row of hearts. The colors will loop back around to the beginning if necessary. Use hexadecimal to specify colors (like HTML color codes).");
      String[] listAbsorb = c.getStringList("Absorption Heart Colors", "main.client", defaults, "The colors for each row of absorption hearts. Works the same way as \"Heart Colors\"");

      try {
         Config.Client.Hearts.heartColors = new int[list.length];

         for(int i = 0; i < Config.Client.Hearts.heartColors.length; ++i) {
            Config.Client.Hearts.heartColors[i] = Integer.decode("0x" + list[i]).intValue();
         }

         Config.Client.Hearts.absorptionHeartColors = new int[listAbsorb.length];

         for(int i = 0; i < Config.Client.Hearts.absorptionHeartColors.length; ++i) {
            Config.Client.Hearts.absorptionHeartColors[i] = Integer.decode("0x" + listAbsorb[i]).intValue();
         }
      } catch (NumberFormatException var5) {
         ScalingHealth.logHelper.warn("Failed to load heart colors because a value could not be parsed. Make sure all values are valid hexadecimal integers. Try using an online HTML color picker if you are having problems.", new Object[0]);
         var5.printStackTrace();
      }

   }

   public boolean canParseInt(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public boolean canParseFloat(String str) {
      try {
         Float.parseFloat(str);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public float tryParseFloat(String str) {
      try {
         return Float.parseFloat(str);
      } catch (NumberFormatException var3) {
         return 0.0F;
      }
   }

   public static final class Client {
      @ConfigOption(
         name = "Enable WIT Support",
         category = "main.client.wit"
      )
      @Comment("If true, additional information on entities will be added to WIT. Disable this if another mod is using the \"wit\" mod ID and causing the game to crash as a result.")
      @BooleanDefault(true)
      public static boolean enableWitSupport;

      public Client() {
      }

      public static final class Difficulty {
         @ConfigOption(
            name = "Render Difficulty Meter",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("Show the difficulty meter. Usually, it is display for a few seconds occasionally. If false, it is never shown.")
         public static boolean renderMeter;
         @ConfigOption(
            name = "Render Difficulty Meter Always",
            category = "main.client"
         )
         @BooleanDefault(false)
         @Comment("Render the difficulty meter at all times. If false, it displays occasionally.")
         public static boolean renderMeterAlways;
         @ConfigOption(
            name = "Difficulty Meter Display Time",
            category = "main.client"
         )
         @RangeInt(
            value = 160,
            min = 0
         )
         @Comment("The time (in ticks) to show the difficulty meter whenever it pops up.")
         public static int meterDisplayTime;
         @ConfigOption(
            name = "Position X",
            category = "main.client"
         )
         @RangeInt(5)
         @Comment("Sets position of the difficulty meter. Negative numbers anchor it to the right side of the screen.")
         public static int meterPosX;
         @ConfigOption(
            name = "Position Y",
            category = "main.client"
         )
         @RangeInt(-30)
         @Comment("Sets position of the difficulty meter. Negative numbers anchor it to the bottom of the screen.")
         public static int meterPosY;
         @ConfigOption(
            name = "Warn When Sleeping",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("If difficulty is set to change when the player sleeps, they will be warned when they get in bed.")
         public static boolean warnWhenSleeping;
         public static String sleepMessageOverride;

         public Difficulty() {
         }
      }

      public static final class Hearts {
         @ConfigOption(
            name = "Custom Heart Rendering",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("Replaces vanilla heart rendering (regular and absorption)")
         public static boolean customHeartRendering;
         @ConfigOption(
            name = "Replace Vanilla Heart Row With Custom",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("If true, replaces the vanilla hearts with Scaling Health's hearts. Otherwise, regular vanilla hearts are rendered first, then custom hearts are used for extra health.")
         public static boolean replaceVanillaRow;
         @ConfigOption(
            name = "Text Offset X",
            category = "main.client"
         )
         @RangeInt(0)
         @Comment("Offset the position of health text.")
         public static int textOffsetX;
         @ConfigOption(
            name = "Text Offset Y",
            category = "main.client"
         )
         @RangeInt(0)
         @Comment("Offset the position of health text.")
         public static int textOffsetY;
         @ConfigOption(
            name = "Text Offset Absorption X",
            category = "main.client"
         )
         @RangeInt(0)
         @Comment("Offset the position of the absorption text")
         public static int absorbTextOffsetX;
         @ConfigOption(
            name = "Text Offset Absorption Y",
            category = "main.client"
         )
         @RangeInt(0)
         @Comment("Offset the position of the absorption text")
         public static int absorbTextOffsetY;
         public static HeartDisplayHandler.TextStyle textStyle;
         public static HeartDisplayHandler.TextColor textColor;
         public static int textSolidColor;
         public static HeartDisplayHandler.AbsorptionHeartStyle absorptionStyle;
         public static HeartDisplayHandler.TextStyle absorbTextStyle;
         public static HeartDisplayHandler.TextColor absorbTextColor;
         @ConfigOption(
            name = "Last Heart Outline Enabled",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("Outline your highest (max health) heart in a different color. This makes seeing your max health a little bit easier.")
         public static boolean lastHeartOutline;
         @ConfigOption(
            name = "Last Heart Outline Color",
            category = "main.client"
         )
         @RangeInt(
            value = 16777215,
            min = 0,
            max = 16777215
         )
         @Comment("The color of the last heart outline (default value). Due to an oversight, this ended up as a decimal number. Oops.")
         public static int lastHeartOutlineColor;
         @ConfigOption(
            name = "Color Looping",
            category = "main.client"
         )
         @BooleanDefault(true)
         @Comment("If true, heart colors will 'loop around' to the first color after going through the entire list. Set false to have every row after the last have the same color.")
         public static boolean heartColorLooping;
         public static int[] defaultHeartColors = new int[]{12517376, 15097856, 15112448, 15127296, 10085888, 5039616, 59033, 59110, 39398, 230, 10027238, 13992191, 9211020, 15132390};
         public static int[] heartColors = new int[0];
         public static int[] absorptionHeartColors = new int[0];

         public Hearts() {
         }
      }
   }

   public static final class Debug {
      @ConfigOption(
         name = "Debug Mode",
         category = "main.debug"
      )
      @BooleanDefault(false)
      @Comment("Master switch for the other debug configs.")
      public static boolean debugMode;
      @ConfigOption(
         name = "Debug Overlay",
         category = "main.debug"
      )
      @BooleanDefault(true)
      @Comment("Draws information related to the mod on-screen, including health modifiers, difficulty data, and more. This is intended for testing purposes only, not normal gameplay.")
      public static boolean debugOverlay;
      @ConfigOption(
         name = "Log Spawns",
         category = "main.debug"
      )
      @BooleanDefault(false)
      @Comment("If debug mode is on, this will log details on mob spawns. This may slow down your game.")
      public static boolean logSpawns;
      @ConfigOption(
         name = "Log Player Damage",
         category = "main.debug"
      )
      @BooleanDefault(true)
      @Comment("If debug mode is on, this will log details of damage done to players.")
      public static boolean logPlayerDamage;

      public Debug() {
      }
   }

   public static final class Difficulty {
      @ConfigOption(
         name = "Difficulty Added When Sleeping",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("Change in difficulty when a player sleeps through the night. Negative numbers cause difficulty to decrease.")
      public static float forSleeping;
      @ConfigOption(
         name = "Group Area Bonus",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.05F,
         min = -10.0F,
         max = 10.0F
      )
      @Comment("Adds extra difficulty per additional nearby player. So area difficulty will be multiplied by: 1 + group_bonus * (players_in_area - 1)")
      public static float groupAreaBonus;
      @ConfigOption(
         name = "Idle Multiplier",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.7F,
         min = -100.0F,
         max = 100.0F
      )
      @Comment("Difficulty added per second is multiplied by this if the player is not moving.")
      public static float idleMulti;
      @ConfigOption(
         name = "Lost On Death",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("The difficulty a player loses on death. Negative numbers cause the player to gain difficulty.")
      public static float lostOnDeath;
      @ConfigOption(
         name = "Max Value",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 250.0F,
         min = 0.0F
      )
      @Comment("The maximum difficulty level that can be reached.")
      public static float maxValue;
      @ConfigOption(
         name = "Min Value",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = 0.0F
      )
      @Comment("The minimum difficulty value. This can be different from the starting value.")
      public static float minValue;
      @ConfigOption(
         name = "Difficulty Per Blight Kill",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("Difficulty change per blight kill. Negative numbers cause difficulty to decrease.")
      public static float perBlightKill;
      @ConfigOption(
         name = "Difficulty Per Boss Kill",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("Difficulty change per boss kill. Negative numbers cause difficulty to decrease.")
      public static float perBossKill;
      @ConfigOption(
         name = "Difficulty Per Block",
         category = "main.difficulty"
      )
      @RangeFloat(0.0025F)
      @Comment("The amount of difficulty added per unit distance from the origin/spawn, assuming \"Area Mode\" is set to a distance-based option. Negative numbers will decrease difficulty over distance.")
      public static float perBlock;
      @ConfigOption(
         name = "Difficulty Per Kill",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("Difficulty change per hostile mob killed. Negative numbers cause difficulty to decrease.")
      public static float perHostileKill;
      @ConfigOption(
         name = "Difficulty Per Passive Kill",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("Difficulty change per passive mob kill. Negative numbers cause difficulty to decrease.")
      public static float perPassiveKill;
      @ConfigOption(
         name = "Increase Per Second",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0011574F,
         min = -10000.0F,
         max = 10000.0F
      )
      @Comment("The amount difficulty changes each second. In Difficult Life, the option was named per tick, but was actually applied each second. Negative numbers will decrease difficulty over time.")
      public static float perSecond;
      @ConfigOption(
         name = "Search Radius",
         category = "main.difficulty"
      )
      @RangeInt(
         value = 256,
         min = 0,
         max = 32767
      )
      @Comment("The distance from a newly spawned mob to search for players to determine its difficulty level. Set to 0 for unlimited range.")
      public static int searchRadius;
      @ConfigOption(
         name = "Starting Value",
         category = "main.difficulty"
      )
      @RangeFloat(
         value = 0.0F,
         min = 0.0F
      )
      @Comment("The starting difficulty level for new worlds or players joining for the first time.")
      public static float startValue;
      @ConfigOption(
         name = "Stats Consume Difficulty",
         category = "main.client"
      )
      @BooleanDefault(false)
      @Comment("If true, the difficulty a mob is spawned with will be \"consumed\" when given health/damage bonuses and potion effects (as in older versions).")
      public static boolean statsConsumeDifficulty;
      public static final Map<String, Integer> DIFFICULTY_BY_GAME_STAGES = new HashMap();
      private static final String[] DEFAULT_DIFFICULTY_LUNAR_MULTIPLIERS = new String[]{"1.5", "1.3", "1.2", "1.0", "0.8", "1.0", "1.2", "1.3"};
      public static PlayerMatchList DIFFICULTY_EXEMPT_PLAYERS = new PlayerMatchList();
      public static EntityDifficultyChangeList DIFFICULTY_PER_KILL_BY_MOB = new EntityDifficultyChangeList();
      public static Map<Integer, Float> DIMENSION_INCREASE_MULTIPLIER = new HashMap();
      public static Map<Integer, SimpleExpression> DIMENSION_VALUE_FACTOR = new HashMap();
      public static EnumAreaDifficultyMode AREA_DIFFICULTY_MODE = EnumAreaDifficultyMode.WEIGHTED_AVERAGE;
      public static EnumResetTime DIFFFICULTY_RESET_TIME = EnumResetTime.NONE;
      public static boolean DIFFICULTY_LUNAR_MULTIPLIERS_ENABLED = false;
      public static float[] DIFFICULTY_LUNAR_MULTIPLIERS = new float[8];

      public Difficulty() {
      }
   }

   public static final class FakePlayer {
      @ConfigOption(
         name = "Can Generate Hearts",
         category = "main.fake_players"
      )
      @BooleanDefault(true)
      @Comment("If enabled, fake players will be able to get heart container drops when killing mobs. Disabling should prevent at least some mob grinders from getting heart drops.")
      public static boolean generateHearts = true;
      public static boolean haveDifficulty = false;

      public FakePlayer() {
      }
   }

   public static final class Items {
      @ConfigOption(
         name = "Difficulty Change",
         category = "main.items.cursed_heart"
      )
      @RangeFloat(10.0F)
      @Comment("The amount of difficulty added/removed when using a cursed heart.")
      public static float cursedHeartChange;
      @ConfigOption(
         name = "Difficulty Change",
         category = "main.items.enchanted_heart"
      )
      @RangeFloat(-10.0F)
      @Comment("The amount of difficulty added/removed when using an enchanted heart.")
      public static float enchantedHeartChange;
      @ConfigOption(
         name = "Healing Items Fire Healing Event",
         category = "main.items"
      )
      @BooleanDefault(true)
      @Comment("If true, the healing from bandages and medkits will fire a standard healing event, allowing other mods to execute additional code or cancel the healing altogether. Disable if needed.")
      public static boolean healingItemFireEvent;

      public Items() {
      }

      public static final class Heart {
         @ConfigOption(
            name = "Hearts Dropped by Blight Min",
            category = "main.items"
         )
         @RangeInt(
            value = 0,
            min = 0,
            max = 64
         )
         @Comment("The minimum number of heart containers that a blight will drop when killed.")
         public static int blightMin;
         @ConfigOption(
            name = "Hearts Dropped by Blight Max",
            category = "main.items"
         )
         @RangeInt(
            value = 2,
            min = 0,
            max = 64
         )
         @Comment("The maximum number of heart containers that a blight will drop when killed.")
         public static int blightMax;
         @ConfigOption(
            name = "Hearts Dropped by Boss Min",
            category = "main.items"
         )
         @RangeInt(
            value = 3,
            min = 0,
            max = 64
         )
         @Comment("The minimum number of heart containers that a boss will drop when killed.")
         public static int bossMin;
         @ConfigOption(
            name = "Hearts Dropped by Boss Max",
            category = "main.items"
         )
         @RangeInt(
            value = 6,
            min = 0,
            max = 64
         )
         @Comment("The maximum number of heart containers that a boss will drop when killed.")
         public static int bossMax;
         @ConfigOption(
            name = "Heart Drop Chance",
            category = "main.items"
         )
         @RangeFloat(
            value = 0.01F,
            min = 0.0F,
            max = 1.0F
         )
         @Comment("The chance of a hostile mob dropping a heart container when killed.")
         public static float chanceHostile;
         @ConfigOption(
            name = "Heart Drop Chance (Passive)",
            category = "main.items"
         )
         @RangeFloat(
            value = 0.001F,
            min = 0.0F,
            max = 1.0F
         )
         @Comment("The chance of a passive mob (animals) dropping a heart container when killed.")
         public static float chancePassive;
         @ConfigOption(
            name = "Drop Shards Instead of Containers",
            category = "main.items"
         )
         @BooleanDefault(false)
         @Comment("If enabled, heart crystals drop shards instead of full containers.")
         public static boolean dropShardsInstead;
         @ConfigOption(
            name = "Heart Healing Event",
            category = "main.items"
         )
         @BooleanDefault(true)
         @Comment("If true, the healing from heart containers will fire a standard healing event, allowing other mods to execute additional code or cancel the healing altogether. Disable if needed.")
         public static boolean healingEvent;
         @ConfigOption(
            name = "Hearts Health Restored",
            category = "main.items"
         )
         @RangeInt(
            value = 4,
            min = 0
         )
         @Comment("The amount of extra health restored when using a heart container. This applies whether or not hearts increase max health.")
         public static int healthRestored;
         @ConfigOption(
            name = "Hearts Increase Max Health",
            category = "main.items"
         )
         @BooleanDefault(true)
         @Comment("If set to false, hearts will no longer increase the player's maximum health, but can still be used for healing.")
         public static boolean increaseHealth;
         @ConfigOption(
            name = "Heart XP Level Cost",
            category = "main.items"
         )
         @RangeInt(
            value = 3,
            min = 0
         )
         @Comment("The number of experience levels required to use a heart container.")
         public static int xpCost;

         public Heart() {
         }
      }
   }

   public static final class Mob {
      @ConfigOption(
         name = "Damage Modifier",
         category = "main.mob"
      )
      @RangeFloat(
         value = 0.1F,
         min = 0.0F
      )
      @Comment("A multiplier for extra attack strength all mobs will receive. Set to 0 to disable extra attack strength.")
      public static float damageMultiplier;
      @ConfigOption(
         name = "Max Damage Bonus",
         category = "main.mob"
      )
      @RangeFloat(
         value = 10.0F,
         min = 0.0F,
         max = 1000.0F
      )
      @Comment("The maximum extra attack damage a mob can receive. Zero means unlimited.")
      public static float maxDamageBoost;
      @ConfigOption(
         name = "Potion Chance (Hostiles)",
         category = "main.mob"
      )
      @RangeFloat(
         value = 0.375F,
         min = 0.0F,
         max = 1.0F
      )
      @Comment("The chance that an extra potion effect will be applied to any hostile mob. Note that this effect requires the mob to have a certain amount of \"difficulty\" left after it has been given extra health and damage. So entering 1 won't guarantee potion effects.")
      public static float hostilePotionChance;
      @ConfigOption(
         name = "Potion Chance (Passives)",
         category = "main.mob"
      )
      @RangeFloat(
         value = 0.025F,
         min = 0.0F,
         max = 1.0F
      )
      @Comment("The chance that an extra potion effect will be applied to any passive mob. Note that this effect requires the mob to have a certain amount of \"difficulty\" left after it has been given extra health and damage. So entering 1 won't guarantee potion effects.")
      public static float passivePotionChance;
      @ConfigOption(
         name = "XP Boost",
         category = "main.mob"
      )
      @RangeFloat(
         value = 0.01F,
         min = 0.0F,
         max = 1.0F
      )
      @Comment("Additional XP (as percentage) per point of difficulty. For example, if this is 0.01, a mob will drop 2x (+1.0x) XP at 100 difficulty and 3x (+2.0x) at 200")
      public static float xpBoost;
      public static EntityMatchList damageBonusBlacklist = new EntityMatchList();

      public Mob() {
      }

      public static final class Blight {
         @ConfigOption(
            name = "Blacklist All Bosses",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("If enabled, no bosses can become blights. If you need more control, use the Blacklist instead.")
         public static boolean blacklistBosses;
         @ConfigOption(
            name = "Blacklist All Hostiles",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("If enabled, no hostile mobs can become blights.")
         public static boolean blacklistHostiles;
         @ConfigOption(
            name = "Blacklist All Passives",
            category = "main.mob.blights"
         )
         @BooleanDefault(true)
         @Comment("If enabled, no passive (peaceful) mobs can become blights.")
         public static boolean blacklistPassives;
         @ConfigOption(
            name = "All Mobs Are Blights",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("If true, every mob that can be a blight will be one 100% of the time.")
         public static boolean blightAlways;
         @ConfigOption(
            name = "Blight Chance Multiplier",
            category = "main.mob.blights"
         )
         @RangeFloat(
            value = 0.0625F,
            min = 0.0F
         )
         @Comment("Determines the chance of a mob spawning as a blight. Formula is \"blightChanceMulti * currentDifficulty / maxDifficulty\". Setting to 0 will disable blights. Setting to 1 will guarantee blights at max difficulty.")
         public static float chanceMultiplier;
         @ConfigOption(
            name = "Fixed Chance",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("If true, blights will have a fixed chance of spawning, regardless of difficulty. Set the chance in \"Blight Chance Multiplier\".")
         public static boolean fixedBlightChance;
         @ConfigOption(
            name = "Blight Difficulty Multiplier",
            category = "main.mob.blights"
         )
         @RangeFloat(
            value = 3.0F,
            min = 1.0F
         )
         @Comment("When an entity spawns as a blight, their calculated difficulty is multiplied by this. Higher numbers mean more health and damage!")
         public static float difficultyMultiplier;
         @ConfigOption(
            name = "Fire Rides Blights",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("Set blight fires to \"ride\" on the blight they belong to. In some cases, this might cause the fire to follow the blight more smoothly, but doesn't seem to help in most cases. Leaving off is recommended.")
         public static boolean fireRidesBlight;
         @ConfigOption(
            name = "Fire Resist",
            category = "main.mob.blights"
         )
         @BooleanDefault(true)
         @Comment("Should blights have the fire resistance potion effect?")
         public static boolean fireResist;
         @ConfigOption(
            name = "Immune To Suffocation",
            category = "main.mob.blights"
         )
         @BooleanDefault(true)
         @Comment("If true, blights will not take suffocation (inside a block) damage")
         public static boolean immuneToSuffocation;
         @ConfigOption(
            name = "Invisibility",
            category = "main.mob.blights"
         )
         @BooleanDefault(false)
         @Comment("Should blights have the invisibility potion effect?")
         public static boolean invisibility;
         @ConfigOption(
            name = "Notify Players on Death",
            category = "main.mob.blights"
         )
         @BooleanDefault(true)
         @Comment("Let all players know when a blight dies in chat.")
         public static boolean notifyOnDeath;
         @ConfigOption(
            name = "Potion Duration",
            category = "main.mob.blights"
         )
         @RangeInt(
            value = 6000,
            min = -1
         )
         @Comment("The duration (in ticks) of the potion effects applied to blights. The effects are refreshed frequently, so this value doesn't matter in most cases... except for the lingering potion effects left by blight creepers. Set to -1 for infinite time. Default is 5 minutes.")
         public static int potionDuration;
         @ConfigOption(
            name = "Amplifier Speed",
            category = "main.mob.blights"
         )
         @RangeInt(
            value = 4,
            min = -1,
            max = 99
         )
         @Comment("The amplifier level on the speed potion effect applied to blights. Set -1 to disable, 0 is level 1.")
         public static int speedAmp;
         @ConfigOption(
            name = "Amplifier Strength",
            category = "main.mob.blights"
         )
         @RangeInt(
            value = 1,
            min = -1,
            max = 99
         )
         @Comment("The amplifier level on the strength potion effect applied to blights. Set -1 to disable, 0 is level 1.")
         public static int strengthAmp;
         @ConfigOption(
            name = "Supercharge Creepers",
            category = "main.mob.blights"
         )
         @BooleanDefault(true)
         @Comment("Blight creepers will be supercharged, like when they are struck by lightning.")
         public static boolean superchargeCreepers;
         @ConfigOption(
            name = "XP Multiplier",
            category = "main.mob.blights"
         )
         @RangeFloat(
            value = 10.0F,
            min = 0.0F,
            max = 1000.0F
         )
         @Comment("The multiplier applied to the amount of XP dropped when a blight is killed.")
         public static float xpMultiplier;
         public static EntityMatchList blightAllList = new EntityMatchList();
         public static EntityMatchList blacklist = new EntityMatchList();
         private static final String[] BLIGHT_BLACKLIST_DEFAULTS = new String[]{"minecraft:wither", "minecraft:villager", "minecolonies:citizen"};

         public Blight() {
         }
      }

      public static final class Health {
         @ConfigOption(
            name = "Allow Boss Extra Health",
            category = "main.mob.health"
         )
         @BooleanDefault(true)
         @Comment("Allow boss mobs (dragon, wither, etc.) to spawn with extra health based on difficulty.")
         public static boolean allowBoss;
         @ConfigOption(
            name = "Allow Peaceful Extra Health",
            category = "main.mob.health"
         )
         @BooleanDefault(true)
         @Comment("Allow peaceful/passive mobs (such as animals) to spawn with extra health based on difficulty.")
         public static boolean allowPeaceful;
         @ConfigOption(
            name = "Allow Hostile Extra Health",
            category = "main.mob.health"
         )
         @BooleanDefault(true)
         @Comment("Allow hostile mobs (monsters) to spawn with extra health based on difficulty.")
         public static boolean allowHostile;
         @ConfigOption(
            name = "Base Health Modifier",
            category = "main.mob.health"
         )
         @RangeFloat(
            value = 0.5F,
            min = 0.0F
         )
         @Comment("The minimum extra health a hostile mob will have per point of difficulty, before the scaling mode is accounted for.")
         public static float hostileHealthMultiplier;
         @ConfigOption(
            name = "Base Health Modifier Peaceful",
            category = "main.mob.health"
         )
         @RangeFloat(
            value = 0.25F,
            min = 0.0F
         )
         @Comment("The minimum extra health a peaceful mob will have per point of difficulty, before the scaling mode is accounted for. Same as \"Base Health Modifier\", but for peaceful/passive mobs!")
         public static float peacefulHealthMultiplier;
         public static EnumHealthModMode healthScalingMode = EnumHealthModMode.MULTI_HALF;
         public static List<Integer> dimensionBlacklist = new ArrayList();
         public static EntityMatchList mobBlacklist = new EntityMatchList();
         private static final String[] mobBlacklistDefaults = new String[0];

         public Health() {
         }
      }
   }

   public static final class Player {
      public Player() {
      }

      public static final class BonusRegen {
         @ConfigOption(
            name = "Enable Bonus Regen",
            category = "main.player.regen"
         )
         @BooleanDefault(true)
         @Comment("Enable bonus health regen for players. Vanilla regen is not changed in any way, this just adds extra healing! Vanilla regen can be disabled with the naturalRegeneration gamerule.")
         public static boolean enabled;
         @ConfigOption(
            name = "Scale With Max Health",
            category = "main.player.regen"
         )
         @BooleanDefault(false)
         @Comment("If enabled, regen will be proportional to max health")
         public static boolean scaleWithMaxHealth;
         @ConfigOption(
            name = "Food Min",
            category = "main.player.regen"
         )
         @RangeInt(
            value = 10,
            min = 0
         )
         @Comment("The minimum food level at which bonus regen will be active (vanilla max food is 20).")
         public static int minFood;
         @ConfigOption(
            name = "Food Max",
            category = "main.player.regen"
         )
         @RangeInt(
            value = Integer.MAX_VALUE,
            min = 0
         )
         @Comment("The maximum food level at which bonus regen will be active (vanilla max food is 20).")
         public static int maxFood;
         @ConfigOption(
            name = "Health Min",
            category = "main.player.regen"
         )
         @RangeInt(
            value = 0,
            min = 0
         )
         @Comment("Bonus regen will stop when players have this much health or less.")
         public static int minHealth;
         @ConfigOption(
            name = "Health Max",
            category = "main.player.regen"
         )
         @RangeInt(
            value = Integer.MAX_VALUE,
            min = 0
         )
         @Comment("Bonus regen will stop when players have this much health or more.")
         public static int maxHealth;
         @ConfigOption(
            name = "Delay (Initial)",
            category = "main.player.regen"
         )
         @RangeInt(
            value = 400,
            min = 0
         )
         @Comment("The amount of time (in ticks) after being hurt before bonus regen activates.")
         public static int initialDelay;
         @ConfigOption(
            name = "Delay",
            category = "main.player.regen"
         )
         @RangeInt(
            value = 100,
            min = 0
         )
         @Comment("The amount of time (in ticks) between each bonus regen tick (a half heart being healed).")
         public static int delay;
         @ConfigOption(
            name = "Exhaustion Added",
            category = "main.player.regen"
         )
         @RangeFloat(
            value = 0.1F,
            min = 0.0F,
            max = 1.0F
         )
         @Comment("The food consumption for each bonus regen tick.")
         public static float exhaustion;

         public BonusRegen() {
         }
      }

      public static final class Health {
         @ConfigOption(
            name = "Allow Modified Health",
            category = "main.player.health"
         )
         @BooleanDefault(true)
         @Comment("Allow Scaling Health to modify the player's health. Scaling Health's changes are often compatible with other mods, assuming they use Minecraft's attribute system. If set to false, heart containers and the '/scalinghealth health' command will not work.")
         public static boolean allowModify;
         @ConfigOption(
            name = "Starting Health",
            category = "main.player.health"
         )
         @RangeInt(
            value = 20,
            min = 2
         )
         @Comment("The amount of health (in half hearts) players will start with when first joining the world. Vanilla is 20.")
         public static int startingHealth;
         @ConfigOption(
            name = "Max Health",
            category = "main.player.health"
         )
         @RangeInt(
            value = 0,
            min = 0
         )
         @Comment("The maximum amount of health (in half hearts) a player can achieve with heart containers alone. Zero means unlimited. NOTE: Players must still obey Minecraft's max health cap. You can change that value with the \"Max Health Cap\" setting under the main category.")
         public static int maxHealth;
         @ConfigOption(
            name = "Min Health",
            category = "main.player.health"
         )
         @RangeInt(
            value = 2,
            min = 2
         )
         @Comment("The minimum amount of health (in half hearts) a player can have. This is different from starting health.")
         public static int minHealth;
         @ConfigOption(
            name = "Health Lost On Death",
            category = "main.player.health"
         )
         @RangeInt(0)
         @Comment("The amount of health (in half hearts) a player will lose each time they die. Set to a negative number to cause players to gain health instead.")
         public static int lostOnDeath;
         public static EnumResetTime resetTime = EnumResetTime.NONE;
         public static Map<Integer, Integer> byXP = new HashMap();

         public Health() {
         }
      }
   }
}
