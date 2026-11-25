package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorLimit;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceLimitRayList extends TableDataSourceList<FactorLimit.Ray, List<FactorLimit.Ray>> {
   public TableDataSourceLimitRayList(List<FactorLimit.Ray> list, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
      this.setUsesPresetActionForAdding(true);
   }

   public String getDisplayString(FactorLimit.Ray entry) {
      return entry.displayString();
   }

   public FactorLimit.Ray newEntry(String actionID) {
      return (FactorLimit.Ray)this.tryInstantiate(actionID, FactorLimit.getRayRegistry().typeForID(actionID), "Failed instantiating limit factor ray: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, FactorLimit.Ray ray) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return ray.tableDataSource(this.navigator, this.tableDelegate);
      });
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(FactorLimit.getRayRegistry().allIDs(), "reccomplex.placer.factors.limit.rays.", this.canEditList());
   }

   @Nonnull
   public String title() {
      return "Rays";
   }
}
