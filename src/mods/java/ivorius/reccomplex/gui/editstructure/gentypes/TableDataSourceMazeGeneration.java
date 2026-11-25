package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceMazeComponent;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private MazeGeneration generationInfo;

   public TableDataSourceMazeGeneration(TableNavigator navigator, MazeVisualizationContext visualizationContext, TableDelegate tableDelegate, MazeGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = tableDelegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, tableDelegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellString cell = new TableCellString("mazeID", generationInfo.mazeID);
         cell.setValidityState(MazeGeneration.idValidity((String)cell.getPropertyValue()));
         cell.addListener((mazeID) -> {
            generationInfo.setMazeID(mazeID);
            cell.setValidityState(MazeGeneration.idValidity((String)cell.getPropertyValue()));
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.mazeComponent.mazeid"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.weight = TableCells.toDouble(val);
         }, generationInfo.weight);
      }});
      this.addSegment(3, (new TableDataSourceMazeComponent(generationInfo.mazeComponent, navigator, tableDelegate)).visualizing(visualizationContext));
   }
}
