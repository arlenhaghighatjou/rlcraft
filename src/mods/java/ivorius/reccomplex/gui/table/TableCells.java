package ivorius.reccomplex.gui.table;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellPresetAction;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCells {
   public TableCells() {
   }

   public static Float toFloat(Double value) {
      return value != null ? (float)value.doubleValue() : null;
   }

   public static Double toDouble(Float value) {
      return value != null ? (double)value.floatValue() : null;
   }

   public static void reloadExcept(TableDelegate delegate, String... cellIDs) {
      for(String cell : cellIDs) {
         delegate.setLocked(cell, true);
      }

      delegate.reloadData();

      for(String cell : cellIDs) {
         delegate.setLocked(cell, false);
      }

   }

   @Nonnull
   public static TableCellButton edit(boolean enabled, TableNavigator navigator, TableDelegate tableDelegate, Supplier<TableDataSource> table) {
      TableCellButton edit = new TableCellButton((String)null, "edit", IvTranslations.get("reccomplex.gui.edit"), enabled) {
         public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
            super.draw(screen, mouseX, mouseY, partialTicks);
            int color = 14737632;
            if (!this.enabled) {
               color = 10526880;
            } else if (this.button != null && this.button.isMouseOver()) {
               color = 16777120;
            }

            String right = "...";
            int rightWidth = this.getFontRenderer().getStringWidth(right);
            this.getFontRenderer().drawString(right, (float)(this.bounds().getMaxX() - 8 - rightWidth), (float)(this.bounds().getCenterY() - 4), color, true);
         }
      };
      edit.addAction(() -> {
         navigator.pushTable(new GuiTable(tableDelegate, (TableDataSource)table.get()));
      });
      return edit;
   }

   @Nonnull
   public static List<TableCellButton> addManyWithBase(Collection<String> ids, String baseKey, boolean enabled) {
      return (List)TableCellPresetAction.sorted(ids.stream().map((type) -> {
         return add(enabled, type, IvTranslations.get(baseKey + type), getTooltip(baseKey, type));
      })).collect(Collectors.toList());
   }

   @Nonnull
   public static List<String> getTooltip(String baseKey, String type) {
      return IvTranslations.formatLines(baseKey + type + ".tooltip", new Object[0]);
   }

   @Nonnull
   public static TableCellButton add(boolean enabled, String id, String title, List<String> tooltip) {
      return new TableCellButton(id, id, title, tooltip, enabled) {
         public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
            super.draw(screen, mouseX, mouseY, partialTicks);
            int color = 3462470;
            if (!this.enabled) {
               color = 8952462;
            } else if (this.button != null && this.button.isMouseOver()) {
               color = 3928912;
            }

            String plus = "+";
            int plusWidth = this.getFontRenderer().getStringWidth(plus);
            this.getFontRenderer().drawString(plus, (float)(this.bounds().getMinX() + 6), (float)(this.bounds().getCenterY() - 4), color, true);
            this.getFontRenderer().drawString(plus, (float)(this.bounds().getMaxX() - 6 - plusWidth), (float)(this.bounds().getCenterY() - 4), color, true);
         }
      };
   }
}
