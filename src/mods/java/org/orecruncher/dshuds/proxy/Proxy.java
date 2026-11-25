package org.orecruncher.dshuds.proxy;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.lib.Localization;

public class Proxy {
   protected long connectionTime = 0L;

   public Proxy() {
   }

   public boolean isRunningAsServer() {
      return true;
   }

   public long currentSessionDuration() {
      return System.currentTimeMillis() - this.connectionTime;
   }

   public Side effectiveSide() {
      return Side.SERVER;
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      Localization.initialize(Side.SERVER, "orelib");
   }

   public void init(@Nonnull FMLInitializationEvent event) {
   }

   public void postInit(@Nonnull FMLPostInitializationEvent event) {
   }

   public void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
   }

   public void clientConnect(@Nonnull ClientConnectedToServerEvent event) {
   }

   public void clientDisconnect(@Nonnull ClientDisconnectionFromServerEvent event) {
   }
}
