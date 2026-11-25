package com.charles445.aireducer.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;

public class IceAndFireOldConfig {
   @Comment({"Master switch for this handler"})
   @Name("ENABLED")
   public boolean ENABLED = true;
   @Comment({"Should death worm AI be tweaked"})
   @Name("Death Worm")
   public boolean deathworm = true;
   @Comment({"How close in blocks a player has to be for death worms to have AI, helps fix chunk loading issues"})
   @Name("DeathWormProximityRequirement")
   @RangeDouble(
      min = 10.0D
   )
   public double deathwormProximityRequirement = 1000.0D;
   @Comment({"Whether to enable Death Worm Proximity Requirements"})
   @Name("DeathWormProximityRequired")
   public boolean deathwormProximityRequired = true;
   @Comment({"Should myrmex AI be tweaked"})
   @Name("Myrmex")
   public boolean myrmex = true;
   @Comment({"Range with which Myrmex can see and pathfind - Vanilla 1.7.1 is 128, 1.8.4 is 64"})
   @Name("MyrmexFollowRange")
   @RangeDouble(
      min = 16.0D,
      max = 128.0D
   )
   public double myrmexFollowRange = 50.0D;
   @Comment({"Update chance for EscortEntity - Vanilla is 1.0"})
   @Name("MyrmexUpdateChance EscortEntity")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceEscortEntity = 0.1D;
   @Comment({"Update chance for FindHidingSpot - Vanilla is 1.0"})
   @Name("MyrmexUpdateChance FindHidingSpot")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceFindHidingSpot = 0.1D;
   @Comment({"Update chance for Forage - Vanilla is 1.0"})
   @Name("MyrmexUpdateChance Forage")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceForage = 0.1D;
   @Comment({"Use alternate path navigation - for 1.7.1"})
   @Name("MyrmexAlternateNavigation")
   public boolean myrmexAlternateNavigation = true;
   @Comment({"Modify LeaveHive - for 1.7.1"})
   @Name("MyrmexModify LeaveHive")
   public boolean myrmexModifyLeaveHive = true;
   @Comment({"Modify MoveThroughHive - for 1.7.1"})
   @Name("MyrmexModify MoveThroughHive")
   public boolean myrmexMoveThroughHive = true;
   @Comment({"Update chance for ReEnterHive - Vanilla is 1.0"})
   @Name("MyrmexUpdateChance ReEnterHive")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceReEnterHive = 0.1D;
   @Comment({"Replaces Ice and Fire dragon avoiding AI to use reduced AI settings instead"})
   @Name("ReplaceVillageAIFearUntamed")
   public boolean replaceVillageAIFearUntamed = true;
   @Comment({"How close in blocks a player has to be for myrmex to have AI, helps fix chunk loading issues"})
   @Name("MyrmexProximityRequirement")
   @RangeDouble(
      min = 10.0D
   )
   public double myrmexProximityRequirement = 1000.0D;
   @Comment({"Whether to enable Myrmex Proximity Requirements"})
   @Name("MyrmexProximityRequired")
   public boolean myrmexProximityRequired = true;

   public IceAndFireOldConfig() {
   }
}
