package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiPanel extends GuiParent {
   public static Style panelStyle = new Style("panel", new ColoredDisplayStyle(0, 0, 0, 140), DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay);

   public GuiPanel(String name, int x, int y, int width, int height) {
      super(name, x, y, width, height);
   }

   protected void renderBackground(GuiRenderHelper helper, Style style) {
      GlStateManager.pushMatrix();
      DisplayStyle display = panelStyle.getBorder(this);
      display.renderStyle(helper, this.width, this.borderWidth);
      display.renderStyle(helper, this.borderWidth, this.height);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, (float)(this.height - this.borderWidth), 0.0F);
      display.renderStyle(helper, this.width, this.borderWidth);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width - this.borderWidth), 0.0F, 0.0F);
      display.renderStyle(helper, this.borderWidth, this.height);
      GlStateManager.popMatrix();
      GlStateManager.translate((float)this.borderWidth, (float)this.borderWidth, 0.0F);
   }
}
