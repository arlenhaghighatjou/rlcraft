package net.blay09.mods.waystones;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
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

   public static void register() {
      ConfigManager.register("waystones", "Waystones", "", "waystones.config",
         ConfigEntry.category("general",
            ConfigEntry.value("Teleport Button in GUI", boolean.class, () -> WaystoneConfig.general.teleportButton, v -> WaystoneConfig.general.teleportButton = (Boolean)v).comment("Should there be a button in the inventory to access the waystone menu?").langKey("waystones.general.teleportbutton"),
            ConfigEntry.value("Teleport Button Target", String.class, () -> WaystoneConfig.general.teleportButtonTarget, v -> WaystoneConfig.general.teleportButtonTarget = (String)v).comment("Set this to a global waystone name to lock the inventory button to that specific waystone. Leave empty for default behaviour.").langKey("waystones.general.teleportbuttontarget"),
            ConfigEntry.value("Teleport Button Cooldown", int.class, () -> WaystoneConfig.general.teleportButtonCooldown, v -> WaystoneConfig.general.teleportButtonCooldown = (Integer)v).comment("The cooldown between usages of the teleport button in seconds.").langKey("waystones.general.teleportbuttoncooldown"),
            ConfigEntry.value("Teleport Button Return Only", boolean.class, () -> WaystoneConfig.general.teleportButtonReturnOnly, v -> WaystoneConfig.general.teleportButtonReturnOnly = (Boolean)v).comment("If enabled, the teleport button will only let you return to the last activated waystone, instead of allowing to choose.").langKey("waystones.general.teleportbuttonreturnonly"),
            ConfigEntry.value("Blocks per XP Level", int.class, () -> WaystoneConfig.general.blocksPerXPLevel, v -> WaystoneConfig.general.blocksPerXPLevel = (Integer)v).comment("The amount of blocks per xp level requirement.").langKey("waystones.general.blocksperxplevel"),
            ConfigEntry.value("Dimensional Warp XP Cost", int.class, () -> WaystoneConfig.general.dimensionalXpCost, v -> WaystoneConfig.general.dimensionalXpCost = (Integer)v).comment("The amount of xp levels to use for dimensional warp.").langKey("waystones.general.dimensionalxpcost"),
            ConfigEntry.value("Short Travel is Free", boolean.class, () -> WaystoneConfig.general.shortTravelFree, v -> WaystoneConfig.general.shortTravelFree = (Boolean)v).comment("If set to true, XP cost by distance travelled will only start beyond the distance-per-level distance, and shorter travel will be free.").langKey("waystones.general.shorttravelfree"),
            ConfigEntry.value("Maximum XP Cost", int.class, () -> WaystoneConfig.general.maximumXpCost, v -> WaystoneConfig.general.maximumXpCost = (Integer)v).comment("The maximum xp cost when Blocks per XP Level is enabled.").langKey("waystones.general.maximumxpcost").range(1, Integer.MAX_VALUE),
            ConfigEntry.value("Waystone Costs XP", boolean.class, () -> WaystoneConfig.general.waystoneXpCost, v -> WaystoneConfig.general.waystoneXpCost = (Boolean)v).comment("If enabled, waystones cost experience when used, based on the distance travelled.").langKey("waystones.general.waystonexpcost"),
            ConfigEntry.value("Inventory Button Costs XP", boolean.class, () -> WaystoneConfig.general.inventoryButtonXpCost, v -> WaystoneConfig.general.inventoryButtonXpCost = (Boolean)v).comment("If enabled, the inventory button cost experience when used, based on the distance travelled.").langKey("waystones.general.inventorybuttonxpcost"),
            ConfigEntry.value("Warp Stone Costs XP", boolean.class, () -> WaystoneConfig.general.warpStoneXpCost, v -> WaystoneConfig.general.warpStoneXpCost = (Boolean)v).comment("If enabled, the warp stone costs experience when used, based on the distance travelled.").langKey("waystones.general.warpstonexpcost"),
            ConfigEntry.value("Global Waystones Cost XP", boolean.class, () -> WaystoneConfig.general.globalWaystonesCostXp, v -> WaystoneConfig.general.globalWaystonesCostXp = (Boolean)v).comment("Set to false to make all global waystones not cost any experience, regardless of method used.").langKey("waystones.general.globalwaystonescostxp"),
            ConfigEntry.value("Warp Stone Cooldown", int.class, () -> WaystoneConfig.general.warpStoneCooldown, v -> WaystoneConfig.general.warpStoneCooldown = (Integer)v).comment("The cooldown between usages of the warp stone in seconds.").langKey("waystones.general.warpstonecooldown"),
            ConfigEntry.value("Interdimensional Teleport", boolean.class, () -> WaystoneConfig.general.interDimension, v -> WaystoneConfig.general.interDimension = (Boolean)v).comment("If enabled, all waystones work inter-dimensionally.").langKey("waystones.general.interdimension"),
            ConfigEntry.value("Restrict Rename to Owner", boolean.class, () -> WaystoneConfig.general.restrictRenameToOwner, v -> WaystoneConfig.general.restrictRenameToOwner = (Boolean)v).comment("If enabled, only the owner of a waystone can rename it.").langKey("waystones.general.restrictrenametoowner"),
            ConfigEntry.value("Creative Mode Only", boolean.class, () -> WaystoneConfig.general.creativeModeOnly, v -> WaystoneConfig.general.creativeModeOnly = (Boolean)v).comment("If enabled, waystones can only be placed in creative mode.").langKey("waystones.general.creativemodeonly"),
            ConfigEntry.value("Set Spawnpoint on Activation", boolean.class, () -> WaystoneConfig.general.setSpawnPoint, v -> WaystoneConfig.general.setSpawnPoint = (Boolean)v).comment("If enabled, the player's spawnpoint will be set to the last activated waystone.").langKey("waystones.general.setspawnpoint"),
            ConfigEntry.value("No Cooldown on Global Waystones", boolean.class, () -> WaystoneConfig.general.globalNoCooldown, v -> WaystoneConfig.general.globalNoCooldown = (Boolean)v).comment("If enabled, waystones marked as global have no cooldown.").langKey("waystones.general.globalnocooldown"),
            ConfigEntry.value("Interdimensional Teleport on Global Waystones", boolean.class, () -> WaystoneConfig.general.globalInterDimension, v -> WaystoneConfig.general.globalInterDimension = (Boolean)v).comment("If enabled, waystones marked as global work inter-dimensionally.").langKey("waystones.general.globalinterdimension"),
            ConfigEntry.value("Allow Global Waystones for Everyone", boolean.class, () -> WaystoneConfig.general.allowEveryoneGlobal, v -> WaystoneConfig.general.allowEveryoneGlobal = (Boolean)v).comment("If enabled, everyone can create global waystones, not just players in creative mode.").langKey("waystones.general.alloweveryoneglobal"),
            ConfigEntry.value("Reset Use On Damage", boolean.class, () -> WaystoneConfig.general.resetUseOnDamage, v -> WaystoneConfig.general.resetUseOnDamage = (Boolean)v).comment("Whether the use of a warp stone or warp scroll should be interrupted by damage. Not supported when playing with Vivecraft!").langKey("waystones.general.resetuseondamage"),
            ConfigEntry.value("Disallow Breaking Generated", boolean.class, () -> WaystoneConfig.general.disallowBreakingGenerated, v -> WaystoneConfig.general.disallowBreakingGenerated = (Boolean)v).comment("Whether generated waystones should not be breakable by players.").langKey("waystones.general.disallowbreakinggenerated"),
            ConfigEntry.value("Drop Item From Generated", boolean.class, () -> WaystoneConfig.general.dropGenerated, v -> WaystoneConfig.general.dropGenerated = (Boolean)v).comment("Whether generated waystones drop their item form when broken by players.").langKey("waystones.general.dropgenerated"),
            ConfigEntry.value("Warp Stone Use Time", int.class, () -> WaystoneConfig.general.warpStoneUseTime, v -> WaystoneConfig.general.warpStoneUseTime = (Integer)v).comment("The time it takes to use a warp stone in ticks. This is the charge-up time when holding right-click.").langKey("waystones.general.warpstoneusetime").range(1, 127),
            ConfigEntry.value("Warp Scroll Use Time", int.class, () -> WaystoneConfig.general.warpScrollUseTime, v -> WaystoneConfig.general.warpScrollUseTime = (Integer)v).comment("The time it takes to use a warp scroll in ticks. This is the charge-up time when holding right-click.").langKey("waystones.general.warpscrollusetime").range(1, 127)),
         ConfigEntry.category("worldGen",
            ConfigEntry.value("Generate in Villages", float.class, () -> WaystoneConfig.worldGen.villageChance, v -> WaystoneConfig.worldGen.villageChance = (Float)v).comment("The chance for a waystone to generate in a village. Set to 1 to always generate one in villages, set to 0 to disable.").langKey("waystones.worldgen.villagechance").range(0.0, 1.0),
            ConfigEntry.value("Change to generate just anywhere", float.class, () -> WaystoneConfig.worldGen.legacyChance, v -> WaystoneConfig.worldGen.legacyChance = (Float)v).comment("The chance for a waystone to generate just anywhere (without a structure), scaled by 1/10000.").langKey("waystones.worldgen.legacychance").range(0.0, 10000.0),
            ConfigEntry.value("Custom Names", String[].class, () -> WaystoneConfig.worldGen.customNames, v -> WaystoneConfig.worldGen.customNames = (String[])v).comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.").langKey("waystones.worldgen.customnames")),
         ConfigEntry.category("client",
            ConfigEntry.value("Sound Volume", float.class, () -> WaystoneConfig.client.soundVolume, v -> WaystoneConfig.client.soundVolume = (Float)v).comment("The volume of the sound played when teleporting.").langKey("waystones.client.soundvolume").range(0.0, 1.0),
            ConfigEntry.value("Teleport Button GUI X", int.class, () -> WaystoneConfig.client.teleportButtonX, v -> WaystoneConfig.client.teleportButtonX = (Integer)v).comment("The x position of the warp button in the inventory.").langKey("waystones.client.teleportbuttonx"),
            ConfigEntry.value("Teleport Button GUI Y", int.class, () -> WaystoneConfig.client.teleportButtonY, v -> WaystoneConfig.client.teleportButtonY = (Integer)v).comment("The y position of the warp button in the inventory.").langKey("waystones.client.teleportbuttony"),
            ConfigEntry.value("Disable Particles", boolean.class, () -> WaystoneConfig.client.disableParticles, v -> WaystoneConfig.client.disableParticles = (Boolean)v).comment("If enabled, activated waystones will not emit particles.").langKey("waystones.client.disableparticles"),
            ConfigEntry.value("Disable Text Glow", boolean.class, () -> WaystoneConfig.client.disableTextGlow, v -> WaystoneConfig.client.disableTextGlow = (Boolean)v).comment("If enabled, the text overlay on waystones will no longer always render at full brightness.").langKey("waystones.client.disabletextglow"),
            ConfigEntry.value("Mossy Look for World Gen Waystones", boolean.class, () -> WaystoneConfig.client.randomlySpawnedLookMossy, v -> WaystoneConfig.client.randomlySpawnedLookMossy = (Boolean)v).comment("If enabled, waystones spawned using the legacy spawn-just-anywhere mode will look mossy ingame.").langKey("waystones.client.randomlyspawnedlookmossy")));
   }
}
