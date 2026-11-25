package ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeReachabilityGrouping extends TableDataSourceSegmented {
   private SavedMazeReachability reachability;

   public TableDataSourceMazeReachabilityGrouping(SavedMazeReachability reachability, Set<SavedMazePath> expected, TableDelegate tableDelegate, TableNavigator tableNavigator) {
      this.reachability = reachability;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean((String)null, reachability.groupByDefault, "Group", "Don't Group");
         cell.addListener((b) -> {
            reachability.groupByDefault = b.booleanValue();
         });
         return (void)(new TitledCell(IvTranslations.get("reccomplex.reachability.groups.default.behavior"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.reachability.groups.default.behavior.tooltip"));
      }});
      this.addSegment(1, new TableDataSourceMazeReachabilityGroups(reachability, expected, tableDelegate, tableNavigator));
   }

   public SavedMazeReachability getReachability() {
      return this.reachability;
   }

   public void setReachability(SavedMazeReachability reachability) {
      this.reachability = reachability;
   }
}
