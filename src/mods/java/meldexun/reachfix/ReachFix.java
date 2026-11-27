package meldexun.reachfix;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.network.CPacketHandlerSyncConfig;
import meldexun.reachfix.network.SPacketSyncConfig;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "reachfix"
)
public class ReachFix {
   public static final String MODID = "reachfix";
   public static final Logger LOGGER = LogManager.getLogger("reachfix");
   public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("reachfix");
   public static boolean isSpartanWeaponryInstalled;

   public ReachFix() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         this.onFMLConstructionEvent((FMLConstructionEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.onFMLPostInitializationEvent((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onFMLServerStartingEvent((FMLServerStartingEvent)event);
      }

   }

   public void onFMLConstructionEvent(FMLConstructionEvent event) {
      this.register(MinecraftForge.EVENT_BUS);
      NETWORK.registerMessage(new CPacketHandlerSyncConfig(), SPacketSyncConfig.class, SPacketSyncConfig::new, 1, Side.CLIENT);
   }

   public void onFMLPostInitializationEvent(FMLPostInitializationEvent event) {
      isSpartanWeaponryInstalled = Loader.isModLoaded("spartanweaponry");
   }

   public void onFMLServerStartingEvent(FMLServerStartingEvent event) {
      loadLocalConfig(true);
   }

   public void register(EventBus bus) {
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLoggedInEvent);
      bus.register(PlayerChangedDimensionEvent.class, this::onPlayerChangedDimensionEvent);
      bus.register(PlayerRespawnEvent.class, this::onPlayerRespawnEvent);
      bus.register(OnConfigChangedEvent.class, this::onConfigChangedEvent);
   }

   public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
      sendServerConfig((EntityPlayerMP)event.player);
      ReachFixUtil.updateBaseReachModifier(event.player);
   }

   public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      ReachFixUtil.updateBaseReachModifier(event.player);
   }

   public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
      ReachFixUtil.updateBaseReachModifier(event.player);
   }

   public void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("reachfix")) {
         ConfigManager.sync("reachfix", Type.INSTANCE);
         Minecraft mc = Minecraft.getMinecraft();
         loadLocalConfig(mc.world == null || mc.isIntegratedServerRunning());
      }

   }

   public static void loadLocalConfig(boolean loadAllSettings) {
      if (loadAllSettings) {
         ReachFixConfig.MASTER_CONFIG.copyAllSettings(ReachFixConfig.SLAVE_CONFIG);
      } else {
         ReachFixConfig.MASTER_CONFIG.copyClientSettings(ReachFixConfig.SLAVE_CONFIG);
      }

   }

   public static void sendServerConfig(@Nullable EntityPlayerMP player) {
      if (player != null) {
         NETWORK.sendTo(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG), player);
      } else {
         NETWORK.sendToAll(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG));
      }

   }
}
