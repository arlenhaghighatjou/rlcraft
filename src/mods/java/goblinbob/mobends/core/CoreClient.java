package goblinbob.mobends.core;

import goblinbob.mobends.core.asset.AssetReloadListener;
import goblinbob.mobends.core.asset.AssetsModule;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.event.EntityRenderHandler;
import goblinbob.mobends.core.client.event.FluxHandler;
import goblinbob.mobends.core.client.event.KeyboardHandler;
import goblinbob.mobends.core.client.event.WorldJoinHandler;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.core.connection.ConnectionManager;
import goblinbob.mobends.core.env.EnvironmentModule;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.supporters.SupporterContent;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CoreClient extends Core<CoreClientConfig> {
   private static CoreClient INSTANCE;
   private CoreClientConfig configuration;

   CoreClient() {
      INSTANCE = this;
      this.registerModule(new EnvironmentModule.Factory());
      this.registerModule(new ConnectionManager.Factory());
      this.registerModule(new AssetsModule.Factory());
      this.registerModule(new SupporterContent.Factory());
   }

   public CoreClientConfig getConfiguration() {
      return this.configuration;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      this.configuration = new CoreClientConfig(event.getSuggestedConfigurationFile());
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      PackManager.INSTANCE.initialize(this.configuration);
      KeyboardHandler.initKeyBindings();
      MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
      MinecraftForge.EVENT_BUS.register(new DataUpdateHandler());
      MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
      MinecraftForge.EVENT_BUS.register(new FluxHandler());
      MinecraftForge.EVENT_BUS.register(new WorldJoinHandler());
      IReloadableResourceManager resourceManager = (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
      resourceManager.registerReloadListener(new AssetReloadListener());
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      EntityBenderRegistry.instance.applyConfiguration(this.configuration);
   }

   @Nullable
   public static CoreClient getInstance() {
      return INSTANCE;
   }
}
