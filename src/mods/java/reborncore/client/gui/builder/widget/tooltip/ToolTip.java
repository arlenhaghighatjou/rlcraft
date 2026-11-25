package reborncore.client.gui.builder.widget.tooltip;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.gui.FontRenderer;
import reborncore.client.gui.GuiUtil;

public class ToolTip {
   protected ArrayList<ToolTipLine> lines = new ArrayList();

   public ToolTip(String... textLines) {
      for(String text : textLines) {
         this.lines.add(new ToolTipLine(text));
      }

   }

   public ToolTip(ToolTipLine... toolTipLines) {
      Collections.addAll(this.lines, toolTipLines);
   }

   public ToolTip(int linesSize) {
      for(int i = 0; i < linesSize; ++i) {
         this.lines.add(new ToolTipLine());
      }

   }

   public void addLine(ToolTipLine toolTipLine) {
      this.lines.add(toolTipLine);
   }

   public void removeLine(int index) {
      this.lines.remove(index);
   }

   public ToolTipLine getLine(int index) {
      return (ToolTipLine)this.lines.get(index);
   }

   public ArrayList<ToolTipLine> getLines() {
      return this.lines;
   }

   protected void refresh() {
   }

   public void draw(FontRenderer font, int mouseX, int mouseY) {
      this.refresh();
      int maxLineLength = 0;
      int textX = mouseX + 3;
      int textY = mouseY + 3;

      for(ToolTipLine toolTipLine : this.lines) {
         toolTipLine.draw(font, textX, textY);
         textY += font.FONT_HEIGHT + 3;
         int lineWidth = toolTipLine.getWidth(font);
         if (lineWidth > maxLineLength) {
            maxLineLength = lineWidth;
         }
      }

      GuiUtil.drawTooltipBox(mouseX, mouseY, maxLineLength, textY + 3);
   }
}
