package ivorius.reccomplex.gui.table.cell;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellMulti extends TableCellDefault {
   public static final int CELL_MIN_WIDTH = 17;
   @Nonnull
   protected final List<TableCell> cells;
   protected TIntFloatMap cellsSize;

   public TableCellMulti(String id, @Nonnull List<? extends TableCell> cells) {
      super(id);
      this.cells = new ArrayList();
      this.cellsSize = new TIntFloatHashMap(10, 0.5F, -1, 1.0F);
      this.cells.addAll(cells);
   }

   public TableCellMulti(String id, @Nonnull TableCell... cells) {
      this(id, Arrays.asList(cells));
   }

   public TableCellMulti(@Nonnull List<? extends TableCell> cells) {
      this((String)null, cells);
   }

   public TableCellMulti(@Nonnull TableCell... cells) {
      this((String)null, cells);
   }

   public void setSize(int cell, float size) {
      this.cellsSize.put(cell, size);
   }

   public float getScalingSize(int cell) {
      return this.cellsSize.get(cell);
   }

   public int getScaledSize(int width, float total, int cell) {
      return (int)((float)width * (this.getScalingSize(cell) / total));
   }

   public void initGui(GuiTable screen) {
      for(TableCell cell : this.cells) {
         cell.initGui(screen);
      }

   }

   public void setBounds(Bounds bounds) {
      float total = (float)IntStream.range(0, this.cells.size()).mapToDouble(this::getScalingSize).sum();
      int spreadableWidth = bounds.getWidth();

      for(int n = 0; n < this.cells.size(); ++n) {
         for(int i = 0; i < this.cells.size(); ++i) {
            if (this.getScaledSize(spreadableWidth, total, i) < 17) {
               total -= this.getScalingSize(i);
               spreadableWidth -= 17;
            }
         }
      }

      int curPos = 0;

      for(int i = 0; i < this.cells.size(); ++i) {
         boolean isLast = i == this.cells.size() - 1;
         boolean isFirst = i == 0;
         int buttonWidth = isLast ? bounds.getWidth() - curPos : Math.max(17, this.getScaledSize(spreadableWidth, total, i));
         TableCell cell = (TableCell)this.cells.get(i);
         cell.setBounds(Bounds.fromAxes(bounds.getMinX() + curPos + (isFirst ? 0 : 1), buttonWidth - (!isLast && !isFirst ? 2 : 1), bounds.getMinY(), bounds.getHeight()));
         curPos += buttonWidth;
      }

      super.setBounds(bounds);
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      for(TableCell cell : this.cells) {
         if (!cell.isHidden()) {
            cell.draw(screen, mouseX, mouseY, partialTicks);
         }
      }

      super.draw(screen, mouseX, mouseY, partialTicks);
   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      for(TableCell cell : this.cells) {
         if (!cell.isHidden()) {
            cell.drawFloating(screen, mouseX, mouseY, partialTicks);
         }
      }

      super.drawFloating(screen, mouseX, mouseY, partialTicks);
   }

   public void update(GuiTable screen) {
      for(TableCell cell : this.cells) {
         cell.update(screen);
      }

   }

   public boolean keyTyped(char keyChar, int keyCode) {
      for(TableCell cell : this.cells) {
         if (cell.keyTyped(keyChar, keyCode)) {
            return true;
         }
      }

      return false;
   }

   public void mouseClicked(int button, int x, int y) {
      for(TableCell cell : this.cells) {
         cell.mouseClicked(button, x, y);
      }

   }

   public void buttonClicked(int buttonID) {
      for(TableCell cell : this.cells) {
         cell.buttonClicked(buttonID);
      }

   }
}
