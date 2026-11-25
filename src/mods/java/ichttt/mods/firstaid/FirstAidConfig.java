package ichttt.mods.firstaid;

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
         public final int visibleDurationTicks;
         @Comment({"If set to true, the overlay will flash for a short moment if the health changed. Only affects PLAYER_MODEL overlay"})
         @LangKey("firstaid.config.flash")
         public final boolean flash;

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
}
