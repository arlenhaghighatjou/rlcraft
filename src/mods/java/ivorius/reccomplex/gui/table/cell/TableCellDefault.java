package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class TableCellDefault implements TableCell {
   protected String id;
   private boolean hidden = false;
   private Bounds bounds = new Bounds(0, 0, 0, 0);
   protected List<String> tooltip;
   public boolean enabled = true;

   public TableCellDefault(String id) {
      this.id = id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Nullable
   public String getID() {
      return this.id;
   }

   public List<String> getTooltip() {
      return this.tooltip;
   }

   public void setTooltip(List<String> tooltip) {
      this.tooltip = tooltip;
   }

   public TableCellDefault withTooltip(List<String> tooltip) {
      this.setTooltip(tooltip);
      return this;
   }

   public void initGui(GuiTable screen) {
   }

   public void setBounds(Bounds bounds) {
      this.bounds = bounds;
   }

   public Bounds bounds() {
      return this.bounds;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public FontRenderer getFontRenderer() {
      return Minecraft.getMinecraft().fontRenderer;
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      List<String> tooltip = this.getTooltip();
      if (tooltip != null) {
         screen.drawTooltipRect(tooltip, this.bounds(), mouseX, mouseY, this.getFontRenderer());
      }

   }

   public void update(GuiTable screen) {
   }

   public boolean keyTyped(char keyChar, int keyCode) {
      return false;
   }

   public void mouseClicked(int button, int x, int y) {
   }

   public void buttonClicked(int buttonID) {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
