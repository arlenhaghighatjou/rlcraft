package snownee.kiwi.client.gui.component;

import java.util.Collections;
import java.util.List;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.client.gui.GuiControl;

public class ComponentText extends Component {
   private List<String> lines;
   public int baseColor;
   public boolean dropShadow;
   public int paddingX;
   public int paddingY;

   public ComponentText(GuiControl parent, int width) {
      this(parent, width, 0, 0);
   }

   public ComponentText(GuiControl parent, int width, int paddingX, int paddingY) {
      super(parent, width, AdvancedFontRenderer.INSTANCE.FONT_HEIGHT + 1);
      this.lines = Collections.EMPTY_LIST;
      this.baseColor = 0;
      this.dropShadow = false;
      this.paddingX = paddingX;
      this.paddingY = paddingY;
   }

   public void setText(String text) {
      this.lines = AdvancedFontRenderer.INSTANCE.listFormattedStringToWidth(text, this.width - this.paddingX * 2);
      this.height = this.paddingY * 2 + this.lines.size() * (AdvancedFontRenderer.INSTANCE.FONT_HEIGHT + 1);
   }

   public List<String> getLines() {
      return Collections.unmodifiableList(this.lines);
   }

   public void drawScreen(int offsetX, int offsetY, int relMouseX, int relMouseY, float partialTicks) {
      int x = offsetX + this.left + this.paddingX;
      int y = offsetY + this.top + this.paddingY;

      for(String line : this.lines) {
         AdvancedFontRenderer.INSTANCE.drawString(line, (float)x, (float)y, this.baseColor, this.dropShadow);
         y += AdvancedFontRenderer.INSTANCE.FONT_HEIGHT + 1;
      }

   }
}
