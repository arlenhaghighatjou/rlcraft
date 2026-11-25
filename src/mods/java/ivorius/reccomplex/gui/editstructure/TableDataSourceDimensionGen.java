package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedDimensionMatcher;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceDimensionGen extends TableDataSourceSegmented {
   private WeightedDimensionMatcher generationInfo;
   private TableDelegate tableDelegate;

   public TableDataSourceDimensionGen(WeightedDimensionMatcher generationInfo, TableDelegate tableDelegate) {
      this.generationInfo = generationInfo;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.dimensions"), generationInfo.getDimensionExpression(), (Object)null));
      this.addSegment(1, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.setGenerationWeight(TableCells.toDouble(val));
         }, generationInfo.getGenerationWeight());
      }});
   }

   @Nonnull
   public String title() {
      return "Dimension";
   }
}
