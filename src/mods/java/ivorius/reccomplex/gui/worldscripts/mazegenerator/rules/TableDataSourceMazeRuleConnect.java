package ivorius.reccomplex.gui.worldscripts.mazegenerator.rules;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourcePreloaded;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceMazePathList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved.MazeRuleConnect;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeRuleConnect extends TableDataSourceSegmented {
   private final MazeRuleConnect rule;

   public TableDataSourceMazeRuleConnect(MazeRuleConnect rule, TableDelegate tableDelegate, TableNavigator navigator, Selection bounds) {
      this.rule = rule;
      TableCellTitle startTitle = new TableCellTitle("", IvTranslations.get("reccomplex.mazerule.connect.start"));
      this.addSegment(1, new TableDataSourcePreloaded(new TableCell[]{(new TitledCell(startTitle)).withTooltip(IvTranslations.formatLines("reccomplex.mazerule.connect.start.tooltip", new Object[0]))}));
      this.addSegment(2, new TableDataSourceMazePathList(rule.start, tableDelegate, navigator, bounds));
      TableCellTitle endTitle = new TableCellTitle("", IvTranslations.get("reccomplex.mazerule.connect.end"));
      this.addSegment(3, new TableDataSourcePreloaded(new TableCell[]{(new TitledCell(endTitle)).withTooltip(IvTranslations.formatLines("reccomplex.mazerule.connect.end.tooltip", new Object[0]))}));
      this.addSegment(4, new TableDataSourceMazePathList(rule.end, tableDelegate, navigator, bounds));
   }

   @Nonnull
   public String title() {
      return "Connect";
   }

   public int sizeOfSegment(int segment) {
      return segment == 0 ? 1 : super.sizeOfSegment(segment);
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 0) {
         TableCellBoolean preventCell = new TableCellBoolean("prevent", this.rule.preventConnection, TextFormatting.GOLD + IvTranslations.get("reccomplex.mazerule.connect.prevent"), TextFormatting.GREEN + IvTranslations.get("reccomplex.mazerule.connect.ensure"));
         preventCell.addListener((val) -> {
            this.rule.preventConnection = val.booleanValue();
         });
         return new TitledCell(preventCell);
      } else {
         return super.cellForIndexInSegment(table, index, segment);
      }
   }
}
