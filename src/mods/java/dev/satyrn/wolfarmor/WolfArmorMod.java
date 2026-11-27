package dev.satyrn.wolfarmor;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import dev.satyrn.wolfarmor.api.common.IProxy;
import dev.satyrn.wolfarmor.api.util.ILogHelper;
import dev.satyrn.wolfarmor.common.network.WolfArmorChannel;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import dev.satyrn.wolfarmor.util.LogHelper;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   useMetadata = true,
   modid = "wolfarmor",
   name = "Wolf Armor and Storage",
   version = "3.8.1+alpha.1",
   guiFactory = "dev.satyrn.wolfarmor.client.gui.config.WolfArmorGuiFactory",
   certificateFingerprint = "e94e38a605842477f3ec218e6fcf781f6b3f7f89"
)
public class WolfArmorMod {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new dev.satyrn.wolfarmor.client.ClientProxy() : new dev.satyrn.wolfarmor.common.CommonProxy();
         dev.satyrn.wolfarmor.api.util.Entities.registerHolders();
         dev.satyrn.wolfarmor.api.util.Items.registerHolders();
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("wolfarmor", "enable_crafting"), new dev.satyrn.wolfarmor.factories.conditions.EnableCraftingCondition());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("wolfarmor", "enable_crafting_advancement"), new dev.satyrn.wolfarmor.factories.conditions.EnableCraftingCondition());
         dev.satyrn.wolfarmor.api.util.Recipes.registerHolders();
         dev.satyrn.wolfarmor.api.util.Capabilities.registerCapabilities();
         com.attributestudios.wolfarmor.api.util.Capabilities.registerCapabilities();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.loadComplete((FMLLoadCompleteEvent)event);
      }

   }

   public static final String MOD_VERSION = "3.8.1+alpha.1";
   public static IProxy proxy;
   public static WolfArmorMod instance;
   private final ILogHelper logger;
   private final WolfArmorConfig config;
   private WolfArmorChannel channel;

   public WolfArmorMod() {
      this(new WolfArmorConfig(), new LogHelper());
   }

   WolfArmorMod(WolfArmorConfig configInstance, ILogHelper loggerInstance) {
      this.logger = loggerInstance;
      this.config = configInstance;
   }

   public void preInit(@Nonnull FMLPreInitializationEvent preInitializationEvent) {
      this.logger.initializeLogger(preInitializationEvent.getModLog());
      this.channel = new WolfArmorChannel();
      this.config.initialize(preInitializationEvent.getModConfigurationDirectory() + "/satyrn");
      proxy.preInit(preInitializationEvent);
   }

   public void init(@Nonnull FMLInitializationEvent initializationEvent) {
      proxy.init(initializationEvent);
   }

   public void postInit(@Nonnull FMLPostInitializationEvent postInitializationEvent) {
      proxy.postInit(postInitializationEvent);
   }

   public void loadComplete(@Nonnull FMLLoadCompleteEvent loadCompleteEvent) {
      proxy.loadComplete(loadCompleteEvent);
   }


   @Nonnull
   public static ILogHelper getLogger() {
      return getInstance().logger;
   }

   @Nonnull
   public static WolfArmorConfig getConfig() {
      return getInstance().config;
   }

   public static WolfArmorChannel getNetworkChannel() {
      return getInstance().channel;
   }

   public static IProxy getProxy() {
      return proxy;
   }

   public static WolfArmorMod getInstance() {
      return instance;
   }
}
