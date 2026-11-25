package me.ichun.mods.ichunutil.client.gui.config.window;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.element.ElementPropSetter;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;

public class WindowSetter extends Window {
   public ElementPropSetter props;
   public GuiConfigs parent;
   public ConfigBase.CategoryInfo selectedCat;

   public WindowSetter(GuiConfigs parent, int x, int y, int w, int h, int minW, int minH) {
      super(parent, x, y, w, h, minW, minH, "", true);
      this.parent = parent;
      this.props = new ElementPropSetter(this, 4, 14, this.width - 8, this.height - 8 - 11, 0);
      this.elements.add(this.props);
   }

   public void resized() {
      this.posX = 219;
      this.posY = 1;
      this.width = this.workspace.width - 1 - this.posX;
      this.height = this.workspace.height - 2;
      super.resized();

      for(ElementPropSetter.Tree tree : this.props.trees) {
         if (tree.element != null) {
            tree.element.resized();
         }
      }

   }

   public void draw(int mouseX, int mouseY) {
      if (!this.parent.windowCats.configs.selectedIdentifier.isEmpty()) {
         for(ElementListTree.Tree tree : this.parent.windowCats.configs.trees) {
            if (tree.selected) {
               ConfigBase.CategoryInfo cat = (ConfigBase.CategoryInfo)tree.attachedObject;
               if (!cat.equals(this.selectedCat) && this.parent.windowCats.selectedConfig != null) {
                  this.props.saveTimeout = 0;
                  this.props.save();
                  this.selectedCat = cat;
                  this.titleLocale = cat.name;
                  this.props.trees.clear();
                  this.props.sliderProg = 0.0D;

                  for(ConfigBase.PropInfo prop : (ArrayList)this.parent.windowCats.selectedConfig.categories.get(this.selectedCat)) {
                     this.props.createTree(this.parent.windowCats.selectedConfig, prop, 17);
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
