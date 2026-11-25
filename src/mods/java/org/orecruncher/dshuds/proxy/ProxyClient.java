package org.orecruncher.dshuds.proxy;

import java.util.Arrays;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dshuds.hud.LightLevelHUD;
import org.orecruncher.dshuds.keyboard.KeyHandler;
import org.orecruncher.lib.ForgeUtils;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.task.Scheduler;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
   public ProxyClient() {
   }

   public boolean isRunningAsServer() {
      return false;
   }

   public Side effectiveSide() {
      return FMLCommonHandler.instance().getEffectiveSide();
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      Localization.initialize(Side.CLIENT, "dshuds");
      KeyHandler.init();
   }

   public void postInit(@Nonnull FMLPostInitializationEvent event) {
      ModMetadata data = ForgeUtils.getModMetadata("dshuds");
      if (data != null) {
         data.name = Localization.loadString("dshuds.metadata.Name");
         data.credits = Localization.loadString("dshuds.metadata.Credits");
         data.description = Localization.loadString("dshuds.metadata.Description");
         data.authorList = Arrays.asList(StringUtils.split(Localization.loadString("dshuds.metadata.Authors"), ','));
      }

      LightLevelHUD.initialize();
   }

   public void clientConnect(@Nonnull ClientConnectedToServerEvent event) {
      Scheduler.schedule(Side.CLIENT, () -> {
         this.connectionTime = System.currentTimeMillis();
      });
   }

   public void clientDisconnect(@Nonnull ClientDisconnectionFromServerEvent event) {
      Scheduler.schedule(Side.CLIENT, () -> {
         this.connectionTime = 0L;
      });
   }
}
