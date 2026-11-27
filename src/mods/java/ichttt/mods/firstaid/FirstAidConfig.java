package ichttt.mods.firstaid;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(
   modid = "firstaid",
   name = "firstaid"
)
@ExtraConfig
public class FirstAidConfig {
   @Comment({"Settings regarding the max health of the body's parts. 2 = 1 heart"})
   @LangKey("firstaid.config.damagesystem")
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static final FirstAidConfig.DamageSystem damageSystem = new FirstAidConfig.DamageSystem();
   @Comment({"Settings regarding the health overlay when ingame"})
   @LangKey("firstaid.config.overlay")
   public static final FirstAidConfig.Overlay overlay = new FirstAidConfig.Overlay();
   @Comment({"Settings regarding the internal healing system"})
   @LangKey("firstaid.config.internalhealing")
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static final FirstAidConfig.InternalHealing internalHealing = new FirstAidConfig.InternalHealing();
   @Comment({"Settings regarding external healing system(like vanilla potions or natural regeneration"})
   @LangKey("firstaid.config.externalhealing")
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static final FirstAidConfig.ExternalHealing externalHealing = new FirstAidConfig.ExternalHealing();
   @Comment({"Enable/Disable specify debuffs on specific body parts"})
   @LangKey("firstaid.config.debuffs")
   @ExtraConfig.Advanced
   public static final FirstAidConfig.Debuffs debuffs = new FirstAidConfig.Debuffs();
   @Comment({"Specify how the locational armor is calculated"})
   @LangKey("firstaid.config.locationalarmor")
   @RequiresWorldRestart
   @ExtraConfig.Advanced
   @ExtraConfig.Sync
   public static final FirstAidConfig.LocationalArmor locationalArmor = new FirstAidConfig.LocationalArmor();
   @Comment({"Specify how enchantments are applied"})
   @LangKey("firstaid.config.enchantmenthandling")
   @ExtraConfig.Advanced
   @ExtraConfig.Sync
   public static final FirstAidConfig.EnchantmentHandling enchantmentHandling = new FirstAidConfig.EnchantmentHandling();
   @Comment({"Set to true to enable the debuff sounds. Requieres enableDebuffs to be true"})
   @LangKey("firstaid.config.enablesoundsystem")
   @ExtraConfig.Advanced
   public static boolean enableSoundSystem = true;
   @Comment({"If true, max health is scaled to your hearts, and the config entries get multiplier to match the max health"})
   @LangKey("firstaid.config.scalemaxhealth")
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static boolean scaleMaxHealth = false;
   @Comment({"If true, max health will be capped at 6 hearts and absorption at 2 hearts per limb. If false, the health cap will be much higher (64 hearts normal and 16 absorption)"})
   @LangKey("firstaid.config.capmaxhealth")
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static boolean capMaxHealth = true;
   @Comment({"If true, all usages of setHealth from other mods will be captured. Should not cause any problems, but allow mods like scaling health bandages to apply"})
   @LangKey("firstaid.config.sethealth")
   @ExtraConfig.Advanced
   public static boolean watchSetHealth = true;
   @Comment({"If true, many damage distributions will be more realistic, but this will also cause them to be harder\nIf enabled, e.g. drowing will only damage your body instead of your body and head last"})
   @LangKey("firstaid.config.hardmode")
   @RequiresMcRestart
   public static boolean hardMode = false;
   @Comment({"Specifies how the vanilla health is calculated. Affects the vanilla visual health bar, as well as the value other mods get to see when they query the player health.\nAVERAGE_ALL simply takes all limbs and calculates the average of it.\nAVERAGE_CRITICAL takes all critical limbs and calculates the average of it\nMIN_CRITICAl takes the smallest health value of all critical limb\nDoes not have any effect if all critical limbs have been disabled."})
   @RequiresWorldRestart
   @ExtraConfig.Sync
   public static FirstAidConfig.VanillaHealthCalculationMode vanillaHealthCalculation = FirstAidConfig.VanillaHealthCalculationMode.AVERAGE_ALL;
   @Comment({"Only effects the fallback random distribution.\nIf enabled, the default random damage distribution will be changed to leave critical limbs at 1hp if possible.\nWhen there is too much damage, the damage will still kill the player. Other distributions that defined are not affected by this."})
   public static boolean useFriendlyRandomDistribution = false;
   @Comment({"Enabled additional debug logs - May slow down the game and will increase log file size\nOnly enable for special purposes"})
   @LangKey("firstaid.config.debug")
   @RequiresMcRestart
   @ExtraConfig.Advanced
   public static boolean debug = false;

   public FirstAidConfig() {
   }

   public static class DamageSystem {
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthHead = 4;
      @RequiresWorldRestart
      public boolean causeDeathHead = true;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthLeftArm = 4;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthLeftLeg = 4;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthLeftFoot = 4;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthBody = 6;
      @RequiresWorldRestart
      public boolean causeDeathBody = true;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthRightArm = 4;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthRightLeg = 4;
      @RangeInt(
         min = 2,
         max = 12
      )
      public int maxHealthRightFoot = 4;

      public DamageSystem() {
      }
   }

   public static class Debuffs {
      public final FirstAidConfig.Debuffs.Head head = new FirstAidConfig.Debuffs.Head();
      public final FirstAidConfig.Debuffs.Body body = new FirstAidConfig.Debuffs.Body();
      public final FirstAidConfig.Debuffs.Arms arms = new FirstAidConfig.Debuffs.Arms();
      public final FirstAidConfig.Debuffs.LegsAndFeet legsAndFeet = new FirstAidConfig.Debuffs.LegsAndFeet();

      public Debuffs() {
      }

