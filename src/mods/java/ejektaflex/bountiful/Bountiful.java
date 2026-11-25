package ejektaflex.bountiful;

import ejektaflex.bountiful.api.BountifulAPIProvider;
import ejektaflex.bountiful.command.BountyCommand;
import ejektaflex.bountiful.config.BountifulIO;
import ejektaflex.bountiful.config.ConfigFile;
import ejektaflex.bountiful.gui.GuiHandler;
import ejektaflex.bountiful.proxy.IProxy;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod(
   modid = "bountiful",
   name = "Bountiful",
   version = "2.2.3",
   modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
   dependencies = "required-after:forge@[14.23.4.2768,15.0.0.0);required-after:forgelin@[1.8.0,1.9.0);",
   acceptedMinecraftVersions = "[1.12.2]"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0017J\u0010\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020&H\u0007J\u0010\u0010'\u001a\u00020!2\u0006\u0010\"\u001a\u00020(H\u0017J\u0010\u0010)\u001a\u00020!2\u0006\u0010\"\u001a\u00020*H\u0017J\u0010\u0010+\u001a\u00020!2\u0006\u0010\"\u001a\u00020,H\u0007R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u0004\u0018\u00010\u00008\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u0015X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R$\u0010\u001a\u001a\u00020\u00018\u0006@\u0006X\u0087.\u00a2\u0006\u0014\n\u0000\u0012\u0004\b\u001b\u0010\u0002\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001f\u00a8\u0006-"},
   d2 = {"Lejektaflex/bountiful/Bountiful;", "Lejektaflex/bountiful/proxy/IProxy;", "()V", "config", "Lejektaflex/bountiful/config/ConfigFile;", "getConfig", "()Lejektaflex/bountiful/config/ConfigFile;", "setConfig", "(Lejektaflex/bountiful/config/ConfigFile;)V", "configDir", "Ljava/io/File;", "getConfigDir", "()Ljava/io/File;", "setConfigDir", "(Ljava/io/File;)V", "instance", "getInstance", "()Lejektaflex/bountiful/Bountiful;", "setInstance", "(Lejektaflex/bountiful/Bountiful;)V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "setLogger", "(Lorg/apache/logging/log4j/Logger;)V", "proxy", "proxy$annotations", "getProxy", "()Lejektaflex/bountiful/proxy/IProxy;", "setProxy", "(Lejektaflex/bountiful/proxy/IProxy;)V", "init", "", "e", "Lnet/minecraftforge/fml/common/event/FMLInitializationEvent;", "loadAPI", "event", "Lnet/minecraftforge/fml/common/event/FMLConstructionEvent;", "postInit", "Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;", "preInit", "Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;", "serverLoad", "Lnet/minecraftforge/fml/common/event/FMLServerStartingEvent;", "Bountiful"}
)
public final class Bountiful implements IProxy {
   @SidedProxy(
      clientSide = "ejektaflex.bountiful.proxy.ClientProxy",
      serverSide = "ejektaflex.bountiful.proxy.CommonProxy"
   )
   @NotNull
   public static IProxy proxy;
   @NotNull
   public static Logger logger;
   @NotNull
   public static File configDir;
   @NotNull
   public static ConfigFile config;
   @Instance
   @Nullable
   private static Bountiful instance;
   public static final Bountiful INSTANCE;

   @NotNull
   public static final IProxy getProxy() {
      IProxy var10000 = proxy;
      if (proxy == null) {
         Intrinsics.throwUninitializedPropertyAccessException("proxy");
      }

      return var10000;
   }

   public static final void setProxy(@NotNull IProxy var0) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      proxy = <set-?>;
   }

   @EventHandler
   public final void loadAPI(@NotNull FMLConstructionEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      BountifulAPIProvider.Companion.changeAPI(InternalAPI.INSTANCE);
   }

   @NotNull
   public final Logger getLogger() {
      Logger var10000 = logger;
      if (logger == null) {
         Intrinsics.throwUninitializedPropertyAccessException("logger");
      }

      return var10000;
   }

   public final void setLogger(@NotNull Logger var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      logger = <set-?>;
   }

   @NotNull
   public final File getConfigDir() {
      File var10000 = configDir;
      if (configDir == null) {
         Intrinsics.throwUninitializedPropertyAccessException("configDir");
      }

      return var10000;
   }

   public final void setConfigDir(@NotNull File var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      configDir = <set-?>;
   }

   @NotNull
   public final ConfigFile getConfig() {
      ConfigFile var10000 = config;
      if (config == null) {
         Intrinsics.throwUninitializedPropertyAccessException("config");
      }

      return var10000;
   }

   public final void setConfig(@NotNull ConfigFile var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      config = <set-?>;
   }

   @Nullable
   public final Bountiful getInstance() {
      return instance;
   }

   public final void setInstance(@Nullable Bountiful var1) {
      instance = <set-?>;
   }

   @EventHandler
   public void preInit(@NotNull FMLPreInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      Logger var10000 = e.getModLog();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "e.modLog");
      logger = var10000;
      BountifulIO var2 = BountifulIO.INSTANCE;
      File var10001 = e.getModConfigurationDirectory();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "e.modConfigurationDirectory");
      configDir = var2.ensureDirectory(var10001, "bountiful");
      ConfigFile var3 = new ConfigFile;
      File var10002 = configDir;
      if (configDir == null) {
         Intrinsics.throwUninitializedPropertyAccessException("configDir");
      }

      var3.<init>(var10002);
      config = var3;
      var3 = config;
      if (config == null) {
         Intrinsics.throwUninitializedPropertyAccessException("config");
      }

      var3.load();
      MinecraftForge.EVENT_BUS.register(ContentRegistry.INSTANCE);
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
      EventBus var5 = MinecraftForge.EVENT_BUS;
      IProxy var7 = proxy;
      if (proxy == null) {
         Intrinsics.throwUninitializedPropertyAccessException("proxy");
      }

      var5.register(var7);
      IProxy var6 = proxy;
      if (proxy == null) {
         Intrinsics.throwUninitializedPropertyAccessException("proxy");
      }

      var6.preInit(e);
   }

   @EventHandler
   public void init(@NotNull FMLInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      IProxy var10000 = proxy;
      if (proxy == null) {
         Intrinsics.throwUninitializedPropertyAccessException("proxy");
      }

      var10000.init(e);
   }

   @EventHandler
   public void postInit(@NotNull FMLPostInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      IProxy var10000 = proxy;
      if (proxy == null) {
         Intrinsics.throwUninitializedPropertyAccessException("proxy");
      }

      var10000.postInit(e);
      ConfigFile var2 = config;
      if (config == null) {
         Intrinsics.throwUninitializedPropertyAccessException("config");
      }

      if (var2.hasChanged()) {
         var2 = config;
         if (config == null) {
            Intrinsics.throwUninitializedPropertyAccessException("config");
         }

         var2.save();
      }

   }

   @EventHandler
   public final void serverLoad(@NotNull FMLServerStartingEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      e.registerServerCommand(new BountyCommand());
   }

   private Bountiful() {
   }

   static {
      Bountiful var0 = new Bountiful();
      INSTANCE = var0;
      instance = var0;
   }
}
