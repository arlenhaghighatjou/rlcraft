package me.ichun.mods.ichunutil.client.gui.window.element;

import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class ElementSelector extends Element {
   public String tooltip;
   public TreeMap<String, Object> choices = new TreeMap(Ordering.natural());
   public String selected;

   public ElementSelector(Window window, int x, int y, int w, int h, int ID, String tip, String selected) {
      super(window, x, y, w, h, ID, false);
      this.tooltip = tip;
      this.selected = selected;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.width, (double)this.height, 0.0D);
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBackgroundInactive[0], this.parent.workspace.currentTheme.elementInputBackgroundInactive[1], this.parent.workspace.currentTheme.elementInputBackgroundInactive[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
      boolean hoverLeft = mouseX >= this.posX && mouseX <= this.posX + this.height && mouseY >= this.posY && mouseY <= this.posY + this.height;
      if (hoverLeft) {
         if (Mouse.isButtonDown(0)) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownClick[0], this.parent.workspace.currentTheme.elementInputUpDownClick[1], this.parent.workspace.currentTheme.elementInputUpDownClick[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
         } else {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownHover[0], this.parent.workspace.currentTheme.elementInputUpDownHover[1], this.parent.workspace.currentTheme.elementInputUpDownHover[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
         }
      } else {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
      }

      this.parent.workspace.getFontRenderer().drawString("◀", (float)(this.getPosX() + this.height - (this.height + this.parent.workspace.getFontRenderer().getStringWidth("◀")) / 2), (float)(this.getPosY() + this.height - (this.height + this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
      boolean hoverRight = mouseX >= this.posX + this.width - this.height && mouseX <= this.posX + this.width && mouseY >= this.posY && mouseY <= this.posY + this.height;
      if (hoverRight) {
         if (Mouse.isButtonDown(0)) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownClick[0], this.parent.workspace.currentTheme.elementInputUpDownClick[1], this.parent.workspace.currentTheme.elementInputUpDownClick[2], 255, (double)(this.getPosX() + this.width - this.height), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
         } else {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownHover[0], this.parent.workspace.currentTheme.elementInputUpDownHover[1], this.parent.workspace.currentTheme.elementInputUpDownHover[2], 255, (double)(this.getPosX() + this.width - this.height), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
         }
      } else {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)(this.getPosX() + this.width - this.height), (double)this.getPosY(), (double)this.height, (double)this.height, 0.0D);
      }

      this.parent.workspace.getFontRenderer().drawString("▶", (float)(this.getPosX() + this.width - (this.height + this.parent.workspace.getFontRenderer().getStringWidth("▶")) / 2), (float)(this.getPosY() + this.height - (this.height + this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
      if (!this.choices.containsKey(this.selected)) {
         if (this.choices.isEmpty()) {
            this.selected = "";
         } else {
            this.selected = (String)((Entry)this.choices.entrySet().iterator().next()).getKey();
         }
      }

      this.parent.workspace.getFontRenderer().drawString(this.parent.workspace.reString(this.selected, this.width - this.height - this.height), (float)(this.getPosX() + this.height + 2), (float)(this.getPosY() + 2), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      if (id == 0) {
         String before = "";
         String after = "";
         ArrayList<String> names = new ArrayList(this.choices.keySet());
         Collections.sort(names);

         for(int i = 0; i < names.size(); ++i) {
            if (((String)names.get(i)).equals(this.selected)) {
               int beforeI = i - 1;
               int afterI = i + 1;
               if (i == 0) {
                  beforeI = names.size() - 1;
               }

               if (i == names.size() - 1) {
                  afterI = 0;
               }

               before = (String)names.get(beforeI);
               after = (String)names.get(afterI);
            }
         }

         if (mouseX >= this.posX && mouseX <= this.posX + this.height && mouseY >= this.posY && mouseY <= this.posY + this.height) {
            this.selected = before;
         } else if (mouseX >= this.posX + this.width - this.height && mouseX <= this.posX + this.width && mouseY >= this.posY && mouseY <= this.posY + this.height) {
            this.selected = after;
         }
      }

      return false;
   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      String before = "";
      String after = "";
      ArrayList<String> names = new ArrayList(this.choices.keySet());
      Collections.sort(names);

      for(int i = 0; i < names.size(); ++i) {
         if (((String)names.get(i)).equals(this.selected)) {
            int beforeI = i - 1;
            int afterI = i + 1;
            if (i == 0) {
               beforeI = names.size() - 1;
            }

            if (i == names.size() - 1) {
               afterI = 0;
            }

            before = (String)names.get(beforeI);
            after = (String)names.get(afterI);
         }
      }

      if (k > 0) {
         this.selected = before;
      } else {
         this.selected = after;
      }

      return false;
   }

   public String tooltip() {
      return this.tooltip;
   }
}
