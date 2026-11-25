package com.oblivioussp.spartanhudbaubles.client.gui;

import com.oblivioussp.spartanhudbaubles.util.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class GuiConfigSHB extends GuiConfig {
   private static final int BUTTON_ID_PREVIEW_HUD = 3000;

   public GuiConfigSHB(GuiScreen guiScreen) {
      super(guiScreen, getConfigElements(), "spartanhudbaubles", false, false, "Spartan HUD: Baubles Config", GuiConfig.getAbridgedConfigPath(Config.getConfig().toString()));
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> elements = new ArrayList();
      elements.addAll((new ConfigElement(Config.getConfig().getCategory("client"))).getChildElements());
      return elements;
   }

   public void initGui() {
      String hudEdit = I18n.format("config.spartanhudbaubles:preview_hud", new Object[0]);
      int width = this.fontRenderer.getStringWidth(hudEdit) + 20;
      this.buttonList.add(new GuiButtonExt(3000, 5, 5, width, 20, hudEdit));
      super.initGui();
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 3000) {
         if (this.entryList.hasChangedEntry(true) && Loader.isModLoaded(this.modID)) {
            boolean requiresMcRestart = this.entryList.saveConfigElements();
            OnConfigChangedEvent ev = new OnConfigChangedEvent(this.modID, this.configID, this.isWorldRunning, requiresMcRestart);
            MinecraftForge.EVENT_BUS.post(ev);
            if (!ev.getResult().equals(Result.DENY)) {
               MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(this.modID, this.configID, this.isWorldRunning, requiresMcRestart));
            }

            this.needsRefresh = true;
         }

         this.mc.displayGuiScreen(new GuiPreviewHUD(this));
      }

      super.actionPerformed(button);
   }
}
