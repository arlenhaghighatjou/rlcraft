package ivorius.reccomplex.gui.editstructure.pattern;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.BlockPattern;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockPatternIngredientList extends TableDataSourceList<BlockPattern.Ingredient, List<BlockPattern.Ingredient>> {
   public TableDataSourceBlockPatternIngredientList(List<BlockPattern.Ingredient> list, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
   }

   public String getDisplayString(BlockPattern.Ingredient entry) {
      return entry.identifier;
   }

   public BlockPattern.Ingredient newEntry(String actionID) {
      return new BlockPattern.Ingredient();
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, BlockPattern.Ingredient ingredient) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceBlockPatternIngredient(ingredient, this.tableDelegate);
      });
   }
}
