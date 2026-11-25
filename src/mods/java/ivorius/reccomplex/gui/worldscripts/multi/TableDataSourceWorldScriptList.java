package ivorius.reccomplex.gui.worldscripts.multi;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.script.WorldScript;
import ivorius.reccomplex.world.gen.script.WorldScriptRegistry;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptList extends TableDataSourceList<WorldScript, List<WorldScript>> {
   protected BlockPos realWorldPos;

   public TableDataSourceWorldScriptList(List<WorldScript> list, BlockPos realWorldPos, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
      this.realWorldPos = realWorldPos;
      this.setUsesPresetActionForAdding(true);
      this.duplicateTitle = TextFormatting.GREEN + "D";
   }

   public String getDisplayString(WorldScript script) {
      return script.getDisplayString();
   }

   public WorldScript newEntry(String actionID) {
      return (WorldScript)this.tryInstantiate(actionID, WorldScriptRegistry.INSTANCE.objectClass(actionID), "Failed instantiating world script: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, WorldScript worldScript) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return worldScript.tableDataSource(this.realWorldPos, this.navigator, this.tableDelegate);
      });
   }

   public WorldScript copyEntry(WorldScript worldScript) {
      return (WorldScript)WorldScriptRegistry.INSTANCE.copy(worldScript);
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(WorldScriptRegistry.INSTANCE.allIDs(), "reccomplex.worldscript.", this.canEditList());
   }

   @Nonnull
   public String title() {
      return "Scripts";
   }
}
