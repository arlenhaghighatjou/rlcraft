package me.ichun.mods.ichunutil.client.gui.config.window;

import java.util.ArrayList;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.element.ElementCatsList;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import net.minecraft.util.ResourceLocation;

public class WindowCats extends Window {
   public ElementCatsList configs;
   public GuiConfigs parent;
   public ConfigBase selectedConfig;

   public WindowCats(GuiConfigs parent, int x, int y, int w, int h, int minW, int minH) {
      super(parent, x, y, w, h, minW, minH, "", true);
      this.parent = parent;
      this.configs = new ElementCatsList(this, 4, 14, this.width - 8, this.height - 8 - 11, 3, false, false);
      this.elements.add(this.configs);
   }

   public void resized() {
      this.posX = 100;
      this.posY = 1;
      this.width = 120;
      this.height = this.workspace.height - 2;
      super.resized();
   }

   public void draw(int mouseX, int mouseY) {
      if (!this.parent.windowConfigs.configs.selectedIdentifier.isEmpty()) {
         for(ElementListTree.Tree tree : this.parent.windowConfigs.configs.trees) {
            if (tree.selected) {
               ConfigBase conf = (ConfigBase)tree.attachedObject;
               if (conf != this.selectedConfig) {
                  this.selectedConfig = conf;
                  this.titleLocale = this.selectedConfig.getModName();
                  this.configs.selectedIdentifier = "";
                  this.configs.trees.clear();

                  for(Entry<ConfigBase.CategoryInfo, ArrayList<ConfigBase.PropInfo>> e : this.selectedConfig.categories.entrySet()) {
                     this.configs.createTree((ResourceLocation)null, e.getKey(), 13, 0, false, false);
                  }
               }
            }
         }

         super.draw(mouseX, mouseY);
      }
   }

   public void elementTriggered(Element element) {
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
