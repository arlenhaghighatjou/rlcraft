package com.bloodnbonesgaming.loadingscreens.client;

import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.common.eventhandler.EventBus;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientEventHandler {
   private static boolean replaced = false;

   public ClientEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(GuiOpenEvent.class, EventPriority.HIGHEST, false, this::onGuiOpen);
      bus.register(RenderTickEvent.class, EventPriority.HIGHEST, false, this::onRenderTick);
      bus.register(DrawScreenEvent.Pre.class, EventPriority.HIGHEST, false, this::onDrawScreen);
   }

   public void onGuiOpen(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiDownloadTerrain && !(event.getGui() instanceof GuiDownloadTerrainOverride)) {
         event.setGui(new GuiDownloadTerrainOverride());
      } else if (event.getGui() instanceof GuiScreenWorking && !(event.getGui() instanceof GuiWorkingOverride)) {
         event.setGui(new GuiWorkingOverride());
      } else if (event.getGui() instanceof GuiDisconnected && !(event.getGui() instanceof GuiDisconnectedOverride)) {
         GuiDisconnected gui = (GuiDisconnected)event.getGui();
         event.setGui(new GuiDisconnectedOverride(gui.parentScreen, gui.reason, gui.message));
      }
   }

   public void onDrawScreen(DrawScreenEvent.Pre event) {
      if (event.getGui() instanceof GuiConnecting) {
         GuiConnecting gui = (GuiConnecting)event.getGui();
         Minecraft mc = Minecraft.getMinecraft();
         LoadingScreenHandler.getInstance().renderScreen(mc, gui.width, gui.height);
         if (gui.handshaker == null) {
            gui.drawCenteredString(mc.fontRenderer, I18n.format("connect.connecting", new Object[0]), gui.width / 2, gui.height / 2 - 50, 16777215);
         } else {
            gui.drawCenteredString(mc.fontRenderer, I18n.format("connect.authorizing", new Object[0]), gui.width / 2, gui.height / 2 - 50, 16777215);
         }

         for(int i = 0; i < gui.buttonList.size(); ++i) {
            ((GuiButton)gui.buttonList.get(i)).drawButton(mc, event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
         }

         for(int j = 0; j < gui.labelList.size(); ++j) {
            ((GuiLabel)gui.labelList.get(j)).drawLabel(mc, event.getMouseX(), event.getMouseY());
         }

         event.setCanceled(true);
      }

   }

   public void onRenderTick(RenderTickEvent event) {
      if (!(Minecraft.getMinecraft().loadingScreen instanceof LoadingScreenRendererCustom)) {
         Minecraft.getMinecraft().loadingScreen = new LoadingScreenRendererCustom(Minecraft.getMinecraft());
      }

   }
}
