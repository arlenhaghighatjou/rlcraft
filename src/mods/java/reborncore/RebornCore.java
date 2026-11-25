package reborncore;

import java.io.File;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.api.ToolManager;
import reborncore.client.gui.ManualGuiHandler;
import reborncore.common.IModInfo;
import reborncore.common.LootManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.BlockWrenchEventHandler;
import reborncore.common.blocks.RebornMachineBlock;
import reborncore.common.commands.CommandListMods;
import reborncore.common.commands.CommandListRecipes;
import reborncore.common.logic.LogicControllerGuiHandler;
import reborncore.common.logic.PacketButtonID;
import reborncore.common.multiblock.MultiblockEventHandler;
import reborncore.common.multiblock.MultiblockServerTickHandler;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.RegisterPacketEvent;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.network.packet.PacketConfigSave;
import reborncore.common.network.packet.PacketFluidConfigSave;
import reborncore.common.network.packet.PacketFluidConfigSync;
import reborncore.common.network.packet.PacketFluidIOSave;
import reborncore.common.network.packet.PacketIOSave;
import reborncore.common.network.packet.PacketSendFluidStack;
import reborncore.common.network.packet.PacketSendLong;
import reborncore.common.network.packet.PacketSendObject;
import reborncore.common.network.packet.PacketSlotSave;
import reborncore.common.network.packet.PacketSlotSync;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryConstructionEvent;
import reborncore.common.registration.impl.ConfigRegistryFactory;
import reborncore.common.util.CalenderUtils;
import reborncore.common.util.CrashHandler;
import reborncore.common.util.GenericWrenchHelper;
import reborncore.common.util.LogHelper;
import reborncore.common.util.OreRegistationEvent;
import reborncore.common.util.OreUtil;
import reborncore.common.util.RebornPermissions;
import reborncore.modcl.manual.ItemTeamRebornManual;

@Mod(
   modid = "reborncore",
   name = "Reborn Core",
   version = "3.19.5",
   acceptedMinecraftVersions = "[1.12]",
   dependencies = "required-after:forge@[14.23.5.2796,);",
   certificateFingerprint = "8727a3141c8ec7f173b87aa78b9b9807867c4e6b"
)
public class RebornCore implements IModInfo {
   public static final String MOD_NAME = "Reborn Core";
   public static final String MOD_ID = "reborncore";
   public static final String MOD_VERSION = "3.19.5";
   public static final String WEB_URL = "https://files.modmuss50.me/";
   public static LogHelper logHelper;
   @Instance
   public static RebornCore INSTANCE;
   @SidedProxy(
      clientSide = "reborncore.ClientProxy",
      serverSide = "reborncore.CommonProxy"
   )
   public static CommonProxy proxy;
   public static File configDir;

   public RebornCore() {
      logHelper = new LogHelper(this);
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      INSTANCE = this;
      FMLCommonHandler.instance().registerCrashCallable(new CrashHandler());
      configDir = new File(event.getModConfigurationDirectory(), "teamreborn");
      if (!configDir.exists()) {
         configDir.mkdir();
      }

      MinecraftForge.EVENT_BUS.register(ConfigRegistryFactory.class);
      ConfigRegistryFactory.setConfigDir(configDir);
      RegistrationManager.init(event);
      RegistrationManager.load(new RegistryConstructionEvent());
      ConfigRegistryFactory.saveAll();
      MinecraftForge.EVENT_BUS.register(OreRegistationEvent.class);
      PowerSystem.selectedFile = new File(configDir, "reborncore/selected_energy.json");
      PowerSystem.readFile();
      CalenderUtils.loadCalender();
      proxy.preInit(event);
      NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new LogicControllerGuiHandler());
      MinecraftForge.EVENT_BUS.register(this);
      RegistrationManager.load(event);
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("ic2:wrench"), true));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("forestry:wrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("actuallyadditions:item_laser_wrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("thermalfoundation:wrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("charset:wrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("teslacorelib:wrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("rftools:smartwrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("intergrateddynamics:smartwrench"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("correlated:weldthrower"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("chiselsandbits:wrench_wood"), false));
      ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("redstonearsenal:tool.wrench_flux"), false));
      PowerSystem.EnergySystem.FE.enabled = () -> {
         return RebornCoreConfig.enableFE;
      };
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      OreUtil.scanForOres();
      NetworkManager.load();
      RebornPermissions.init();
      MinecraftForge.EVENT_BUS.register(LootManager.INSTANCE);
      MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
      MinecraftForge.EVENT_BUS.register(new MultiblockServerTickHandler());
      MinecraftForge.EVENT_BUS.register(BlockWrenchEventHandler.class);
      MinecraftForge.EVENT_BUS.register(RebornMachineBlock.class);
      if (ItemTeamRebornManual.isManualEnabled) {
         RebornRegistry.registerItem(new ItemTeamRebornManual());
         NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new ManualGuiHandler());
      }

      proxy.init(event);
      RegistrationManager.load(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
      RegistrationManager.load(event);

      try {
         OreUtil.remove("blockMetal");
      } catch (Exception var3) {
         logHelper.error("Failed to remove ore");
         logHelper.error(var3);
      }

   }

   @EventHandler
   public void loaded(FMLLoadCompleteEvent event) {
      OreRegistationEvent.loadComplete();
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      logHelper.error("Invalid fingerprint detected for RebornCore!");
      proxy.invalidFingerprints.add("Invalid fingerprint detected for RebornCore!");
   }

   @SubscribeEvent
   public void registerPackets(RegisterPacketEvent event) {
      event.registerPacket(PacketButtonID.class, Side.SERVER);
      event.registerPacket(CustomDescriptionPacket.class, Side.CLIENT);
      event.registerPacket(PacketSlotSave.class, Side.SERVER);
      event.registerPacket(PacketFluidConfigSave.class, Side.SERVER);
      event.registerPacket(PacketConfigSave.class, Side.SERVER);
      event.registerPacket(PacketSlotSync.class, Side.CLIENT);
      event.registerPacket(PacketFluidConfigSync.class, Side.CLIENT);
      event.registerPacket(PacketIOSave.class, Side.SERVER);
      event.registerPacket(PacketFluidIOSave.class, Side.SERVER);
      event.registerPacket(PacketSendLong.class, Side.CLIENT);
      event.registerPacket(PacketSendObject.class, Side.CLIENT);
      event.registerPacket(PacketSendFluidStack.class, Side.CLIENT);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandListRecipes());
      event.registerServerCommand(new CommandListMods());
   }

   public String MOD_NAME() {
      return "Reborn Core";
   }

   public String MOD_ID() {
      return "reborncore";
   }

   public String MOD_VERSION() {
      return "3.19.5";
   }

   public String MOD_DEPENDENCIES() {
      return "";
   }
}
