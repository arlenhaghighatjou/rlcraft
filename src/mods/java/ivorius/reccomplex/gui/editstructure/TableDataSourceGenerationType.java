package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceGenerationType extends TableDataSourceList<GenerationType, List<GenerationType>> {
   protected MazeVisualizationContext realWorldMapper;

   public TableDataSourceGenerationType(List<GenerationType> list, MazeVisualizationContext realWorldMapper, TableDelegate tableDelegate, TableNavigator navigator) {
      super(list, tableDelegate, navigator);
      this.setUsesPresetActionForAdding(true);
      this.realWorldMapper = realWorldMapper;
   }

   public List<TableCellButton> getAddActions() {
      return TableCells.addManyWithBase(StructureRegistry.GENERATION_TYPES.allIDs(), "reccomplex.generationInfo.", this.canEditList());
   }

   public String getDisplayString(GenerationType generationType) {
      return generationType.displayString();
   }

   public List<String> getTooltip(GenerationType generationType) {
      return TableCells.getTooltip("reccomplex.generationInfo.", StructureRegistry.GENERATION_TYPES.iDForType(generationType.getClass()));
   }

   public GenerationType newEntry(String actionID) {
      return (GenerationType)this.tryInstantiate(actionID, StructureRegistry.GENERATION_TYPES.typeForID(actionID), "Failed instantiating generation type: %s");
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, GenerationType generationType) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return generationType.tableDataSource(this.realWorldMapper, this.navigator, this.tableDelegate);
      });
   }

   @Nonnull
   public String title() {
      return "Generation Types";
   }
}
