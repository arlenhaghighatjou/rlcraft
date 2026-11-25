package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability.TableDataSourceMazeReachability;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeComponent;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeComponent extends TableDataSourceSegmented {
   public static final int[] DEFAULT_MAX_COMPONENT_SIZE = new int[]{100, 100, 100};
   protected SavedMazeComponent component;
   protected TableNavigator navigator;
   protected TableDelegate tableDelegate;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceMazeComponent(SavedMazeComponent component, TableNavigator navigator, TableDelegate tableDelegate) {
      this.component = component;
      this.navigator = navigator;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new TableDataSourceConnector(component.defaultConnector, IvTranslations.get("reccomplex.maze.connector.default")));
      this.addSegment(1, TableCellMultiBuilder.create(navigator, tableDelegate).addNavigation(() -> {
         return (new TableDataSourceSelection(component.rooms, DEFAULT_MAX_COMPONENT_SIZE, tableDelegate, navigator, false)).visualizing(this.visualizationContext);
      }, () -> {
         return IvTranslations.get("reccomplex.generationInfo.mazeComponent.rooms");
      }, () -> {
         return IvTranslations.getLines("reccomplex.generationInfo.mazeComponent.rooms.tooltip");
      }).addNavigation(() -> {
         return (new TableDataSourceMazePathConnectionList(component.exitPaths, tableDelegate, navigator, component.rooms)).visualizing(this.visualizationContext);
      }, () -> {
         return IvTranslations.get("reccomplex.generationInfo.mazeComponent.exits");
      }, () -> {
         return IvTranslations.getLines("reccomplex.generationInfo.mazeComponent.exits.tooltip");
      }).enabled(() -> {
         return component.rooms.size() > 0;
      }).withTitle("").buildDataSource());
      this.addSegment(2, TableCellMultiBuilder.create(navigator, tableDelegate).addNavigation(() -> {
         return new TableDataSourceMazeReachability(component.reachability, this.visualizationContext, tableDelegate, navigator, SavedMazeReachability.buildExpected(component), component.rooms);
      }, () -> {
         return IvTranslations.get("reccomplex.generationInfo.mazeComponent.reachability");
      }, () -> {
         return IvTranslations.formatLines("reccomplex.reachability.tooltip", new Object[0]);
      }).enabled(() -> {
         return component.rooms.size() > 0;
      }).withTitle("").buildDataSource());
   }

   public TableDataSourceMazeComponent visualizing(MazeVisualizationContext visualizationContext) {
      this.visualizationContext = visualizationContext;
      return this;
   }

   @Nonnull
   public String title() {
      return "Maze Component";
   }
}
