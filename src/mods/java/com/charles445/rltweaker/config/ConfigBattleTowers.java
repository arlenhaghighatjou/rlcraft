package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigBattleTowers {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Make tower explosions belong to nobody, avoids Infernal Mobs issues"})
   @Name("Change Tower Explosion Owner")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean towerExplosionNoCredit = false;
   @Comment({"Golems who are attacked will get angry more consistently, and from farther away"})
   @Name("Golem High Aggression")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemHighAggression = false;
   @Comment({"Make golems stop building up speed when dormant"})
   @Name("Golem Dormant Speed Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemDormantSpeedFix = false;
   @Comment({"Prevent golems from drowning"})
   @Name("Golem Drowning Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemDrowningFix = false;
   @Comment({"Prevent golems from suffocating in walls"})
   @Name("Golem Suffocating Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemSuffocatingFix = false;
   @Comment({"Prevent falling block damage for golems"})
   @Name("Golem Falling Block Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemFallingBlockFix = false;
   @Comment({"Prevent anvil damage for golems"})
   @Name("Golem Anvil Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemAnvilFix = false;
   @Comment({"Prevent golems taking damage from lycanites fluids"})
   @Name("Golem Lycanites Fluid Fix")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemLycanitesFluidFix = false;
   @Comment({"Max Golem XZ movement to make using tools like fishing rods harder. Set to a negative number to disable"})
   @Name("Golem Speed Cap")
   @RLConfig.ImprovementsOnly("-1.0")
   @RLConfig.RLCraftTwoEightTwo("-1.0")
   @RLConfig.RLCraftTwoNine("0.125d")
   @RangeDouble(
      min = -1.0D
   )
   public double golemSpeedCap = -1.0D;
   @Comment({"Max Golem Y movement (upwards) to make launching him harder. Set to a negative number to disable"})
   @Name("Golem Speed Cap Upwards")
   @RLConfig.ImprovementsOnly("-1.0")
   @RLConfig.RLCraftTwoEightTwo("-1.0")
   @RLConfig.RLCraftTwoNine("-1.0")
   @RangeDouble(
      min = -1.0D
   )
   public double golemSpeedCapUpwards = -1.0D;
   @Comment({"Prevent golems from riding anything, like boats or minecarts"})
   @Name("Golem Auto Dismount")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemAutoDismount = false;
   @Comment({"Replace the golem's projectile with a LycanitesMobs Projectile (Requires LycanitesMobs)"})
   @Name("Golem Lycanites Projectile")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean golemLycanitesProjectile = false;
   @Comment({"The name of the projectile to use"})
   @Name("Golem Lycanites Projectile Name")
   @RLConfig.ImprovementsOnly("demonicblast")
   @RLConfig.RLCraftTwoEightTwo("demonicblast")
   @RLConfig.RLCraftTwoNine("demonicblast")
   public String golemLycanitesProjectileName = "demonicblast";
   @Comment({"Scale modifier for the lycanites projectile"})
   @Name("Golem Lycanites Projectile Scale Modifier")
   @RLConfig.ImprovementsOnly("1.0")
   @RLConfig.RLCraftTwoEightTwo("1.0")
   @RLConfig.RLCraftTwoNine("1.0")
   public double golemLycanitesProjectileScaleModifier = 1.0D;
   @Comment({"Speed modifier for the lycanites projectile"})
   @Name("Golem Lycanites Projectile Speed Modifier")
   @RLConfig.ImprovementsOnly("9.0")
   @RLConfig.RLCraftTwoEightTwo("9.0")
   @RLConfig.RLCraftTwoNine("9.0")
   public double golemLycanitesProjectileSpeedModifier = 9.0D;
   @Comment({"Use dimension blacklist"})
   @Name("Dimension Blacklist Enabled")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean dimensionBlacklistEnabled = false;
   @Comment({"Dimension blacklist of dimension IDs"})
   @Name("Dimension Blacklist")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("111")
   @RLConfig.RLCraftTwoEightTwo("111")
   @RLConfig.RLCraftTwoNine("111")
   public int[] dimensionBlacklistIds = new int[]{111};
   @Comment({"Whether the dimension blacklist is a whitelist"})
   @Name("Dimension Blacklist Is Whitelist")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean dimensionBlacklistIsWhitelist = false;
   @Comment({"Make towers generate consistently instead of as soon as possible, avoiding the need for the positions file. Be warned, this disables tower commands."})
   @Name("Consistent Tower Generation")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean consistentTowerGeneration = false;
   @Comment({"Removes tower destroyers from unloaded worlds. Prevents potential leaks and crashes."})
   @Name("Remove Unloaded Tower Destroyers")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean removeUnloadedTowerDestroyers = true;
   @Comment({"Prevents clients from changing the tower destruction config"})
   @Name("Enforce Tower Destruction Config")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enforceTowerDestructionConfig = true;

   public ConfigBattleTowers() {
   }
}
