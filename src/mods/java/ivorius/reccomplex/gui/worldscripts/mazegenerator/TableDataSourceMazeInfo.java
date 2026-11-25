package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class TableDataSourceMazeInfo extends TableDataSourceSegmented {
   public WorldScriptMazeGenerator script;
   protected TableDelegate delegate;
   protected TableNavigator navigator;

   public TableDataSourceMazeInfo(WorldScriptMazeGenerator script, TableDelegate delegate, TableNavigator navigator) {
      this.script = script;
      this.delegate = delegate;
      this.navigator = navigator;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellIntTextField cell = new TableCellIntTextField("roomSizeX", script.getRoomSize()[0], (i) -> {
            return i > 0;
         });
         cell.addListener(this.roomSizeConsumer(0));
         return new TitledCell(IvTranslations.get("reccomplex.maze.rooms.size.x"), cell);
      }, () -> {
         TableCellIntTextField cell = new TableCellIntTextField("roomSizeY", script.getRoomSize()[1], (i) -> {
            return i > 0;
         });
         cell.addListener(this.roomSizeConsumer(1));
         return new TitledCell(IvTranslations.get("reccomplex.maze.rooms.size.y"), cell);
      }, () -> {
         TableCellIntTextField cell = new TableCellIntTextField("roomSizeZ", script.getRoomSize()[2], (i) -> {
            return i > 0;
         });
         cell.addListener(this.roomSizeConsumer(2));
         return new TitledCell(IvTranslations.get("reccomplex.maze.rooms.size.z"), cell);
      }});
   }

   @Nonnull
   public String title() {
      return "Maze";
   }

   private Consumer<Integer> roomSizeConsumer(int index) {
      return (val) -> {
         int[] size = this.script.getRoomSize();
         size[index] = val.intValue();
         this.script.setRoomSize(size);
      };
   }
}
