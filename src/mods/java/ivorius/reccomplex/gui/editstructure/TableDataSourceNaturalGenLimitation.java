package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceNaturalGenLimitation extends TableDataSourceSegmented {
   private NaturalGeneration.SpawnLimitation limitation;
   private TableDelegate tableDelegate;

   public TableDataSourceNaturalGenLimitation(NaturalGeneration.SpawnLimitation limitation, TableDelegate tableDelegate) {
      this.limitation = limitation;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellEnum<NaturalGeneration.SpawnLimitation.Context> cell = new TableCellEnum<NaturalGeneration.SpawnLimitation.Context>("context", limitation.context, TableCellEnum.options(NaturalGeneration.SpawnLimitation.Context.values(), "reccomplex.generationInfo.natural.limitation.context.", false));
         cell.addListener((val) -> {
            limitation.context = val;
            tableDelegate.reloadData();
         });
         return new TitledCell("Context", cell);
      }, () -> {
         TableCellIntTextField cell = new TableCellIntTextField("max", limitation.maxCount, (i) -> {
            return i > 0;
         });
         cell.addListener((val) -> {
            limitation.maxCount = val.intValue();
         });
         return new TitledCell("Max Occurrences", cell);
      }});
   }
}
