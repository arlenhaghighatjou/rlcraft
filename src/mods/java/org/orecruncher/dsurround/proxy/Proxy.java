package org.orecruncher.dsurround.proxy;

import javax.annotation.Nonnull;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.CapabilityEntityData;
import org.orecruncher.dsurround.commands.CommandDS;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.server.services.ServiceManager;
import org.orecruncher.lib.Localization;

public class Proxy implements IProxy {
   public Proxy() {
   }

   protected void registerLanguage() {
      Localization.initialize(Side.SERVER, "dsurround");
   }

   protected static void register(Class<?> clazz) {
      ModBase.log().debug("Registering for Forge events: %s", new Object[]{clazz.getName()});
      MinecraftForge.EVENT_BUS.register(clazz);
   }

   protected void eventBusRegistrations() {
      register(CapabilityEntityData.EventHandler.class);
      register(RegistryManager.class);
   }

   public boolean isRunningAsServer() {
      return true;
   }

   public Side effectiveSide() {
      return Side.SERVER;
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      this.registerLanguage();
      this.eventBusRegistrations();
      CapabilityEntityData.register();
      CapabilityDimensionInfo.register();
   }

   public void init(@Nonnull FMLInitializationEvent event) {
      ModEnvironment.initialize();
      Network.initialize();
   }

   public void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
      RegistryManager.initialize();
   }

   public void serverAboutToStart(@Nonnull FMLServerAboutToStartEvent event) {
      ServiceManager.initialize();
   }

   public void serverStarting(@Nonnull FMLServerStartingEvent event) {
      MinecraftServer server = event.getServer();
      ICommandManager command = server.getCommandManager();
      ServerCommandManager serverCommand = (ServerCommandManager)command;
      serverCommand.registerCommand(new CommandDS());
   }

   public void serverStopped(@Nonnull FMLServerStoppedEvent event) {
      ServiceManager.deinitialize();
   }

   public IThreadListener getThreadListener(@Nonnull MessageContext context) {
      if (context.side.isServer()) {
         return context.getServerHandler().player.getServer();
      } else {
         throw new IllegalStateException("Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
      }
   }
}
