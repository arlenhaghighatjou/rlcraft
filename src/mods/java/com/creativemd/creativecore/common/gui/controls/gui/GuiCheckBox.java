package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import net.minecraft.init.SoundEvents;
import org.lwjgl.util.Color;

public class GuiCheckBox extends GuiControl {
   public static final int disabledColor = ColorUtils.RGBAToInt(new Color(100, 100, 100));
   public static final DisplayStyle partialStyle = new ColoredDisplayStyle(-1);
   public static final int checkBoxWidth = 7;
   public boolean value;
   public String title;
   public boolean partial;

   public GuiCheckBox(String name, String title, int x, int y, boolean value) {
      super(name, x, y, 7 + GuiRenderHelper.instance.getStringWidth(title) + 3, 8);
      this.value = false;
      this.partial = false;
      this.value = value;
      this.title = title;
   }

   public GuiCheckBox(String title, int x, int y, boolean value) {
      this(title, title, x, y, value);
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      int yoffset = 0;
      if (!this.enabled) {
         style.getDisableEffect(this).renderStyle(0, yoffset, helper, 7, 7);
      }

      style.getBorder(this).renderStyle(0, yoffset, helper, 7, 7);
      style.getBackground(this).renderStyle(1, yoffset + 1, helper, 5, 5);
      if (this.value) {
         helper.font.drawString("x", 1, yoffset - 1, this.enabled ? -1 : disabledColor);
      } else if (this.partial) {
         partialStyle.renderStyle(2, yoffset + 2, helper, 3, 3);
      }

      helper.font.drawStringWithShadow(this.title, 10.0F, (float)yoffset, this.enabled ? -1 : disabledColor);
   }

   public boolean hasBorder() {
      return false;
   }

   public boolean hasBackground() {
      return false;
   }

   public boolean mousePressed(int posX, int posY, int button) {
      playSound(SoundEvents.UI_BUTTON_CLICK);
      this.value = !this.value;
      this.raiseEvent(new GuiControlChangedEvent(this));
      return true;
   }

   protected void renderForeground(GuiRenderHelper helper, Style style) {
   }
}
