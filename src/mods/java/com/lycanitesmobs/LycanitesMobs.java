package com.lycanitesmobs;

import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import com.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import com.lycanitesmobs.core.command.CommandMain;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ElementManager;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.CreativeTabBlocks;
import com.lycanitesmobs.core.item.CreativeTabCharges;
import com.lycanitesmobs.core.item.CreativeTabCreatures;
import com.lycanitesmobs.core.item.CreativeTabEquipmentParts;
import com.lycanitesmobs.core.item.CreativeTabItems;
import com.lycanitesmobs.core.item.consumable.ItemHalloweenTreat;
import com.lycanitesmobs.core.item.consumable.ItemWinterGift;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.pets.PlayerFamiliars;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import com.lycanitesmobs.core.tileentity.EquipmentStationTileEntity;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import com.lycanitesmobs.core.worldgen.WorldGeneratorDungeon;
import com.lycanitesmobs.core.worldgen.WorldGeneratorFluids;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
   modid = "lycanitesmobs",
   name = "Lycanites Mobs",
   version = "2.0.8.9 - MC 1.12.2",
   useMetadata = false,
   acceptedMinecraftVersions = "[1.12,1.13)",
   dependencies = "after:thaumcraft"
)
public class LycanitesMobs {
   public static final String modid = "lycanitesmobs";
   public static final String name = "Lycanites Mobs";
   public static final String versionNumber = "2.0.8.9";
   public static final String versionMC = "1.12.2";
   public static final String version = "2.0.8.9 - MC 1.12.2";
   public static final String website = "https://lycanitesmobs.com";
   public static final String serviceAPI = "https://service.lycanitesmobs.com/api/v1";
   public static final String twitter = "https://twitter.com/Lycanite05";
   public static final String patreon = "https://www.patreon.com/lycanite";
   public static final String guilded = "https://www.guilded.gg/i/jpLvd6J2";
   public static final String discord = "https://discord.gg/bFpV3z4";
   public static final String acceptedMinecraftVersions = "[1.12,1.13)";
   public static final PacketHandler packetHandler = new PacketHandler();
   public static ModInfo modInfo;
   public static ConfigBase config;
   public static LycanitesMobs instance;
   public static CommonProxy proxy;
   public static Capability<ExtendedEntity> EXTENDED_ENTITY = null;
   public static Capability<ExtendedPlayer> EXTENDED_PLAYER = null;
   public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), "lycanitesmobs.items");
   public static final CreativeTabs blocksTab = new CreativeTabBlocks(CreativeTabs.getNextID(), "lycanitesmobs.blocks");
   public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), "lycanitesmobs.creatures");
   public static final CreativeTabs chargesTab = new CreativeTabCharges(CreativeTabs.getNextID(), "lycanitesmobs.charges");
   public static final CreativeTabs equipmentPartsTab = new CreativeTabEquipmentParts(CreativeTabs.getNextID(), "lycanitesmobs.equipmentparts");
   public static String texturePath = "mods/lycanitesmobs/";
   public PotionEffects potionEffects;
   public static WorldGeneratorDungeon dungeonGenerator;

   public LycanitesMobs() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.lycanitesmobs.client.ClientProxy() : new com.lycanitesmobs.CommonProxy();
         com.lycanitesmobs.LycanitesMobs.registerCapabilities();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverLoad((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      MapStorage.storageProviders.put(ExtendedWorld.class, ExtendedWorld::new);
      modInfo = new ModInfo(this, "Lycanites Mobs", 1000);
      ObjectManager.setCurrentModInfo(modInfo);
      proxy.initLanguageManager();
      ConfigBase.versionCheck("2.0.0.0", "2.0.8.9 - MC 1.12.2");
      config = ConfigBase.getConfig(modInfo, "general");
      config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
      config.setCategoryComment("Extras", "Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");
      config.getBool("Extras", "Disable Sneak Dismount", true, "Set to true to prevent players from dismounting from Lycanites Mobs mounts when pressing shift (useful for shift to fly/swim down).");
      config.setCategoryComment("Player", "Settings for player related stats and features.");
      (new GameEventListener()).register(MinecraftForge.EVENT_BUS);
      CreatureManager.getInstance().register(MinecraftForge.EVENT_BUS);
      ProjectileManager.getInstance().register(MinecraftForge.EVENT_BUS);
      PlayerFamiliars.INSTANCE.familiarBlacklist = new ArrayList();
      String[] familiarBlacklist = config.getStringList("Extras", "Familiar Username Blacklist", new String[0], "Donation Familiars help support the development of this mod but can be turned of for individual players be adding their username to this list.");
      PlayerFamiliars.INSTANCE.familiarBlacklist.addAll(Arrays.asList(familiarBlacklist));
      VersionChecker.INSTANCE.enabled = config.getBool("Extras", "Version Checker", VersionChecker.INSTANCE.enabled, "Set to false to disable the version checker.");
      packetHandler.init();
      NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
      ((RangedAttribute)SharedMonsterAttributes.MAX_HEALTH).maximumValue = 100000.0D;
      config.setCategoryComment("Admin", "Special tools for server admins.");
      ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = config.getStringList("Admin", "Force Remove Entity Names", new String[0], "Here you can add a list of entity IDs for entity that you want to be forcefully removed.");
      if (ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
         logInfo("", "Lycanites Mobs will forcefully remove the following entities based on their registered IDs:");

         for(String removeEntityID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS) {
            logInfo("", removeEntityID);
         }
      }

      ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = config.getInt("Admin", "Force Remove Entity Ticks", 40, "How many ticks it takes for an entity to be forcefully removed (1 second = 20 ticks). This only applies to EntityLiving, other entities are instantly removed.");
      ElementManager.getInstance().loadConfig();
      ElementManager.getInstance().loadAllFromJSON(modInfo);
      this.potionEffects = new PotionEffects();
      this.potionEffects.init(config);
      ObjectLists.createVanillaLists();
      ObjectLists.createCustomItems();
      ItemManager.getInstance().startup(modInfo);
      EquipmentPartManager.getInstance().loadAllFromJSON(modInfo);
      ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class, TileEntitySummoningPedestal::new);
      ObjectManager.addTileEntity("equipmentforge", TileEntityEquipmentForge.class, TileEntityEquipmentForge::new);
      ObjectManager.addTileEntity("equipment_infuser", EquipmentInfuserTileEntity.class, EquipmentInfuserTileEntity::new);
      ObjectManager.addTileEntity("equipment_station", EquipmentStationTileEntity.class, EquipmentStationTileEntity::new);
      ObjectManager.addSpecialEntity("hitarea", EntityHitArea.class);
      CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer::new);
      CapabilityManager.INSTANCE.register(ExtendedEntity.class, new ExtendedEntityStorage(), ExtendedEntity::new);
      CreatureManager.getInstance().startup(modInfo);
      ProjectileManager.getInstance().startup(modInfo);
      SpawnerEventListener.getInstance().register(FMLCommonHandler.instance().bus());
      SpawnerManager.getInstance().loadAllFromJSON();
      AltarInfo.loadGlobalSettings();
      AltarInfo.createAltars();
      MobEventManager.getInstance().register(FMLCommonHandler.instance().bus());
      MobEventListener.getInstance().register(FMLCommonHandler.instance().bus());
      MobEventManager.getInstance().loadConfig();
      MobEventManager.getInstance().loadAllFromJSON(modInfo);
      GameRegistry.registerWorldGenerator(new WorldGeneratorFluids(), 0);
      DungeonManager.getInstance().loadAllFromJSON();
      dungeonGenerator = new WorldGeneratorDungeon();
      GameRegistry.registerWorldGenerator(dungeonGenerator, 1000);
      ItemHalloweenTreat.createObjectLists();
      ItemWinterGift.createObjectLists();
      proxy.registerEvents();
      proxy.registerRenders(modInfo);
      proxy.registerTextures();
   }

   public void init(FMLInitializationEvent event) {
      proxy.registerModels(modInfo);
   }

   public void postInit(FMLPostInitializationEvent event) {
      CreatureManager.getInstance().lateStartup(modInfo);
   }

   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandMain());
   }

   public static void logInfo(String key, String message) {
      if ("".equals(key) || config.getBool("Debug", key, false)) {
         System.out.println("[LycanitesMobs] [Info] [" + key + "] " + message);
      }

   }

   public static void logDebug(String key, String message) {
      if ("".equals(key) || config.getBool("Debug", key, false)) {
         System.out.println("[LycanitesMobs] [Debug] [" + key + "] " + message);
      }

   }

   public static void logWarning(String key, String message) {
      if ("".equals(key) || config.getBool("Debug", key, false)) {
         System.err.println("[LycanitesMobs] [WARNING] [" + key + "] " + message);
      }

   }

   static {
      FluidRegistry.enableUniversalBucket();
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(ExtendedEntity.class, capability -> EXTENDED_ENTITY = capability);
      CapabilityManager.INSTANCE.onRegistered(ExtendedPlayer.class, capability -> EXTENDED_PLAYER = capability);
   }
}
