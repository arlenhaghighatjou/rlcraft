package ivorius.reccomplex.gui.worldscripts.command;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.TableDataSourceWorldScript;
import ivorius.reccomplex.world.gen.script.WorldScriptCommand;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptCommand extends TableDataSourceSegmented {
   public TableDataSourceWorldScriptCommand(WorldScriptCommand script, TableDelegate tableDelegate, TableNavigator navigator) {
      this.addSegment(0, new TableDataSourceWorldScript(script));
      this.addSegment(1, new TableDataSourceList<WorldScriptCommand.Entry, List<WorldScriptCommand.Entry>>(script.entries, tableDelegate, navigator) {
         public String getDisplayString(WorldScriptCommand.Entry entry) {
            return entry.command;
         }

         public WorldScriptCommand.Entry newEntry(String actionID) {
            return new WorldScriptCommand.Entry(1.0D, "");
         }

         @Nonnull
         public TableCell entryCell(boolean enabled, WorldScriptCommand.Entry entry) {
            return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
               return new TableDataSourceSpawnCommandEntry(entry, this.tableDelegate);
            });
         }
      });
   }
}
