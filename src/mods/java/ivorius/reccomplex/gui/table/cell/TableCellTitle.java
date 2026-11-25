package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellTitle extends TableCellDefault {
   protected String displayString;
   @Nonnull
   protected TableCellTitle.Positioning positioning = TableCellTitle.Positioning.CENTER;

   public TableCellTitle(String id, String displayString) {
      super(id);
      this.displayString = displayString;
   }

   public String getDisplayString() {
      return this.displayString;
   }

   public void setDisplayString(String displayString) {
      this.displayString = displayString;
   }

   @Nonnull
   public TableCellTitle.Positioning getPositioning() {
      return this.positioning;
   }

   public void setPositioning(@Nonnull TableCellTitle.Positioning positioning) {
      this.positioning = positioning;
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.draw(screen, mouseX, mouseY, partialTicks);
      Bounds bounds = this.bounds();
      FontRenderer fontRenderer = this.getFontRenderer();
      screen.drawCenteredString(fontRenderer, this.displayString, bounds.getCenterX(), this.positioning.getY(fontRenderer, bounds.getMinY(), bounds.getMaxY()), -1);
   }

   public static enum Positioning {
      BOTTOM,
      CENTER,
      TOP;

      private Positioning() {
      }

      public int getY(FontRenderer renderer, int min, int max) {
         switch(this) {
         case TOP:
            return min + 3;
         case CENTER:
            return (min + max - renderer.FONT_HEIGHT) / 2;
         case BOTTOM:
            return max - 2 - renderer.FONT_HEIGHT;
         default:
            throw new InternalError();
         }
      }
   }
}
