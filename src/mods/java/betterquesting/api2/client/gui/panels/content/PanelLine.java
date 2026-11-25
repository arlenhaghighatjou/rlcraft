package betterquesting.api2.client.gui.panels.content;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class PanelLine implements IGuiPanel {
   private final IGuiRect bounds;
   private final IGuiLine line;
   private final IGuiRect start;
   private final IGuiRect end;
   private final IGuiColor color;
   private final int width;
   private boolean enabled = true;

   public PanelLine(IGuiRect start, IGuiRect end, IGuiLine line, int width, IGuiColor color, int drawOrder) {
      this.start = start;
      this.end = end;
      this.line = line;
      this.width = width;
      this.color = color;
      this.bounds = new GuiRectangle(0, 0, 0, 0, drawOrder);
      this.bounds.setParent(start);
   }

   public IGuiRect getTransform() {
      return this.bounds;
   }

   public void initPanel() {
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      GlStateManager.pushMatrix();
      this.line.drawLine(this.start, this.end, this.width, this.color, partialTick);
      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int button) {
      return false;
   }

   public boolean onMouseRelease(int mx, int my, int button) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }
}
