package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericVariableDomain;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceGenericVariableDomain extends TableDataSourceList<GenericVariableDomain.Variable, List<GenericVariableDomain.Variable>> {
   public GenericVariableDomain domain;

   public TableDataSourceGenericVariableDomain(TableDelegate tableDelegate, TableNavigator navigator, GenericVariableDomain domain) {
      super(domain.variables(), tableDelegate, navigator);
      this.domain = domain;
      this.duplicateTitle = TextFormatting.GREEN + "D";
   }

   @Nonnull
   public String title() {
      return "Local Toggles";
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, GenericVariableDomain.Variable variable) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceGenericVariable(variable);
      });
   }

   public String getDisplayString(GenericVariableDomain.Variable variable) {
      return variable.id;
   }

   public GenericVariableDomain.Variable newEntry(String actionID) {
      GenericVariableDomain.Variable variable = new GenericVariableDomain.Variable();
      variable.id = String.format("var_%d", (new Random()).nextInt(100));
      return variable;
   }

   public GenericVariableDomain.Variable copyEntry(GenericVariableDomain.Variable variable) {
      return variable.copy();
   }
}
