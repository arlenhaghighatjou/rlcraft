package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.Color;

public class GuiListBox extends GuiScrollBox {
   protected List<String> lines = new ArrayList();
   public int selected = -1;

   public GuiListBox(String name, int x, int y, int width, int height, List<String> lines) {
      super(name, x, y, width, height);
      this.lines = lines;
      this.reloadControls();
   }

   public String getSelected() {
      return this.selected != -1 ? this.get(this.selected) : "";
   }

   public void clear() {
      this.lines.clear();
      this.selected = -1;
      this.maxScroll = 0;
      this.scrolled.setStart(0.0D);
      this.reloadControls();
   }

   public int size() {
      return this.lines.size();
   }

   public String get(int id) {
      return (String)this.lines.get(id);
   }

   public void add(String input) {
      this.lines.add(input);
      this.reloadControls();
   }

   public void remove(int id) {
      this.lines.remove(id);
      this.reloadControls();
   }

   public void onLineClicked(GuiControl control) {
      int index = this.controls.indexOf(control);
      if (index != -1) {
         if (this.selected != -1 && this.selected < this.controls.size()) {
            ((GuiLabel)this.controls.get(this.selected)).color = 14737632;
         }

         this.selected = index;
         ((GuiLabel)this.controls.get(this.selected)).color = 16777000;
         this.onSelectionChange();
      }

   }

   public void reloadControls() {
      this.controls.clear();

      for(int i = 0; i < this.lines.size(); ++i) {
         int color = 14737632;
         if (i == this.selected) {
            color = 16777000;
         }

         GuiClickableLabel label = new GuiClickableLabel((String)this.lines.get(i), 0, i * 15, this.width - this.scrollbarWidth - 2 - this.getContentOffset() * 2, 8, color) {
            public int getColor() {
               return this.isMouseOver() && this.color != 16777000 ? ColorUtils.RGBAToInt(new Color(255, 255, 100)) : this.color;
            }

            public void onClicked(int x, int y, int button) {
               GuiListBox.this.onLineClicked(this);
            }
         };
         this.controls.add(label);
      }

      this.refreshControls();
   }

   public void onSelectionChange() {
      this.raiseEvent(new GuiControlChangedEvent(this));
   }
}