      public static class Arms {
         public boolean mining_fatigue = true;
         @Comment({"Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionConstant miningFatigueConditions = new FirstAidConfig.Debuffs.ConditionConstant(new float[]{0.25F, 0.5F, 0.75F}, new int[]{3, 2, 1});

         public Arms() {
         }
      }

      public static class Body {
         public boolean nausea = true;
         @Comment({"Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionOnHit nauseaConditions = new FirstAidConfig.Debuffs.ConditionOnHit(new float[]{4.0F, 2.0F}, new int[]{320, 160});
         public boolean weakness = true;
         @Comment({"Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionConstant weaknessConditions = new FirstAidConfig.Debuffs.ConditionConstant(new float[]{0.25F, 0.5F}, new int[]{2, 1});

         public Body() {
         }
      }

      public static class ConditionConstant {
         @RequiresMcRestart
         @Comment({"How much health the user must have left for the debuff to apply at the mapped length. Must be sorted so the **lowest** value comes first"})
         @RangeDouble(
            min = 0.0D,
            max = 1.0D
         )
         public float[] healthPercentageLeft;
         @RequiresMcRestart
         @Comment({"How strong the potion effect should stay. If the first condition from the healthPercentageLeft config is met, the first value in this list will be taken"})
         @RangeInt(
            min = 0,
            max = 127
         )
         public int[] debuffStrength;

         public ConditionConstant(float[] defaultPercentage, int[] defaultStrength) {
            this.healthPercentageLeft = defaultPercentage;
            this.debuffStrength = defaultStrength;
         }
      }

      public static class ConditionOnHit {
         @RequiresMcRestart
         @Comment({"How much damage the user must have taken for the debuff to apply at the mapped length. Must be sorted so the **highest** value comes first. 2 = 1 heart"})
         @RangeDouble(
            min = 0.0D,
            max = 10.0D
         )
         public float[] damageTaken;
         @RequiresMcRestart
         @Comment({"How long the debuff should stay. If the first condition from the damageTaken config is met, the first value in this list will be taken"})
         @RangeInt(
            min = 0,
            max = 32767
         )
         public int[] debuffLength;

         public ConditionOnHit(float[] defaultTaken, int[] defaultLength) {
            this.damageTaken = defaultTaken;
            this.debuffLength = defaultLength;
         }
      }

      public static class Head {
         public boolean blindness = true;
         @Comment({"Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionOnHit blindnessConditions = new FirstAidConfig.Debuffs.ConditionOnHit(new float[]{2.0F, 1.0F}, new int[]{160, 80});
         public boolean nausea = true;
         @Comment({"Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionOnHit nauseaConditions = new FirstAidConfig.Debuffs.ConditionOnHit(new float[]{3.0F, 2.0F}, new int[]{320, 240});

         public Head() {
         }
      }

      public static class LegsAndFeet {
         public boolean slowness = true;
         @Comment({"Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing."})
         public final FirstAidConfig.Debuffs.ConditionConstant slownessConditions = new FirstAidConfig.Debuffs.ConditionConstant(new float[]{0.35F, 0.6F, 0.8F}, new int[]{3, 2, 1});

         public LegsAndFeet() {
         }
      }
   }

   public static class EnchantmentHandling {
      @Comment({"If set to LOCAL_ENCHANTMENTS, only the enchantments for the armor for the body part that is currently being damaged is taken into account. The strength of the armor is multiplied by 4, so it matches the vanilla default\nIf set to GLOBAL_ENCHANTMENTS, the enchantments of all armor pieces are taken into account for all body parts that have any kind of armor."})
      public FirstAidConfig.EnchantmentHandling.ArmorEnchantmentMode armorEnchantmentMode = FirstAidConfig.EnchantmentHandling.ArmorEnchantmentMode.LOCAL_ENCHANTMENTS;
      @RangeInt(
         min = 1,
         max = 4
      )
      @Comment({"Specifies the default modifier. This is only used if armorEnchantmentMode is LOCAL_ENCHANTMENTS, as this is used scale up the values to somewhat match what vanilla balances around (as vanilla balances around global enchantments)"})
      public int enchantmentMultiplier = 4;
      @Comment({"Specifies the overrides for the the modifier. This is only used if armorEnchantmentMode is LOCAL_ENCHANTMENTS. This can be used to set another multiplier for special enchantments"})
      @ExtraConfig.Advanced
      public FirstAidConfig.EnchantmentHandling.OverrideEntries overrideEntries = new FirstAidConfig.EnchantmentHandling.OverrideEntries();

      public EnchantmentHandling() {
      }

      public static enum ArmorEnchantmentMode {
         GLOBAL_ENCHANTMENTS,
         LOCAL_ENCHANTMENTS;

         private ArmorEnchantmentMode() {
         }
      }

      public static class OverrideEntries {
         @Comment({"The resource location of the enchantment. Must be fully specified and cannot use wildcard. Example: minecraft:feather_falling"})
         public String[] resourceLocation = new String[]{"minecraft:feather_falling"};
         @RangeInt(
            min = 1,
            max = 4
         )
         public int[] multiplierOverride = new int[]{1};

         public OverrideEntries() {
         }
      }
   }

   public static class ExternalHealing {
      @Comment({"Allow vanilla's natural regeneration. Requires \"allowOtherHealingItems\" to be true\n**WARNING** This sets the gamerule \"naturalRegeneration\" for all of your worlds internally, so it persists even if you remove the mod"})
      @LangKey("firstaid.config.allownaturalregeneration")
      @RequiresWorldRestart
      public boolean allowNaturalRegeneration = false;
      @Comment({"If false, healing potions and other healing items will have no effect"})
      @LangKey("firstaid.config.allowotherhealingitems")
      public boolean allowOtherHealingItems = true;
      @Comment({"Specifies how much percent of the max health should be restored when sleeping"})
      @LangKey("firstaid.config.sleephealpercentage")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double sleepHealPercentage = 0.07D;
      @Comment({"The value external regen will be multiplied with. Has no effect if \"allowOtherHealingItems\" is disabled"})
      @LangKey("firstaid.config.otherregenmultiplier")
      @RangeDouble(
         min = 0.0D,
         max = 20.0D
      )
      @ExtraConfig.Advanced
      public double otherRegenMultiplier = 0.75D;
      @Comment({"The value vanilla's natural regeneration will be multiplied with. Has no effect if \"allowNaturalRegeneration\" is disabled"})
      @LangKey("firstaid.config.naturalregenmultiplier")
      @RangeDouble(
         min = 0.0D,
         max = 20.0D
      )
      @ExtraConfig.Advanced
      public double naturalRegenMultiplier = 0.5D;
      @Comment({"Specifies how the vanilla resistance potion effect should reduce damage.\nBy default, one level of resistance reduces 20% of damage. Changing this value to e.g. 10 will reduce the reduction to 10% damage reduction per level"})
      @LangKey("firstaid.config.resistancereductionpercentperlevel")
      @RangeInt(
         min = 1,
         max = 40
      )
      public int resistanceReductionPercentPerLevel = 20;

      public ExternalHealing() {
      }
   }

   public static class InternalHealing {
      @Comment({"Settings for the bandage item"})
      @LangKey("firstaid.config.bandage")
      public final FirstAidConfig.InternalHealing.Entry bandage = new FirstAidConfig.InternalHealing.Entry(4, 18, 2500);
      @Comment({"Settings for the plaster item"})
      @LangKey("firstaid.config.plaster")
      public final FirstAidConfig.InternalHealing.Entry plaster = new FirstAidConfig.InternalHealing.Entry(2, 22, 3000);

      public InternalHealing() {
      }

      public static class Entry {
         @Comment({"The total heals this item does when applied. 1 heal = half a heart"})
         @LangKey("firstaid.config.totalheals")
         @RangeInt(
            min = 1,
            max = 127
         )
         public int totalHeals;
         @Comment({"The time it takes for a single heal to trigger. Total time this item is active = this * totalHeals"})
         @LangKey("firstaid.config.secondsperheal")
         @RangeInt(
            min = 1,
            max = 32767
         )
         public int secondsPerHeal;
         @Comment({"The time it takes in the GUI to apply the item in milliseconds"})
         @LangKey("firstaid.config.applytime")
         @RangeInt(
            min = 0,
            max = 16000
         )
         public int applyTime;

         public Entry(int initialTotalHeals, int initialSecondsPerHeal, int initialApplyTime) {
            this.totalHeals = initialTotalHeals;
            this.secondsPerHeal = initialSecondsPerHeal;
            this.applyTime = initialApplyTime;
         }
      }
   }

   public static class LocationalArmor {
      public final FirstAidConfig.LocationalArmor.Armor armor = new FirstAidConfig.LocationalArmor.Armor();
      public final FirstAidConfig.LocationalArmor.Toughness toughness = new FirstAidConfig.LocationalArmor.Toughness();

      public LocationalArmor() {
      }

      public static class Armor {
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double headArmorMultiplier = 6.0D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double chestArmorMultiplier = 2.5D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double legsArmorMultiplier = 3.0D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double feetArmorMultiplier = 6.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double headArmorOffset = 1.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double chestArmorOffset = 0.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double legsArmorOffset = 0.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double feetArmorOffset = 0.0D;

         public Armor() {
         }
      }

      public static class Toughness {
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double headToughnessMultiplier = 4.0D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double chestToughnessMultiplier = 3.0D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double legsToughnessMultiplier = 3.0D;
         @RangeDouble(
            min = 1.0D,
            max = 16.0D
         )
         public double feetToughnessMultiplier = 3.5D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double headToughnessOffset = 0.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double chestToughnessOffset = 0.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double legsToughnessOffset = 0.0D;
         @RangeDouble(
            min = 0.0D,
            max = 4.0D
         )
         public double feetToughnessOffset = 0.0D;

         public Toughness() {
         }
      }
   }

   public static class Overlay {
      @Comment({"The mode how to show the mc vanilla health bar.\nNORMAL shows the vanilla health value that calculated the way vanillaHealthCalculation is specified (server config)\nHIGHLIGHT_CRITICAL_PATH show the vanilla health value and highlights the health value of the most damaged critical limb (by default head or body) using the hardcore-styled hearts for them and the normal hearts for the rest of the health. Only works if vanillaHealthCalculation is set to AVERAGE_ALL\nHIDE just doesn't display the vanilla health bar at all."})
      @LangKey("firstaid.config.vanillahealthbarmode")
      public FirstAidConfig.Overlay.VanillaHealthbarMode vanillaHealthBarMode = FirstAidConfig.Overlay.VanillaHealthbarMode.HIDE;
      @Comment({"Specifies when and how the HUD should be displayed"})
      @LangKey("firstaid.config.displaymode")
      public FirstAidConfig.Overlay.DisplayMode displayMode = new FirstAidConfig.Overlay.DisplayMode(-1, true);
      @Comment({"Specifies the type of the overlay HUD."})
      @LangKey("firstaid.config.overlaymode")
      public FirstAidConfig.Overlay.OverlayMode overlayMode = FirstAidConfig.Overlay.OverlayMode.PLAYER_MODEL;
      @Comment({"The relative point of the overlay"})
      @LangKey("firstaid.config.position")
      public FirstAidConfig.Overlay.Position pos = FirstAidConfig.Overlay.Position.TOP_LEFT;
      @Comment({"The offset on the x axis"})
      @LangKey("firstaid.config.xoffset")
      @ExtraConfig.Advanced
      public int xOffset = 0;
      @Comment({"The offset on the y axis"})
      @LangKey("firstaid.config.yoffset")
      @ExtraConfig.Advanced
      public int yOffset = 1;
      @Comment({"Determines the transparency of the overlay. 200 = Maximum transparency, 0 = Fully opaque"})
      @LangKey("firstaid.config.alpha")
      @RangeInt(
         min = 0,
         max = 200
      )
      @ExtraConfig.Advanced
      public int alpha = 50;
      @Comment({"Determines how first aid should display armor on item tooltips.\nREPLACE replaces the vanilla description with the one fitting first aid\nAPPEND will add the first aid values at the bottom\nNONE will show the old vanilla values. Be advised this is purly visual, interally, the first aid value will always be used"})
      @ExtraConfig.Advanced
      public FirstAidConfig.Overlay.TooltipMode armorTooltipMode = FirstAidConfig.Overlay.TooltipMode.REPLACE;
      @Comment({"Disables the funny easter eggs on certain events"})
      @ExtraConfig.Advanced
      public boolean enableEasterEggs = true;

      public Overlay() {
      }

      public static class DisplayMode {
         @Comment({"Specifies how many ticks (20 ticks = 1 second) the overlay should be visible after health changed (healing/damaging)\nIf set to -1, the HUD is always visible"})
         @LangKey("firstaid.config.visibledurationticks")
         @RangeInt(
            min = -1,
            max = 600
         )
         public int visibleDurationTicks;
         @Comment({"If set to true, the overlay will flash for a short moment if the health changed. Only affects PLAYER_MODEL overlay"})
         @LangKey("firstaid.config.flash")
         public boolean flash;

         private DisplayMode(int visibleDurationTicks, boolean flash) {
            this.visibleDurationTicks = visibleDurationTicks;
            this.flash = flash;
         }
      }

      public static enum OverlayMode {
         OFF,
         NUMBERS,
         HEARTS,
         PLAYER_MODEL,
         PLAYER_MODEL_4_COLORS,
         PLAYER_MODEL_OLD_COLORS,
         PLAYER_MODEL_OLD_4_COLORS;

         private OverlayMode() {
         }

         public boolean isPlayerModel() {
            return this == PLAYER_MODEL || this == PLAYER_MODEL_4_COLORS || this == PLAYER_MODEL_OLD_COLORS || this == PLAYER_MODEL_OLD_4_COLORS;
         }
      }

      public static enum Position {
         TOP_LEFT,
         TOP_RIGHT,
         BOTTOM_RIGHT,
         BOTTOM_LEFT;

         private Position() {
         }
      }

      public static enum TooltipMode {
         REPLACE,
         APPEND,
         NONE;

         private TooltipMode() {
         }
      }

      public static enum VanillaHealthbarMode {
         NORMAL,
         HIGHLIGHT_CRITICAL_PATH,
         HIDE;

         private VanillaHealthbarMode() {
         }
      }
   }

   public static enum VanillaHealthCalculationMode {
      AVERAGE_ALL,
      AVERAGE_CRITICAL,
      MIN_CRITICAL,
      CRITICAL_50_PERCENT_OTHER_50_PERCENT;

      private VanillaHealthCalculationMode() {
      }
   }

   public static void register() {
      ConfigManager.register("firstaid", "firstaid", "general", null,
         ConfigEntry.category("damageSystem",
            ConfigEntry.value("maxHealthHead", int.class, () -> FirstAidConfig.damageSystem.maxHealthHead, v -> FirstAidConfig.damageSystem.maxHealthHead = (Integer)v).range(2, 12),
            ConfigEntry.value("causeDeathHead", boolean.class, () -> FirstAidConfig.damageSystem.causeDeathHead, v -> FirstAidConfig.damageSystem.causeDeathHead = (Boolean)v).requiresWorldRestart(),
            ConfigEntry.value("maxHealthLeftArm", int.class, () -> FirstAidConfig.damageSystem.maxHealthLeftArm, v -> FirstAidConfig.damageSystem.maxHealthLeftArm = (Integer)v).range(2, 12),
            ConfigEntry.value("maxHealthLeftLeg", int.class, () -> FirstAidConfig.damageSystem.maxHealthLeftLeg, v -> FirstAidConfig.damageSystem.maxHealthLeftLeg = (Integer)v).range(2, 12),
            ConfigEntry.value("maxHealthLeftFoot", int.class, () -> FirstAidConfig.damageSystem.maxHealthLeftFoot, v -> FirstAidConfig.damageSystem.maxHealthLeftFoot = (Integer)v).range(2, 12),
            ConfigEntry.value("maxHealthBody", int.class, () -> FirstAidConfig.damageSystem.maxHealthBody, v -> FirstAidConfig.damageSystem.maxHealthBody = (Integer)v).range(2, 12),
            ConfigEntry.value("causeDeathBody", boolean.class, () -> FirstAidConfig.damageSystem.causeDeathBody, v -> FirstAidConfig.damageSystem.causeDeathBody = (Boolean)v).requiresWorldRestart(),
            ConfigEntry.value("maxHealthRightArm", int.class, () -> FirstAidConfig.damageSystem.maxHealthRightArm, v -> FirstAidConfig.damageSystem.maxHealthRightArm = (Integer)v).range(2, 12),
            ConfigEntry.value("maxHealthRightLeg", int.class, () -> FirstAidConfig.damageSystem.maxHealthRightLeg, v -> FirstAidConfig.damageSystem.maxHealthRightLeg = (Integer)v).range(2, 12),
            ConfigEntry.value("maxHealthRightFoot", int.class, () -> FirstAidConfig.damageSystem.maxHealthRightFoot, v -> FirstAidConfig.damageSystem.maxHealthRightFoot = (Integer)v).range(2, 12)).comment("Settings regarding the max health of the body's parts. 2 = 1 heart").langKey("firstaid.config.damagesystem").requiresWorldRestart(),
         ConfigEntry.category("overlay",
            ConfigEntry.enumValue("vanillaHealthBarMode", ichttt.mods.firstaid.FirstAidConfig.Overlay.VanillaHealthbarMode.values(), () -> FirstAidConfig.overlay.vanillaHealthBarMode, v -> FirstAidConfig.overlay.vanillaHealthBarMode = v).comment("The mode how to show the mc vanilla health bar.\nNORMAL shows the vanilla health value that calculated the way vanillaHealthCalculation is specified (server config)\nHIGHLIGHT_CRITICAL_PATH show the vanilla health value and highlights the health value of the most damaged critical limb (by default head or body) using the hardcore-styled hearts for them and the normal hearts for the rest of the health. Only works if vanillaHealthCalculation is set to AVERAGE_ALL\nHIDE just doesn't display the vanilla health bar at all.").langKey("firstaid.config.vanillahealthbarmode"),
            ConfigEntry.category("displayMode",
               ConfigEntry.value("visibleDurationTicks", int.class, () -> FirstAidConfig.overlay.displayMode.visibleDurationTicks, v -> FirstAidConfig.overlay.displayMode.visibleDurationTicks = (Integer)v).comment("Specifies how many ticks (20 ticks = 1 second) the overlay should be visible after health changed (healing/damaging)\nIf set to -1, the HUD is always visible").langKey("firstaid.config.visibledurationticks").range(-1, 600),
               ConfigEntry.value("flash", boolean.class, () -> FirstAidConfig.overlay.displayMode.flash, v -> FirstAidConfig.overlay.displayMode.flash = (Boolean)v).comment("If set to true, the overlay will flash for a short moment if the health changed. Only affects PLAYER_MODEL overlay").langKey("firstaid.config.flash")).comment("Specifies when and how the HUD should be displayed").langKey("firstaid.config.displaymode"),
            ConfigEntry.enumValue("overlayMode", ichttt.mods.firstaid.FirstAidConfig.Overlay.OverlayMode.values(), () -> FirstAidConfig.overlay.overlayMode, v -> FirstAidConfig.overlay.overlayMode = v).comment("Specifies the type of the overlay HUD.").langKey("firstaid.config.overlaymode"),
            ConfigEntry.enumValue("pos", ichttt.mods.firstaid.FirstAidConfig.Overlay.Position.values(), () -> FirstAidConfig.overlay.pos, v -> FirstAidConfig.overlay.pos = v).comment("The relative point of the overlay").langKey("firstaid.config.position"),
            ConfigEntry.value("xOffset", int.class, () -> FirstAidConfig.overlay.xOffset, v -> FirstAidConfig.overlay.xOffset = (Integer)v).comment("The offset on the x axis").langKey("firstaid.config.xoffset"),
            ConfigEntry.value("yOffset", int.class, () -> FirstAidConfig.overlay.yOffset, v -> FirstAidConfig.overlay.yOffset = (Integer)v).comment("The offset on the y axis").langKey("firstaid.config.yoffset"),
            ConfigEntry.value("alpha", int.class, () -> FirstAidConfig.overlay.alpha, v -> FirstAidConfig.overlay.alpha = (Integer)v).comment("Determines the transparency of the overlay. 200 = Maximum transparency, 0 = Fully opaque").langKey("firstaid.config.alpha").range(0, 200),
            ConfigEntry.enumValue("armorTooltipMode", ichttt.mods.firstaid.FirstAidConfig.Overlay.TooltipMode.values(), () -> FirstAidConfig.overlay.armorTooltipMode, v -> FirstAidConfig.overlay.armorTooltipMode = v).comment("Determines how first aid should display armor on item tooltips.\nREPLACE replaces the vanilla description with the one fitting first aid\nAPPEND will add the first aid values at the bottom\nNONE will show the old vanilla values. Be advised this is purly visual, interally, the first aid value will always be used"),
            ConfigEntry.value("enableEasterEggs", boolean.class, () -> FirstAidConfig.overlay.enableEasterEggs, v -> FirstAidConfig.overlay.enableEasterEggs = (Boolean)v).comment("Disables the funny easter eggs on certain events")).comment("Settings regarding the health overlay when ingame").langKey("firstaid.config.overlay"),
         ConfigEntry.category("internalHealing",
            ConfigEntry.category("bandage",
               ConfigEntry.value("totalHeals", int.class, () -> FirstAidConfig.internalHealing.bandage.totalHeals, v -> FirstAidConfig.internalHealing.bandage.totalHeals = (Integer)v).comment("The total heals this item does when applied. 1 heal = half a heart").langKey("firstaid.config.totalheals").range(1, 127),
               ConfigEntry.value("secondsPerHeal", int.class, () -> FirstAidConfig.internalHealing.bandage.secondsPerHeal, v -> FirstAidConfig.internalHealing.bandage.secondsPerHeal = (Integer)v).comment("The time it takes for a single heal to trigger. Total time this item is active = this * totalHeals").langKey("firstaid.config.secondsperheal").range(1, 32767),
               ConfigEntry.value("applyTime", int.class, () -> FirstAidConfig.internalHealing.bandage.applyTime, v -> FirstAidConfig.internalHealing.bandage.applyTime = (Integer)v).comment("The time it takes in the GUI to apply the item in milliseconds").langKey("firstaid.config.applytime").range(0, 16000)).comment("Settings for the bandage item").langKey("firstaid.config.bandage"),
            ConfigEntry.category("plaster",
               ConfigEntry.value("totalHeals", int.class, () -> FirstAidConfig.internalHealing.plaster.totalHeals, v -> FirstAidConfig.internalHealing.plaster.totalHeals = (Integer)v).comment("The total heals this item does when applied. 1 heal = half a heart").langKey("firstaid.config.totalheals").range(1, 127),
               ConfigEntry.value("secondsPerHeal", int.class, () -> FirstAidConfig.internalHealing.plaster.secondsPerHeal, v -> FirstAidConfig.internalHealing.plaster.secondsPerHeal = (Integer)v).comment("The time it takes for a single heal to trigger. Total time this item is active = this * totalHeals").langKey("firstaid.config.secondsperheal").range(1, 32767),
               ConfigEntry.value("applyTime", int.class, () -> FirstAidConfig.internalHealing.plaster.applyTime, v -> FirstAidConfig.internalHealing.plaster.applyTime = (Integer)v).comment("The time it takes in the GUI to apply the item in milliseconds").langKey("firstaid.config.applytime").range(0, 16000)).comment("Settings for the plaster item").langKey("firstaid.config.plaster")).comment("Settings regarding the internal healing system").langKey("firstaid.config.internalhealing").requiresWorldRestart(),
         ConfigEntry.category("externalHealing",
            ConfigEntry.value("allowNaturalRegeneration", boolean.class, () -> FirstAidConfig.externalHealing.allowNaturalRegeneration, v -> FirstAidConfig.externalHealing.allowNaturalRegeneration = (Boolean)v).comment("Allow vanilla's natural regeneration. Requires \"allowOtherHealingItems\" to be true\n**WARNING** This sets the gamerule \"naturalRegeneration\" for all of your worlds internally, so it persists even if you remove the mod").langKey("firstaid.config.allownaturalregeneration").requiresWorldRestart(),
            ConfigEntry.value("allowOtherHealingItems", boolean.class, () -> FirstAidConfig.externalHealing.allowOtherHealingItems, v -> FirstAidConfig.externalHealing.allowOtherHealingItems = (Boolean)v).comment("If false, healing potions and other healing items will have no effect").langKey("firstaid.config.allowotherhealingitems"),
            ConfigEntry.value("sleepHealPercentage", double.class, () -> FirstAidConfig.externalHealing.sleepHealPercentage, v -> FirstAidConfig.externalHealing.sleepHealPercentage = (Double)v).comment("Specifies how much percent of the max health should be restored when sleeping").langKey("firstaid.config.sleephealpercentage").range(0.0, 1.0),
            ConfigEntry.value("otherRegenMultiplier", double.class, () -> FirstAidConfig.externalHealing.otherRegenMultiplier, v -> FirstAidConfig.externalHealing.otherRegenMultiplier = (Double)v).comment("The value external regen will be multiplied with. Has no effect if \"allowOtherHealingItems\" is disabled").langKey("firstaid.config.otherregenmultiplier").range(0.0, 20.0),
            ConfigEntry.value("naturalRegenMultiplier", double.class, () -> FirstAidConfig.externalHealing.naturalRegenMultiplier, v -> FirstAidConfig.externalHealing.naturalRegenMultiplier = (Double)v).comment("The value vanilla's natural regeneration will be multiplied with. Has no effect if \"allowNaturalRegeneration\" is disabled").langKey("firstaid.config.naturalregenmultiplier").range(0.0, 20.0),
            ConfigEntry.value("resistanceReductionPercentPerLevel", int.class, () -> FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel, v -> FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel = (Integer)v).comment("Specifies how the vanilla resistance potion effect should reduce damage.\nBy default, one level of resistance reduces 20% of damage. Changing this value to e.g. 10 will reduce the reduction to 10% damage reduction per level").langKey("firstaid.config.resistancereductionpercentperlevel").range(1, 40)).comment("Settings regarding external healing system(like vanilla potions or natural regeneration").langKey("firstaid.config.externalhealing").requiresWorldRestart(),
         ConfigEntry.category("debuffs",
            ConfigEntry.category("head",
               ConfigEntry.value("blindness", boolean.class, () -> FirstAidConfig.debuffs.head.blindness, v -> FirstAidConfig.debuffs.head.blindness = (Boolean)v),
               ConfigEntry.category("blindnessConditions",
                  ConfigEntry.value("damageTaken", float[].class, () -> FirstAidConfig.debuffs.head.blindnessConditions.damageTaken, v -> FirstAidConfig.debuffs.head.blindnessConditions.damageTaken = (float[])v).comment("How much damage the user must have taken for the debuff to apply at the mapped length. Must be sorted so the **highest** value comes first. 2 = 1 heart").range(0.0, 10.0).requiresMcRestart(),
                  ConfigEntry.value("debuffLength", int[].class, () -> FirstAidConfig.debuffs.head.blindnessConditions.debuffLength, v -> FirstAidConfig.debuffs.head.blindnessConditions.debuffLength = (int[])v).comment("How long the debuff should stay. If the first condition from the damageTaken config is met, the first value in this list will be taken").range(0, 32767).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing."),
               ConfigEntry.value("nausea", boolean.class, () -> FirstAidConfig.debuffs.head.nausea, v -> FirstAidConfig.debuffs.head.nausea = (Boolean)v),
               ConfigEntry.category("nauseaConditions",
                  ConfigEntry.value("damageTaken", float[].class, () -> FirstAidConfig.debuffs.head.nauseaConditions.damageTaken, v -> FirstAidConfig.debuffs.head.nauseaConditions.damageTaken = (float[])v).comment("How much damage the user must have taken for the debuff to apply at the mapped length. Must be sorted so the **highest** value comes first. 2 = 1 heart").range(0.0, 10.0).requiresMcRestart(),
                  ConfigEntry.value("debuffLength", int[].class, () -> FirstAidConfig.debuffs.head.nauseaConditions.debuffLength, v -> FirstAidConfig.debuffs.head.nauseaConditions.debuffLength = (int[])v).comment("How long the debuff should stay. If the first condition from the damageTaken config is met, the first value in this list will be taken").range(0, 32767).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing.")),
            ConfigEntry.category("body",
               ConfigEntry.value("nausea", boolean.class, () -> FirstAidConfig.debuffs.body.nausea, v -> FirstAidConfig.debuffs.body.nausea = (Boolean)v),
               ConfigEntry.category("nauseaConditions",
                  ConfigEntry.value("damageTaken", float[].class, () -> FirstAidConfig.debuffs.body.nauseaConditions.damageTaken, v -> FirstAidConfig.debuffs.body.nauseaConditions.damageTaken = (float[])v).comment("How much damage the user must have taken for the debuff to apply at the mapped length. Must be sorted so the **highest** value comes first. 2 = 1 heart").range(0.0, 10.0).requiresMcRestart(),
                  ConfigEntry.value("debuffLength", int[].class, () -> FirstAidConfig.debuffs.body.nauseaConditions.debuffLength, v -> FirstAidConfig.debuffs.body.nauseaConditions.debuffLength = (int[])v).comment("How long the debuff should stay. If the first condition from the damageTaken config is met, the first value in this list will be taken").range(0, 32767).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different damage taken. Only use this if you know what you are doing."),
               ConfigEntry.value("weakness", boolean.class, () -> FirstAidConfig.debuffs.body.weakness, v -> FirstAidConfig.debuffs.body.weakness = (Boolean)v),
               ConfigEntry.category("weaknessConditions",
                  ConfigEntry.value("healthPercentageLeft", float[].class, () -> FirstAidConfig.debuffs.body.weaknessConditions.healthPercentageLeft, v -> FirstAidConfig.debuffs.body.weaknessConditions.healthPercentageLeft = (float[])v).comment("How much health the user must have left for the debuff to apply at the mapped length. Must be sorted so the **lowest** value comes first").range(0.0, 1.0).requiresMcRestart(),
                  ConfigEntry.value("debuffStrength", int[].class, () -> FirstAidConfig.debuffs.body.weaknessConditions.debuffStrength, v -> FirstAidConfig.debuffs.body.weaknessConditions.debuffStrength = (int[])v).comment("How strong the potion effect should stay. If the first condition from the healthPercentageLeft config is met, the first value in this list will be taken").range(0, 127).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing.")),
            ConfigEntry.category("arms",
               ConfigEntry.value("mining_fatigue", boolean.class, () -> FirstAidConfig.debuffs.arms.mining_fatigue, v -> FirstAidConfig.debuffs.arms.mining_fatigue = (Boolean)v),
               ConfigEntry.category("miningFatigueConditions",
                  ConfigEntry.value("healthPercentageLeft", float[].class, () -> FirstAidConfig.debuffs.arms.miningFatigueConditions.healthPercentageLeft, v -> FirstAidConfig.debuffs.arms.miningFatigueConditions.healthPercentageLeft = (float[])v).comment("How much health the user must have left for the debuff to apply at the mapped length. Must be sorted so the **lowest** value comes first").range(0.0, 1.0).requiresMcRestart(),
                  ConfigEntry.value("debuffStrength", int[].class, () -> FirstAidConfig.debuffs.arms.miningFatigueConditions.debuffStrength, v -> FirstAidConfig.debuffs.arms.miningFatigueConditions.debuffStrength = (int[])v).comment("How strong the potion effect should stay. If the first condition from the healthPercentageLeft config is met, the first value in this list will be taken").range(0, 127).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing.")),
            ConfigEntry.category("legsAndFeet",
               ConfigEntry.value("slowness", boolean.class, () -> FirstAidConfig.debuffs.legsAndFeet.slowness, v -> FirstAidConfig.debuffs.legsAndFeet.slowness = (Boolean)v),
               ConfigEntry.category("slownessConditions",
                  ConfigEntry.value("healthPercentageLeft", float[].class, () -> FirstAidConfig.debuffs.legsAndFeet.slownessConditions.healthPercentageLeft, v -> FirstAidConfig.debuffs.legsAndFeet.slownessConditions.healthPercentageLeft = (float[])v).comment("How much health the user must have left for the debuff to apply at the mapped length. Must be sorted so the **lowest** value comes first").range(0.0, 1.0).requiresMcRestart(),
                  ConfigEntry.value("debuffStrength", int[].class, () -> FirstAidConfig.debuffs.legsAndFeet.slownessConditions.debuffStrength, v -> FirstAidConfig.debuffs.legsAndFeet.slownessConditions.debuffStrength = (int[])v).comment("How strong the potion effect should stay. If the first condition from the healthPercentageLeft config is met, the first value in this list will be taken").range(0, 127).requiresMcRestart()).comment("Holds the information how the debuff should be applied at different health left. Only use this if you know what you are doing."))).comment("Enable/Disable specify debuffs on specific body parts").langKey("firstaid.config.debuffs"),
         ConfigEntry.category("locationalArmor",
            ConfigEntry.category("armor",
               ConfigEntry.value("headArmorMultiplier", double.class, () -> FirstAidConfig.locationalArmor.armor.headArmorMultiplier, v -> FirstAidConfig.locationalArmor.armor.headArmorMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("chestArmorMultiplier", double.class, () -> FirstAidConfig.locationalArmor.armor.chestArmorMultiplier, v -> FirstAidConfig.locationalArmor.armor.chestArmorMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("legsArmorMultiplier", double.class, () -> FirstAidConfig.locationalArmor.armor.legsArmorMultiplier, v -> FirstAidConfig.locationalArmor.armor.legsArmorMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("feetArmorMultiplier", double.class, () -> FirstAidConfig.locationalArmor.armor.feetArmorMultiplier, v -> FirstAidConfig.locationalArmor.armor.feetArmorMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("headArmorOffset", double.class, () -> FirstAidConfig.locationalArmor.armor.headArmorOffset, v -> FirstAidConfig.locationalArmor.armor.headArmorOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("chestArmorOffset", double.class, () -> FirstAidConfig.locationalArmor.armor.chestArmorOffset, v -> FirstAidConfig.locationalArmor.armor.chestArmorOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("legsArmorOffset", double.class, () -> FirstAidConfig.locationalArmor.armor.legsArmorOffset, v -> FirstAidConfig.locationalArmor.armor.legsArmorOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("feetArmorOffset", double.class, () -> FirstAidConfig.locationalArmor.armor.feetArmorOffset, v -> FirstAidConfig.locationalArmor.armor.feetArmorOffset = (Double)v).range(0.0, 4.0)),
            ConfigEntry.category("toughness",
               ConfigEntry.value("headToughnessMultiplier", double.class, () -> FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier, v -> FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("chestToughnessMultiplier", double.class, () -> FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier, v -> FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("legsToughnessMultiplier", double.class, () -> FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier, v -> FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("feetToughnessMultiplier", double.class, () -> FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier, v -> FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier = (Double)v).range(1.0, 16.0),
               ConfigEntry.value("headToughnessOffset", double.class, () -> FirstAidConfig.locationalArmor.toughness.headToughnessOffset, v -> FirstAidConfig.locationalArmor.toughness.headToughnessOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("chestToughnessOffset", double.class, () -> FirstAidConfig.locationalArmor.toughness.chestToughnessOffset, v -> FirstAidConfig.locationalArmor.toughness.chestToughnessOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("legsToughnessOffset", double.class, () -> FirstAidConfig.locationalArmor.toughness.legsToughnessOffset, v -> FirstAidConfig.locationalArmor.toughness.legsToughnessOffset = (Double)v).range(0.0, 4.0),
               ConfigEntry.value("feetToughnessOffset", double.class, () -> FirstAidConfig.locationalArmor.toughness.feetToughnessOffset, v -> FirstAidConfig.locationalArmor.toughness.feetToughnessOffset = (Double)v).range(0.0, 4.0))).comment("Specify how the locational armor is calculated").langKey("firstaid.config.locationalarmor").requiresWorldRestart(),
         ConfigEntry.category("enchantmentHandling",
            ConfigEntry.enumValue("armorEnchantmentMode", ichttt.mods.firstaid.FirstAidConfig.EnchantmentHandling.ArmorEnchantmentMode.values(), () -> FirstAidConfig.enchantmentHandling.armorEnchantmentMode, v -> FirstAidConfig.enchantmentHandling.armorEnchantmentMode = v).comment("If set to LOCAL_ENCHANTMENTS, only the enchantments for the armor for the body part that is currently being damaged is taken into account. The strength of the armor is multiplied by 4, so it matches the vanilla default\nIf set to GLOBAL_ENCHANTMENTS, the enchantments of all armor pieces are taken into account for all body parts that have any kind of armor."),
            ConfigEntry.value("enchantmentMultiplier", int.class, () -> FirstAidConfig.enchantmentHandling.enchantmentMultiplier, v -> FirstAidConfig.enchantmentHandling.enchantmentMultiplier = (Integer)v).comment("Specifies the default modifier. This is only used if armorEnchantmentMode is LOCAL_ENCHANTMENTS, as this is used scale up the values to somewhat match what vanilla balances around (as vanilla balances around global enchantments)").range(1, 4),
            ConfigEntry.category("overrideEntries",
               ConfigEntry.value("resourceLocation", String[].class, () -> FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation, v -> FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation = (String[])v).comment("The resource location of the enchantment. Must be fully specified and cannot use wildcard. Example: minecraft:feather_falling"),
               ConfigEntry.value("multiplierOverride", int[].class, () -> FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride, v -> FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride = (int[])v).range(1, 4)).comment("Specifies the overrides for the the modifier. This is only used if armorEnchantmentMode is LOCAL_ENCHANTMENTS. This can be used to set another multiplier for special enchantments")).comment("Specify how enchantments are applied").langKey("firstaid.config.enchantmenthandling"),
         ConfigEntry.value("enableSoundSystem", boolean.class, () -> FirstAidConfig.enableSoundSystem, v -> FirstAidConfig.enableSoundSystem = (Boolean)v).comment("Set to true to enable the debuff sounds. Requieres enableDebuffs to be true").langKey("firstaid.config.enablesoundsystem"),
         ConfigEntry.value("scaleMaxHealth", boolean.class, () -> FirstAidConfig.scaleMaxHealth, v -> FirstAidConfig.scaleMaxHealth = (Boolean)v).comment("If true, max health is scaled to your hearts, and the config entries get multiplier to match the max health").langKey("firstaid.config.scalemaxhealth").requiresWorldRestart(),
         ConfigEntry.value("capMaxHealth", boolean.class, () -> FirstAidConfig.capMaxHealth, v -> FirstAidConfig.capMaxHealth = (Boolean)v).comment("If true, max health will be capped at 6 hearts and absorption at 2 hearts per limb. If false, the health cap will be much higher (64 hearts normal and 16 absorption)").langKey("firstaid.config.capmaxhealth").requiresWorldRestart(),
         ConfigEntry.value("watchSetHealth", boolean.class, () -> FirstAidConfig.watchSetHealth, v -> FirstAidConfig.watchSetHealth = (Boolean)v).comment("If true, all usages of setHealth from other mods will be captured. Should not cause any problems, but allow mods like scaling health bandages to apply").langKey("firstaid.config.sethealth"),
         ConfigEntry.value("hardMode", boolean.class, () -> FirstAidConfig.hardMode, v -> FirstAidConfig.hardMode = (Boolean)v).comment("If true, many damage distributions will be more realistic, but this will also cause them to be harder\nIf enabled, e.g. drowing will only damage your body instead of your body and head last").langKey("firstaid.config.hardmode").requiresMcRestart(),
         ConfigEntry.enumValue("vanillaHealthCalculation", ichttt.mods.firstaid.FirstAidConfig.VanillaHealthCalculationMode.values(), () -> FirstAidConfig.vanillaHealthCalculation, v -> FirstAidConfig.vanillaHealthCalculation = v).comment("Specifies how the vanilla health is calculated. Affects the vanilla visual health bar, as well as the value other mods get to see when they query the player health.\nAVERAGE_ALL simply takes all limbs and calculates the average of it.\nAVERAGE_CRITICAL takes all critical limbs and calculates the average of it\nMIN_CRITICAl takes the smallest health value of all critical limb\nDoes not have any effect if all critical limbs have been disabled.").requiresWorldRestart(),
         ConfigEntry.value("useFriendlyRandomDistribution", boolean.class, () -> FirstAidConfig.useFriendlyRandomDistribution, v -> FirstAidConfig.useFriendlyRandomDistribution = (Boolean)v).comment("Only effects the fallback random distribution.\nIf enabled, the default random damage distribution will be changed to leave critical limbs at 1hp if possible.\nWhen there is too much damage, the damage will still kill the player. Other distributions that defined are not affected by this."),
         ConfigEntry.value("debug", boolean.class, () -> FirstAidConfig.debug, v -> FirstAidConfig.debug = (Boolean)v).comment("Enabled additional debug logs - May slow down the game and will increase log file size\nOnly enable for special purposes").langKey("firstaid.config.debug").requiresMcRestart());
   }
}
