package reborncore.client.gui;

import net.minecraft.client.gui.Gui;

public class GuiUtil {
   public GuiUtil() {
   }

   public static void drawTooltipBox(int x, int y, int w, int h) {
      int bg = -267386864;
      drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
      drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
      drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);
      drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
      drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
      int grad1 = 1347420415;
      int grad2 = 1344798847;
      drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
      drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
      drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
      drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
   }

   public static void drawGradientRect(int x, int y, int w, int h, int colour1, int colour2) {
      (new GuiUtil.GuiHook()).drawGradientRect(x, y, x + w, y + h, colour1, colour2);
   }

   public static class GuiHook extends Gui {
      public GuiHook() {
      }

      public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
         super.drawGradientRect(par1, par2, par3, par4, par5, par6);
      }
   }
}
