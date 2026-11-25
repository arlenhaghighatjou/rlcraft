package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.editstructure.pattern.TableDataSourceBlockPattern;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.SaplingGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSaplingGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private SaplingGeneration generationInfo;

   public TableDataSourceSaplingGeneration(SaplingGeneration generationInfo, MazeVisualizationContext visualizationContext, TableNavigator navigator, TableDelegate tableDelegate) {
      this.navigator = navigator;
      this.tableDelegate = tableDelegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, tableDelegate));
      this.addSegment(1, new TableDataSourceSupplied(new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.generationWeight = TableCells.toDouble(val);
         }, generationInfo.generationWeight);
      }}));
      this.addSegment(2, new TableDataSourceBlockPattern(generationInfo.pattern, visualizationContext, tableDelegate, navigator));
      this.addSegment(3, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.environment"), generationInfo.environmentExpression, (Object)null));
      this.addSegment(4, new TableDataSourceBlockPos(generationInfo.spawnShift, generationInfo::setSpawnShift, IvTranslations.get("reccomplex.gui.blockpos.shift"), IvTranslations.getLines("reccomplex.gui.blockpos.shift.tooltip")));
   }
}
