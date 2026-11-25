package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.TableDataSourceWorldScript;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.rules.TableDataSourceMazeRuleList;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptMazeGenerator extends TableDataSourceSegmented {
   private WorldScriptMazeGenerator script;
   protected TableDelegate delegate;
   protected TableNavigator navigator;

   public TableDataSourceWorldScriptMazeGenerator(WorldScriptMazeGenerator script, BlockPos realWorldPos, TableDelegate delegate, TableNavigator navigator) {
      this.script = script;
      this.delegate = delegate;
      this.navigator = navigator;
      this.addSegment(0, new TableDataSourceWorldScript(script));
      this.addSegment(1, new Supplier[]{() -> {
         return TableCellMultiBuilder.create(navigator, delegate).addCell(() -> {
            TableCellString cell = new TableCellString("mazeID", script.getMazeID());
            cell.setShowsValidityState(true);
            cell.setValidityState(MazeGeneration.idValidity((String)cell.getPropertyValue()));
            cell.addListener((mazeID) -> {
               script.setMazeID(mazeID);
               cell.setValidityState(MazeGeneration.idValidity(mazeID));
            });
            return cell;
         }).addNavigation(() -> {
            return new TableDataSourceMazeInfo(script, delegate, navigator);
         }).withTitle(IvTranslations.get("reccomplex.maze.id")).build();
      }});
      this.addSegment(2, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return (new TableDataSourceMazeComponent(script.mazeComponent, navigator, delegate)).visualizing(new MazeVisualizationContext(script.structureShift.add(realWorldPos), script.roomSize));
      }, () -> {
         return IvTranslations.get("reccomplex.maze");
      }).withTitle("").buildDataSource());
      this.addSegment(3, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceMazeRuleList(script.rules, delegate, navigator, script.mazeComponent.exitPaths, script.mazeComponent.rooms);
      }, () -> {
         return IvTranslations.get("reccomplex.worldscript.mazeGen.rules");
      }).withTitle("").buildDataSource());
      this.addSegment(4, new TableDataSourceBlockPos(script.getStructureShift(), script::setStructureShift, IvTranslations.get("reccomplex.gui.blockpos.shift"), IvTranslations.getLines("reccomplex.gui.blockpos.shift.tooltip")));
   }

   public WorldScriptMazeGenerator getScript() {
      return this.script;
   }

   public void setScript(WorldScriptMazeGenerator script) {
      this.script = script;
   }

   public TableDelegate getTableDelegate() {
      return this.delegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.delegate = tableDelegate;
   }

   public TableNavigator getNavigator() {
      return this.navigator;
   }

   public void setNavigator(TableNavigator navigator) {
      this.navigator = navigator;
   }
}
