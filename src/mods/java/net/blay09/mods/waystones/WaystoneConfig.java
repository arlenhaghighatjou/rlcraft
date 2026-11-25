package net.blay09.mods.waystones;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;

@Config(
   modid = "waystones",
   type = Type.INSTANCE,
   name = "Waystones",
   category = ""
)
@LangKey("waystones.config")
public class WaystoneConfig {
   public static WaystoneConfig.General general = new WaystoneConfig.General();
   public static WaystoneConfig.WorldGen worldGen = new WaystoneConfig.WorldGen();
   public static WaystoneConfig.Compat compat = new WaystoneConfig.Compat();
   public static WaystoneConfig.Client client = new WaystoneConfig.Client();

   public WaystoneConfig() {
   }

   public static void read(ByteBuf buf) {
      general.teleportButton = buf.readBoolean();
      general.teleportButtonCooldown = buf.readInt();
      general.teleportButtonReturnOnly = buf.readBoolean();
      general.warpStoneCooldown = buf.readInt();
      general.interDimension = buf.readBoolean();
      general.creativeModeOnly = buf.readBoolean();
      general.setSpawnPoint = buf.readBoolean();
      general.restrictRenameToOwner = buf.readBoolean();
      general.blocksPerXPLevel = buf.readInt();
      general.maximumXpCost = buf.readInt();
      general.allowEveryoneGlobal = buf.readBoolean();
      general.warpStoneUseTime = buf.readByte();
      general.warpScrollUseTime = buf.readByte();
      general.globalWaystonesCostXp = buf.readBoolean();
      general.warpStoneXpCost = buf.readBoolean();
      general.shortTravelFree = buf.readBoolean();
   }

   public static void write(ByteBuf buf) {
      buf.writeBoolean(general.teleportButton);
      buf.writeInt(general.teleportButtonCooldown);
      buf.writeBoolean(general.teleportButtonReturnOnly);
      buf.writeInt(general.warpStoneCooldown);
      buf.writeBoolean(general.interDimension);
      buf.writeBoolean(general.creativeModeOnly);
      buf.writeBoolean(general.setSpawnPoint);
      buf.writeBoolean(general.restrictRenameToOwner);
      buf.writeInt(general.blocksPerXPLevel);
      buf.writeInt(general.maximumXpCost);
      buf.writeBoolean(general.allowEveryoneGlobal);
      buf.writeByte(general.warpStoneUseTime);
      buf.writeByte(general.warpScrollUseTime);
      buf.writeBoolean(general.globalWaystonesCostXp);
      buf.writeBoolean(general.warpStoneXpCost);
      buf.writeBoolean(general.shortTravelFree);
   }

   public static class Client {
      @Name("Sound Volume")
      @Comment({"The volume of the sound played when teleporting."})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public float soundVolume = 0.5F;
      @Name("Teleport Button GUI X")
      @Comment({"The x position of the warp button in the inventory."})
      public int teleportButtonX = 58;
      @Name("Teleport Button GUI Y")
      @Comment({"The y position of the warp button in the inventory."})
      public int teleportButtonY = 60;
      @Name("Disable Particles")
      @Comment({"If enabled, activated waystones will not emit particles."})
      public boolean disableParticles = false;
      @Name("Disable Text Glow")
      @Comment({"If enabled, the text overlay on waystones will no longer always render at full brightness."})
      public boolean disableTextGlow = false;
      @Name("Mossy Look for World Gen Waystones")
      @Comment({"If enabled, waystones spawned using the legacy spawn-just-anywhere mode will look mossy ingame."})
      public boolean randomlySpawnedLookMossy = true;

      public Client() {
      }
   }

   public static class Compat {
      @Name("Create JourneyMap Waypoint")
      @Comment({"If this is true, activating a waystone will cause a JourneyMap waypoint to be created at its position."})
      public boolean createJourneyMapWaypoint = false;

      public Compat() {
      }
   }

