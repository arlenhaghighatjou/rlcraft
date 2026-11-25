package betterquesting.api2.client.gui.resources.lines;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;

public class GuiLineSequence implements IGuiLine {
   public final IGuiLine[] lines;
   private final float interval;

   public GuiLineSequence(float interval, IGuiLine... lines) {
      this.lines = lines;
      this.interval = interval;
   }

   public void drawLine(IGuiRect start, IGuiRect end, int width, IGuiColor color, float partialTick) {
      this.getCurrentLine().drawLine(start, end, width, color, partialTick);
   }

   public IGuiLine getCurrentLine() {
      return this.lines[(int)Math.floor((double)System.currentTimeMillis() / 1000.0D % (double)((float)this.lines.length * this.interval) / (double)this.interval)];
   }

   public IGuiLine[] getAllLines() {
      return this.lines;
   }
}
