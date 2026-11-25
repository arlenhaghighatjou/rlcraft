package reborncore.common;

import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;

@RebornRegistry
public class RebornCoreConfig {
   @ConfigRegistry(
      config = "power",
      key = "EU - FU ratio",
      comment = "The Amount of FU to output from EU"
   )
   public static int euPerFU = 4;
   @ConfigRegistry(
      config = "power",
      key = "Enable FE support",
      comment = "Whether energy blocks will accept and emit Forge Energy (FE/RF/etc)"
   )
   public static boolean enableFE = true;
   @ConfigRegistry(
      config = "client",
      key = "Show Stack Info HUD",
      comment = "Show Stack Info HUD"
   )
   public static boolean ShowStackInfoHUD = true;
   @ConfigRegistry(
      config = "client",
      key = "Stack Info Corner",
      comment = "Screen corner for HUD, 0 is top left, 1 is top right, 2 is bottom right and 3 is bottom left"
   )
   public static int stackInfoCorner = 0;
   @ConfigRegistry(
      config = "client",
      key = "Stack Info X",
      comment = "X padding for HUD "
   )
   public static int stackInfoX = 2;
   @ConfigRegistry(
      config = "client",
      key = "Stack Info Y",
      comment = "Y padding for HUD "
   )
   public static int stackInfoY = 7;
   @ConfigRegistry(
      config = "misc",
      key = "Version Check",
      comment = "Enable version checker"
   )
   public static boolean versionCheck = true;
   @ConfigRegistry(
      config = "misc",
      key = "Enable Seasonal Easter Eggs",
      comment = "Disable this is you don't want seasonal easter eggs"
   )
   public static boolean easterEggs = true;
   @ConfigRegistry(
      config = "misc",
      category = "debug",
      key = "Enable Debug tools for ores",
      comment = "Contains some debug tools to help fix issues with ores"
   )
   public static boolean oreDebug = false;
   @ConfigRegistry(
      config = "misc",
      key = "Config Updater",
      comment = "AutoUpdates none user chnaged config values, when the mod default changes."
   )
   public static boolean configUpdating = true;
   @ConfigRegistry(
      config = "power",
      key = "Energy smoking",
      comment = "When enabled machines that try to insert power into a machine with a lower tier will smoke"
   )
   public static boolean smokeHighTeir = false;
   @ConfigRegistry(
      config = "misc",
      key = "Wrench Required",
      comment = "Wrench required to pick machine. If not wrenched than machine frame will drop instead."
   )
   public static boolean wrenchRequired = true;
   @ConfigRegistry(
      config = "upgrades",
      key = "Use Exponential Machine Speed Scaling",
      comment = "Whether to use an alternative, exponential scaling algorithm like IC2.\nWith the exponential scaling algorithm (true), processing time will be determined by (1 - OverclockerSpeed)^NumberOfOverclockers.\nWithout the algorithm (false), processing time is determined by (1 - OverclockerSpeed*NumberOfOverclockers), with a minimum of 1% processing time."
   )
   public static boolean exponentialMachineSpeedScaling = false;

   public RebornCoreConfig() {
   }
}
