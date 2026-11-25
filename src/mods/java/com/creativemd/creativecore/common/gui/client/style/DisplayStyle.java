package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public abstract class DisplayStyle {
   public static DisplayStyle emptyDisplay = new DisplayStyle() {
      public void renderStyle(GuiRenderHelper helper, int width, int height) {
      }
   };

   public DisplayStyle() {
   }

   public void renderStyle(int x, int y, GuiRenderHelper helper, int width, int height) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, 0.0F);
      this.renderStyle(helper, width, height);
      GlStateManager.popMatrix();
   }

   public abstract void renderStyle(GuiRenderHelper var1, int var2, int var3);
}