   public static class General {
      @Name("Teleport Button in GUI")
      @Comment({"Should there be a button in the inventory to access the waystone menu?"})
      public boolean teleportButton = false;
      @Name("Teleport Button Target")
      @Comment({"Set this to a global waystone name to lock the inventory button to that specific waystone. Leave empty for default behaviour."})
      public String teleportButtonTarget = "";
      @Name("Teleport Button Cooldown")
      @Comment({"The cooldown between usages of the teleport button in seconds."})
      public int teleportButtonCooldown = 300;
      @Name("Teleport Button Return Only")
      @Comment({"If enabled, the teleport button will only let you return to the last activated waystone, instead of allowing to choose."})
      public boolean teleportButtonReturnOnly = true;
      @Name("Blocks per XP Level")
      @Comment({"The amount of blocks per xp level requirement."})
      public int blocksPerXPLevel = 500;
      @Name("Dimensional Warp XP Cost")
      @Comment({"The amount of xp levels to use for dimensional warp."})
      public int dimensionalXpCost = 3;
      @Name("Short Travel is Free")
      @Comment({"If set to true, XP cost by distance travelled will only start beyond the distance-per-level distance, and shorter travel will be free."})
      public boolean shortTravelFree = true;
      @Name("Maximum XP Cost")
      @Comment({"The maximum xp cost when Blocks per XP Level is enabled."})
      @RangeInt(
         min = 1
      )
      public int maximumXpCost = 3;
      @Name("Waystone Costs XP")
      @Comment({"If enabled, waystones cost experience when used, based on the distance travelled."})
      public boolean waystoneXpCost = true;
      @Name("Inventory Button Costs XP")
      @Comment({"If enabled, the inventory button cost experience when used, based on the distance travelled."})
      public boolean inventoryButtonXpCost = false;
      @Name("Warp Stone Costs XP")
      @Comment({"If enabled, the warp stone costs experience when used, based on the distance travelled."})
      public boolean warpStoneXpCost = false;
      @Name("Global Waystones Cost XP")
      @Comment({"Set to false to make all global waystones not cost any experience, regardless of method used."})
      public boolean globalWaystonesCostXp = true;
      @Name("Warp Stone Cooldown")
      @Comment({"The cooldown between usages of the warp stone in seconds."})
      public int warpStoneCooldown = 300;
      @Name("Interdimensional Teleport")
      @Comment({"If enabled, all waystones work inter-dimensionally."})
      public boolean interDimension = true;
      @Name("Restrict Rename to Owner")
      @Comment({"If enabled, only the owner of a waystone can rename it."})
      public boolean restrictRenameToOwner = false;
      @Name("Creative Mode Only")
      @Comment({"If enabled, waystones can only be placed in creative mode."})
      public boolean creativeModeOnly;
      @Name("Set Spawnpoint on Activation")
      @Comment({"If enabled, the player's spawnpoint will be set to the last activated waystone."})
      public boolean setSpawnPoint = false;
      @Name("No Cooldown on Global Waystones")
      @Comment({"If enabled, waystones marked as global have no cooldown."})
      public boolean globalNoCooldown = true;
      @Name("Interdimensional Teleport on Global Waystones")
      @Comment({"If enabled, waystones marked as global work inter-dimensionally."})
      public boolean globalInterDimension = true;
      @Name("Allow Global Waystones for Everyone")
      @Comment({"If enabled, everyone can create global waystones, not just players in creative mode."})
      public boolean allowEveryoneGlobal = false;
      @Name("Reset Use On Damage")
      @Comment({"Whether the use of a warp stone or warp scroll should be interrupted by damage. Not supported when playing with Vivecraft!"})
      public boolean resetUseOnDamage = false;
      @Name("Disallow Breaking Generated")
      @Comment({"Whether generated waystones should not be breakable by players."})
      public boolean disallowBreakingGenerated = false;
      @Name("Drop Item From Generated")
      @Comment({"Whether generated waystones drop their item form when broken by players."})
      public boolean dropGenerated = true;
      @Name("Warp Stone Use Time")
      @Comment({"The time it takes to use a warp stone in ticks. This is the charge-up time when holding right-click."})
      @RangeInt(
         min = 1,
         max = 127
      )
      public int warpStoneUseTime = 32;
      @Name("Warp Scroll Use Time")
      @Comment({"The time it takes to use a warp scroll in ticks. This is the charge-up time when holding right-click."})
      @RangeInt(
         min = 1,
         max = 127
      )
      public int warpScrollUseTime = 32;

      public General() {
      }
   }

   public static class WorldGen {
      @Name("Generate in Villages")
      @Comment({"The chance for a waystone to generate in a village. Set to 1 to always generate one in villages, set to 0 to disable."})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public float villageChance = 1.0F;
      @Name("Change to generate just anywhere")
      @Comment({"The chance for a waystone to generate just anywhere (without a structure), scaled by 1/10000."})
      @RangeDouble(
         min = 0.0D,
         max = 10000.0D
      )
      public float legacyChance = 0.0F;
      @Name("Custom Names")
      @Comment({"The Name Generator will pick from these names until they have all been used, then it will generate random ones again."})
      public String[] customNames = new String[0];

      public WorldGen() {
      }
   }
}
