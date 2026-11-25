package noppes.vc.shared.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class GuiLabel {
   private static int DefaultColor = 4210752;
   public String label;
   public int x;
   public int y;
   public int color;
   public boolean enabled;
   public int id;

   public GuiLabel(int id, Object label, int x, int y, int color) {
      this.enabled = true;
      this.id = id;
      this.label = I18n.translateToLocal(label.toString());
      this.x = x;
      this.y = y;
      this.color = color;
   }

   public GuiLabel(int id, Object label, int x, int y) {
      this(id, label, x, y, DefaultColor);
   }

   public void drawLabel(GuiScreen gui, FontRenderer fontRenderer) {
      if (this.enabled) {
         fontRenderer.drawString(this.label, this.x, this.y, this.color);
      }

   }

   public void center(int width) {
      int size = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.label);
      this.x += (width - size) / 2;
   }
}
