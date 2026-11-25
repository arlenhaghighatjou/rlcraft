package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import net.minecraft.util.ResourceLocation;

public class WindowConfigs extends Window {
   public ElementListTree configs;

   public WindowConfigs(IWorkspace parent, int x, int y, int w, int h, int minW, int minH) {
      super(parent, x, y, w, h, minW, minH, "ichunutil.config.gui.options", true);
      this.elements.add(new ElementButton(this, 10, this.height - 22, 60, 16, -1, false, 0, 1, "gui.done"));
      this.configs = new ElementListTree(this, 4, 14, this.width - 8, this.height - 3 - 22 - 16, 3, false, false);
      this.elements.add(this.configs);

      for(ConfigBase config : ConfigHandler.configs) {
         this.configs.createTree((ResourceLocation)null, config, 13, 0, false, false);
      }

   }

   public void resized() {
      this.posX = 1;
      this.posY = 1;
      this.width = 100;
      this.height = this.workspace.height - 2;
      super.resized();
   }

   public void elementTriggered(Element element) {
      if (element.id == -1) {
         this.workspace.mc.displayGuiScreen(((GuiConfigs)this.workspace).oriScreen);
         if (this.workspace.mc.currentScreen == null) {
            this.workspace.mc.setIngameFocus();
         }
      }

   }

   public boolean canBeDragged() {
      return false;
   }

   public boolean isStatic() {
      return true;
   }

   public boolean canMinimize() {
      return false;
   }
}
