package lumberwizard.anvilpatch;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.fml.common.Loader;

@Config(
   modid = "anvilpatch"
)
public class ModConfig {
   @Ignore
   private static boolean valuesOverridden = false;
   @Comment({"Set to -1 to remove the cap.", "This setting will be ignored if Apotheosis is installed."})
   @Name("New level cap")
   @RangeInt(
      min = -1
   )
   public static int levelCap = -1;
   @Ignore
   private static int syncedLevelCap = levelCap;
   @Comment({"Valid values:", "KEEP - keeps the cumulative repair cost, same as vanilla.", "REMOVE_REPAIR_SCALING - repairs won't increase the xp cost, but enchantments will, even for repaired items.", "ENCHANTMENT_ONLY - repairs will always cost the same, but applying more enchantments will cost more.", "REMOVE - removes the cumulative repair cost entirely."})
   @Name("XP cost increase")
   public static ModConfig.EnumCostIncreaseSetting costIncreaseSetting = ModConfig.EnumCostIncreaseSetting.REMOVE_REPAIR_SCALING;
   @Ignore
   private static ModConfig.EnumCostIncreaseSetting syncedCostIncreaseSetting;
   @Comment({"Vanilla behavior, when merging two items where the one on the right has an enchantment with a level beyond its max", "(for example power 8) is to reset the level to the max (so the resulting item would have power 5)", "This setting disables that.", "It does not, however, allow creating enchantments beyond the cap", "So, for example, merging two power V books would still result in power V"})
   @Name("Allow repairs beyond enchantment level cap.")
   public static boolean breakEnchantLevelCap = false;
   @Ignore
   private static boolean syncedBreakEnchantLevelCap;
   @Comment({"Chance for the anvil to reach the next stage of breakage per use.", "Choose a value from 0 to 1. Vanilla default is 0.12 (= 12%)."})
   @Name("Anvil break chance")
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public static float breakChance = 0.12F;
   @Ignore
   private static float syncedBreakChance = breakChance;

   public ModConfig() {
   }

   public static void setSyncedValues(int levelCap, ModConfig.EnumCostIncreaseSetting costIncreaseSetting, boolean breakEnchantLevelCap, float breakChance) {
      syncedLevelCap = levelCap;
      syncedCostIncreaseSetting = costIncreaseSetting;
      syncedBreakEnchantLevelCap = breakEnchantLevelCap;
      syncedBreakChance = breakChance;
   }

   public static int getLevelCap() {
      if (Loader.isModLoaded("apotheosis")) {
         return -1;
      } else {
         return areValuesOverridden() ? syncedLevelCap : levelCap;
      }
   }

   public static ModConfig.EnumCostIncreaseSetting getCostIncreaseSetting() {
      return areValuesOverridden() ? syncedCostIncreaseSetting : costIncreaseSetting;
   }

   public static boolean shouldBreakEnchantLevelCap() {
      return areValuesOverridden() ? syncedBreakEnchantLevelCap : breakEnchantLevelCap;
   }

   public static float getBreakChance() {
      return areValuesOverridden() ? syncedBreakChance : breakChance;
   }

   public static boolean areValuesOverridden() {
      return valuesOverridden;
   }

   public static void setValuesOverridden(boolean valuesOverridden) {
      valuesOverridden = valuesOverridden;
   }

   public static enum EnumCostIncreaseSetting {
      KEEP,
      REMOVE_REPAIR_SCALING,
      ENCHANTMENT_ONLY,
      REMOVE;

      private EnumCostIncreaseSetting() {
      }
   }
}
