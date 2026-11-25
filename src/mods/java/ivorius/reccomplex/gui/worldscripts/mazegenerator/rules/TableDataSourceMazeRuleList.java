package ivorius.reccomplex.gui.worldscripts.mazegenerator.rules;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRule;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRuleRegistry;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeRuleList extends TableDataSourceList<MazeRule, List<MazeRule>> {
   private List<SavedMazePathConnection> expected;
   private Selection bounds;

   public TableDataSourceMazeRuleList(List<MazeRule> list, TableDelegate tableDelegate, TableNavigator navigator, List<SavedMazePathConnection> expected, Selection bounds) {
      super(list, tableDelegate, navigator);
      this.expected = expected;
      this.bounds = bounds;
      this.setUsesPresetActionForAdding(true);
   }

   public String getDisplayString(MazeRule mazeRule) {
      return mazeRule.displayString();
   }

   public MazeRule newEntry(String actionID) {
      return (MazeRule)this.tryInstantiate(actionID, MazeRuleRegistry.INSTANCE.objectClass(actionID), "Failed instantiating maze rule: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, MazeRule mazeRule) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return mazeRule.tableDataSource(this.navigator, this.tableDelegate, this.expected, this.bounds);
      });
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(MazeRuleRegistry.INSTANCE.allIDs(), "reccomplex.mazerule.", this.canEditList());
   }

   @Nonnull
   public String title() {
      return "Rules";
   }
}
