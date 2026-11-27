package ichttt.mods.firstaid;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.CapProvider;
import ichttt.mods.firstaid.common.DebugDamageCommand;
import ichttt.mods.firstaid.common.IProxy;
import ichttt.mods.firstaid.common.apiimpl.HealingItemApiHelperImpl;
import ichttt.mods.firstaid.common.apiimpl.RegistryManager;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import ichttt.mods.firstaid.common.config.ExtraConfigManager;
import ichttt.mods.firstaid.common.items.FirstAidItems;
import ichttt.mods.firstaid.common.network.MessageAddHealth;
import ichttt.mods.firstaid.common.network.MessageApplyAbsorption;
import ichttt.mods.firstaid.common.network.MessageApplyHealingItem;
import ichttt.mods.firstaid.common.network.MessageClientRequest;
import ichttt.mods.firstaid.common.network.MessageConfiguration;
import ichttt.mods.firstaid.common.network.MessagePlayHurtSound;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import ichttt.mods.firstaid.common.network.MessageUpdatePart;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "firstaid",
   name = "First Aid",
   version = "1.6.22",
   acceptedMinecraftVersions = "[1.12.2,1.13)",
   dependencies = "required-after:forge@[14.23.5.2803,);",
   guiFactory = "ichttt.mods.firstaid.client.config.GuiFactory",
   certificateFingerprint = "7904c4e13947c8a616c5f39b26bdeba796500722"
)
public class FirstAid {
   public static final String MODID = "firstaid";
   public static final String NAME = "First Aid";
   public static final String VERSION = "1.6.22";
   public static final String FINGERPRINT = "7904c4e13947c8a616c5f39b26bdeba796500722";
   public static final Logger LOGGER = LogManager.getLogger("firstaid");
   public static boolean isSynced = false;
   public static List<ConfigEntry<ExtraConfig.Sync>> syncedConfigOptions;
   public static IProxy proxy;
   public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("firstaid") {
      public ItemStack getTabIconItem() {
         return new ItemStack(FirstAidItems.BANDAGE);
      }
   };
   public static SimpleNetworkWrapper NETWORKING;
   public static boolean morpheusLoaded = false;

