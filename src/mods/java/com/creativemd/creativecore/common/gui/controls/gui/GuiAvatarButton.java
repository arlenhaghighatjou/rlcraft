package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.client.avatar.Avatar;
import java.util.ArrayList;

public abstract class GuiAvatarButton extends GuiAvatarLabelClickable {
   public GuiAvatarButton(String caption, int x, int y, Avatar avatar) {
      super(caption, x, y, -1, avatar);
   }

   public GuiAvatarButton(String name, String caption, int x, int y, int width, int height, Avatar avatar) {
      super(name, caption, x, y, -1, avatar, true);
      this.width = width + this.getContentOffset() * 2;
      this.height = height + this.getContentOffset() * 2;
   }

   public GuiAvatarButton(String caption, int x, int y, int width, int height, Avatar avatar) {
      this(caption, caption, x, y, width, height, avatar);
   }

   public boolean hasBorder() {
      return true;
   }

   public boolean hasBackground() {
      return true;
   }

   public ArrayList<String> getTooltip() {
      ArrayList<String> tooltip = new ArrayList();
      tooltip.add(this.caption);
      return tooltip;
   }
}
