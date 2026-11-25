package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.utils.RCStrings;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TitledCell extends TableCellDefault {
   public static final int TITLE_WIDTH = 100;
   @Nullable
   protected String title;
   @Nonnull
   protected TableCell cell;
   protected List<String> titleTooltip;

   public TitledCell(@Nullable String id, @Nullable String title, @Nonnull TableCell cell) {
      super(id);
      this.title = title;
      this.cell = cell;
   }

   public TitledCell(@Nullable String title, @Nonnull TableCell cell) {
      this((String)null, title, cell);
   }

   public TitledCell(@Nonnull TableCell cell) {
      this((String)null, (String)null, cell);
   }

   @Nullable
   public String getTitle() {
      return this.title;
   }

   public void setTitle(@Nullable String title) {
      this.title = title;
   }

   @Nonnull
   public TableCell getCell() {
      return this.cell;
   }

   public void setCell(@Nonnull TableCell cell) {
      this.cell = cell;
   }

   public List<String> getTitleTooltip() {
      return this.titleTooltip;
   }

   public void setTitleTooltip(List<String> titleTooltip) {
      this.titleTooltip = titleTooltip;
   }

   public TitledCell withTitleTooltip(List<String> tooltip) {
      this.setTitleTooltip(tooltip);
      return this;
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      this.cell.initGui(screen);
   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      this.cell.setBounds(Bounds.fromSize(bounds.getMinX() + 100, bounds.getMinY(), bounds.getWidth() - 100, bounds.getHeight()));
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      this.cell.setHidden(hidden);
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.draw(screen, mouseX, mouseY, partialTicks);
      Bounds bounds = this.bounds();
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      String title = this.getTitle();
      if (title != null) {
         screen.drawString(fontRenderer, RCStrings.shorten(title, fontRenderer, 95), bounds.getMinX(), bounds.getCenterY() - 4, -1);
      }

      this.cell.draw(screen, mouseX, mouseY, partialTicks);
   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.drawFloating(screen, mouseX, mouseY, partialTicks);
      this.cell.drawFloating(screen, mouseX, mouseY, partialTicks);
      Bounds bounds = this.bounds();
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      String title = this.getTitle();
      if (title != null) {
         int stringWidth = Math.max(fontRenderer.getStringWidth(title), 100);
         List<String> tooltip = this.getTitleTooltip();
         if (tooltip != null) {
            screen.drawTooltipRect(tooltip, Bounds.fromSize(bounds.getMinX() + 100 - stringWidth - 10, bounds.getCenterY() - 6, stringWidth, 12), mouseX, mouseY, fontRenderer);
         }
      }

   }

   public void update(GuiTable screen) {
      super.update(screen);
      this.cell.update(screen);
   }

   public boolean keyTyped(char keyChar, int keyCode) {
      return this.cell.keyTyped(keyChar, keyCode) || super.keyTyped(keyChar, keyCode);
   }

   public void mouseClicked(int button, int x, int y) {
      super.mouseClicked(button, x, y);
      this.cell.mouseClicked(button, x, y);
   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);
      this.cell.buttonClicked(buttonID);
   }
}
