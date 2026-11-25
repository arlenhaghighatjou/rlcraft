package org.orecruncher.lib.gui;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiMessageDialog;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigBase extends GuiConfig {
   public GuiConfigBase(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title) {
      super(parentScreen, configElements, modID, allRequireWorldRestart, allRequireMcRestart, title);
   }

   protected void doFixups() {
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 2000) {
         boolean flag = true;

         try {
            if ((this.configID != null || this.parentScreen == null || !(this.parentScreen instanceof GuiConfig)) && this.entryList.hasChangedEntry(true)) {
               boolean requiresMcRestart = this.entryList.saveConfigElements();
               this.doFixups();
               if (Loader.isModLoaded(this.modID)) {
                  ConfigChangedEvent event = new OnConfigChangedEvent(this.modID, this.configID, this.isWorldRunning, requiresMcRestart);
                  MinecraftForge.EVENT_BUS.post(event);
                  if (!event.getResult().equals(Result.DENY)) {
                     MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(this.modID, this.configID, this.isWorldRunning, requiresMcRestart));
                  }

                  if (requiresMcRestart) {
                     flag = false;
                     this.mc.displayGuiScreen(new GuiMessageDialog(this.parentScreen, "fml.configgui.gameRestartTitle", new TextComponentString(I18n.format("fml.configgui.gameRestartRequired", new Object[0])), "fml.configgui.confirmRestartMessage"));
                  }

                  if (this.parentScreen instanceof GuiConfig) {
                     ((GuiConfig)this.parentScreen).needsRefresh = true;
                  }
               }
            }
         } catch (Throwable var5) {
            FMLLog.log.error("Error performing GuiConfig action:", var5);
         }

         if (flag) {
            this.mc.displayGuiScreen(this.parentScreen);
         }
      } else if (button.id == 2001) {
         this.entryList.setAllToDefault(this.chkApplyGlobally.isChecked());
      } else if (button.id == 2002) {
         this.entryList.undoAllChanges(this.chkApplyGlobally.isChecked());
      }

   }
}
