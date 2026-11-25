package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceTransformerList extends TableDataSourceList<Transformer, List<Transformer>> {
   public TableDataSourceTransformerList(List<Transformer> list, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
      this.setUsesPresetActionForAdding(true);
   }

   public String getDisplayString(Transformer transformer) {
      return transformer.getDisplayString();
   }

   public Transformer newEntry(String actionID) {
      Class<? extends Transformer> clazz = StructureRegistry.TRANSFORMERS.typeForID(actionID);
      return (Transformer)this.tryInstantiate(actionID, clazz, "Failed instantiating transformer: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, Transformer transformer) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return transformer.tableDataSource(this.navigator, this.tableDelegate);
      });
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(StructureRegistry.TRANSFORMERS.allIDs(), "reccomplex.transformer.", this.canEditList());
   }

   @Nonnull
   public String title() {
      return "Transformers";
   }
}
