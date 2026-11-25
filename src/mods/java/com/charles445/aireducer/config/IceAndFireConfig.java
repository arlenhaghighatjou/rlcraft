package com.charles445.aireducer.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

public class IceAndFireConfig {
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
   @Comment({"Range with which Myrmex can see and pathfind - Vanilla is 64"})
   @Name("MyrmexFollowRange")
   @RangeDouble(
      min = 16.0D,
      max = 128.0D
   )
   public double myrmexFollowRange = 64.0D;
   @Comment({"Modify EscortEntity"})
   @Name("MyrmexModify EscortEntity")
   public boolean myrmexModifyEscortEntity = true;
   @Comment({"Delay in ticks between new EscortEntity tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay EscortEntity")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayEscortEntity = 20;
   @Comment({"Update chance for EscortEntity task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance EscortEntity")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceEscortEntity = 1.0D;
   @Comment({"Modify FindHidingSpot"})
   @Name("MyrmexModify FindHidingSpot")
   public boolean myrmexModifyFindHidingSpot = true;
   @Comment({"Delay in ticks between new FindHidingSpot tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay FindHidingSpot")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayFindHidingSpot = 60;
   @Comment({"Update chance for FindHidingSpot task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance FindHidingSpot")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceFindHidingSpot = 0.1D;
   @Comment({"Modify Forage"})
   @Name("MyrmexModify Forage")
   public boolean myrmexModifyForage = true;
   @Comment({"Delay in ticks between new Forage tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay Forage")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayForage = 60;
   @Comment({"Update chance for Forage task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance Forage")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceForage = 0.5D;
   @Comment({"Modify LeaveHive"})
   @Name("MyrmexModify LeaveHive")
   public boolean myrmexModifyLeaveHive = true;
   @Comment({"Delay in ticks between new LeaveHive tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay LeaveHive")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayLeaveHive = 60;
   @Comment({"Update chance for LeaveHive task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance LeaveHive")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceLeaveHive = 0.1D;
   @Comment({"Modify MoveThroughHive"})
   @Name("MyrmexModify MoveThroughHive")
   public boolean myrmexModifyMoveThroughHive = true;
   @Comment({"Delay in ticks between new MoveThroughHive tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay MoveThroughHive")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayWanderHiveCenter = 60;
   @Comment({"Update chance for MoveThroughHive task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance MoveThroughHive")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceMoveThroughHive = 0.5D;
   @Comment({"Modify WanderHiveCenter"})
   @Name("MyrmexModify WanderHiveCenter")
   public boolean myrmexModifyWanderHiveCenter = true;
   @Comment({"Delay in ticks between new WanderHiveCenter tasks - Vanilla is 0"})
   @Name("MyrmexRunDelay WanderHiveCenter")
   @RangeInt(
      min = 0
   )
   public int myrmexRunDelayMoveThroughHive = 60;
   @Comment({"Update chance for WanderHiveCenter task updates - Vanilla is 1"})
   @Name("myrmexUpdateChance WanderHiveCenter")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   public double myrmexUpdateChanceWanderHiveCenter = 0.7D;
   @Comment({"How close in blocks a player has to be for myrmex to have AI, helps fix chunk loading issues"})
   @Name("MyrmexProximityRequirement")
   @RangeDouble(
      min = 10.0D
   )
   public double myrmexProximityRequirement = 1000.0D;
   @Comment({"Whether to enable Myrmex Proximity Requirements"})
   @Name("MyrmexProximityRequired")
   public boolean myrmexProximityRequired = true;

   public IceAndFireConfig() {
   }
}
