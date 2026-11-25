package org.orecruncher.dsurround.proxy;

import javax.annotation.Nonnull;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy {
   boolean isRunningAsServer();

   Side effectiveSide();

   default void preInit(@Nonnull FMLPreInitializationEvent event) {
   }

   default void init(@Nonnull FMLInitializationEvent event) {
   }

   default void postInit(@Nonnull FMLPostInitializationEvent event) {
   }

   default void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
   }

   default void clientConnect(@Nonnull ClientConnectedToServerEvent event) {
   }

   default void clientDisconnect(@Nonnull ClientDisconnectionFromServerEvent event) {
   }

   default void serverAboutToStart(@Nonnull FMLServerAboutToStartEvent event) {
   }

   default void serverStarting(@Nonnull FMLServerStartingEvent event) {
   }

   default void serverStopping(@Nonnull FMLServerStoppingEvent event) {
   }

   default void serverStopped(@Nonnull FMLServerStoppedEvent event) {
   }

   IThreadListener getThreadListener(@Nonnull MessageContext var1);
}
