package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourcePlacerFactorList extends TableDataSourceList<GenericPlacer.Factor, List<GenericPlacer.Factor>> {
   public TableDataSourcePlacerFactorList(List<GenericPlacer.Factor> list, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
      this.setUsesPresetActionForAdding(true);
   }

   public String getDisplayString(GenericPlacer.Factor entry) {
      return entry.displayString();
   }

   public GenericPlacer.Factor newEntry(String actionID) {
      return (GenericPlacer.Factor)this.tryInstantiate(actionID, FactorRegistry.INSTANCE.getTypeRegistry().typeForID(actionID), "Failed instantiating placer factor: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, GenericPlacer.Factor factor) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return factor.tableDataSource(this.navigator, this.tableDelegate);
      });
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(FactorRegistry.INSTANCE.getTypeRegistry().allIDs(), "reccomplex.placer.factors.", this.canEditList());
   }

   @Nonnull
   public String title() {
      return "Placer Factors";
   }
}
