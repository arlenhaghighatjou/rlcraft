package reborncore.client.gui.builder.widget.tooltip;

import net.minecraft.client.gui.FontRenderer;

public class ToolTipLine {
   private String line;
   private int color;
   private boolean shadowed;

   public ToolTipLine(String line, int color, boolean shadowed) {
      this.line = line;
      this.color = color;
      this.shadowed = shadowed;
   }

   public ToolTipLine(String line, int color) {
      this(line, color, false);
   }

   public ToolTipLine(String line, boolean shadowed) {
      this(line, 16777215, shadowed);
   }

   public ToolTipLine(String line) {
      this(line, 16777215, false);
   }

   public ToolTipLine() {
      this("");
   }

   public String getLine() {
      return this.line;
   }

   public void setLine(String line) {
      this.line = line;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public boolean isShadowed() {
      return this.shadowed;
   }

   public void setShadowed(boolean shadowed) {
      this.shadowed = shadowed;
   }

   public int getWidth(FontRenderer fontRenderer) {
      return fontRenderer.getStringWidth(this.getLine());
   }

   public void draw(FontRenderer fontRenderer, int x, int y) {
      fontRenderer.drawString(this.getLine(), (float)x, (float)y, this.color, this.isShadowed());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ToolTipLine that = (ToolTipLine)o;
         return this.color == that.color && this.shadowed == that.shadowed && this.line.equals(that.line);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.line.hashCode();
      result = 31 * result + this.color;
      result = 31 * result + (this.shadowed ? 1 : 0);
      return result;
   }
}