   public FirstAid() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new ichttt.mods.firstaid.client.ClientProxy() : new ichttt.mods.firstaid.server.ServerProxy();
         ichttt.mods.firstaid.FirstAidConfig.register();
         ichttt.mods.firstaid.common.EventHandler.registerHolders();
         ichttt.mods.firstaid.common.items.FirstAidItems.registerHolders();
         ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem.registerCapabilities();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.loadComplete((FMLLoadCompleteEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.beforeServerStart((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerStoppedEvent) {
         this.onServerStop((FMLServerStoppedEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      LOGGER.info("{} version {} starting", "First Aid", "1.6.22");
      if (FirstAidConfig.debug) {
         LOGGER.warn("DEBUG MODE ENABLED");
         LOGGER.warn("FirstAid may be slower than usual and will produce much noisier logs if debug mode is enabled");
         LOGGER.warn("Disable debug in firstaid config");
      }

      ichttt.mods.firstaid.common.EventHandler.register(MinecraftForge.EVENT_BUS);
      proxy.preInit();
      HealingItemApiHelperImpl.init();
      RegistryManager.setupRegistries();
      checkEarlyExit();
   }

   public void init(FMLInitializationEvent event) {
      CapabilityManager.INSTANCE.register(AbstractPlayerDamageModel.class, new IStorage<AbstractPlayerDamageModel>() {
         @Nullable
         public NBTBase writeNBT(Capability<AbstractPlayerDamageModel> capability, AbstractPlayerDamageModel instance, EnumFacing side) {
            return instance.serializeNBT();
         }

         public void readNBT(Capability<AbstractPlayerDamageModel> capability, AbstractPlayerDamageModel instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound)nbt);
         }
      }, () -> {
         throw new UnsupportedOperationException("No default implementation");
      });
      syncedConfigOptions = ExtraConfigManager.getSyncedEntries();
      ExtraConfigManager.scheduleDelete("Overlay.hudScale");
      ExtraConfigManager.scheduleDelete("Overlay.displayHealthAsNumber");
      ExtraConfigManager.scheduleDelete("Overlay.heartThreshold");
      ExtraConfigManager.scheduleDelete("Overlay.showOverlay");
      ExtraConfigManager.scheduleDelete("Overlay.onlyShowWhenDamaged");
      ExtraConfigManager.scheduleDelete("Overlay.position");
      ExtraConfigManager.scheduleDelete("Overlay.mode");
      ExtraConfigManager.scheduleDelete("ExternalHealing.sleepHealing");
      ExtraConfigManager.scheduleDelete("experimentalSetHealth");
      ExtraConfigManager.scheduleDelete("Overlay.hideOnNoChange");
      ExtraConfigManager.scheduleDelete("Overlay.showVanillaHealthBar");
      ExtraConfigManager.scheduleDelete("armorEnchantmentMode");
      ExtraConfigManager.postProcessConfigs();
      int i = 0;
      NETWORKING = NetworkRegistry.INSTANCE.newSimpleChannel("firstaid");
      ++i;
      NETWORKING.registerMessage(new MessageUpdatePart.Handler(), MessageUpdatePart.class, MessageUpdatePart::new, i, Side.CLIENT);
      ++i;
      NETWORKING.registerMessage(new MessageApplyHealingItem.Handler(), MessageApplyHealingItem.class, MessageApplyHealingItem::new, i, Side.SERVER);
      ++i;
      NETWORKING.registerMessage(new MessageConfiguration.Handler(), MessageConfiguration.class, MessageConfiguration::new, i, Side.CLIENT);
      ++i;
      NETWORKING.registerMessage(new MessageApplyAbsorption.Handler(), MessageApplyAbsorption.class, MessageApplyAbsorption::new, i, Side.CLIENT);
      ++i;
      NETWORKING.registerMessage(new MessageAddHealth.Handler(), MessageAddHealth.class, MessageAddHealth::new, i, Side.CLIENT);
      ++i;
      NETWORKING.registerMessage(new MessagePlayHurtSound.Handler(), MessagePlayHurtSound.class, MessagePlayHurtSound::new, i, Side.CLIENT);
      ++i;
      NETWORKING.registerMessage(new MessageClientRequest.Handler(), MessageClientRequest.class, MessageClientRequest::new, i, Side.SERVER);
      ++i;
      NETWORKING.registerMessage(new MessageSyncDamageModel.Handler(), MessageSyncDamageModel.class, MessageSyncDamageModel::new, i, Side.CLIENT);
      proxy.init();
      RegistryManager.registerDefaults();
      checkEarlyExit();
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
      RegistryManager.finalizeRegistries();
      checkEarlyExit();
   }


   private static void checkEarlyExit() {
      if (FMLCommonHandler.instance().isDisplayCloseRequested()) {
         LOGGER.info("Early exit requested by user - terminating minecraft");
         FMLCommonHandler.instance().exitJava(0, false);
      }

   }

   public void beforeServerStart(FMLServerStartingEvent event) {
      event.registerServerCommand(new DebugDamageCommand());
   }

   public void onServerStop(FMLServerStoppedEvent event) {
      LOGGER.debug("Cleaning up");
      CapProvider.tutorialDone.clear();
      ichttt.mods.firstaid.common.EventHandler.HIT_LIST.clear();
   }

   static {
      VFile2 configDir = Loader.instance().getConfigDir();
      VFile2 oldConfigPath = new VFile2(configDir, "First Aid.cfg");
      VFile2 newConfigPath = new VFile2(configDir, "firstaid.cfg");
      if (oldConfigPath.exists() && !newConfigPath.exists()) {
         if (oldConfigPath.renameTo(newConfigPath)) {
            LOGGER.info("Moved config file to new home");
         } else {
            LOGGER.error("Failed to move config file to new correct location!");
         }
      }

   }
}
