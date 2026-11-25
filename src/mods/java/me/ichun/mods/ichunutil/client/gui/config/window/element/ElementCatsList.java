package me.ichun.mods.ichunutil.client.gui.config.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;

public class ElementCatsList extends ElementListTree {
   public int mX;
   public int mY;

   public ElementCatsList(Window window, int x, int y, int w, int h, int ID, boolean igMin, boolean drag) {
      super(window, x, y, w, h, ID, igMin, drag);
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      this.mX = mouseX;
      this.mY = mouseY;
      super.draw(mouseX, mouseY, hover);
   }

   public String tooltip() {
      int treeHeight = 0;
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
      }

      int scrollHeight = 0;
      if (treeHeight1 > this.height) {
         scrollHeight = (int)((double)(this.height - treeHeight1) * this.sliderProg);
      }

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
         if (this.mX >= this.posX && this.mX < this.posX + this.width + (treeHeight1 > this.height ? 10 : 0) && this.mY >= this.posY + treeHeight + scrollHeight && this.mY < this.posY + treeHeight + scrollHeight + tree.getHeight()) {
            return ((ConfigBase.CategoryInfo)tree.attachedObject).comment;
         }

         treeHeight += tree.getHeight();
      }

      return null;
   }
}
