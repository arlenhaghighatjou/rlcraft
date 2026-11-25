package com.bloodnbonesgaming.loadingscreens.client;

import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiConnectingWrapper;
import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiDisconnectedOverride;
import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiDownloadTerrainOverride;
import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiWorkingOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientEventHandler {
   private static boolean replaced = false;

   public ClientEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onGuiOpen(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiDownloadTerrain && !(event.getGui() instanceof GuiDownloadTerrainOverride)) {
         event.setGui(new GuiDownloadTerrainOverride());
      } else if (event.getGui() instanceof GuiScreenWorking && !(event.getGui() instanceof GuiWorkingOverride)) {
         event.setGui(new GuiWorkingOverride());
      } else if (event.getGui() instanceof GuiConnecting && !(event.getGui() instanceof GuiConnectingWrapper)) {
         event.setGui(new GuiConnectingWrapper((GuiConnecting)event.getGui()));
      } else if (event.getGui() instanceof GuiDisconnected && !(event.getGui() instanceof GuiDisconnectedOverride)) {
         GuiDisconnected gui = (GuiDisconnected)event.getGui();
         event.setGui(new GuiDisconnectedOverride(gui.parentScreen, gui.reason, gui.message));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onRenderTick(RenderTickEvent event) {
      if (!(Minecraft.getMinecraft().loadingScreen instanceof LoadingScreenRendererCustom)) {
         Minecraft.getMinecraft().loadingScreen = new LoadingScreenRendererCustom(Minecraft.getMinecraft());
      }

   }
}
