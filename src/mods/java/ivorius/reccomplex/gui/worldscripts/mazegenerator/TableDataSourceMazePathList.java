package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePathList extends TableDataSourceList<SavedMazePath, List<SavedMazePath>> {
   private Selection bounds;

   public TableDataSourceMazePathList(List<SavedMazePath> list, TableDelegate tableDelegate, TableNavigator navigator, Selection bounds) {
      super(list, tableDelegate, navigator);
      this.bounds = bounds;
   }

   public String getDisplayString(SavedMazePath mazePath) {
      return String.format("%s %s%s%s", Arrays.toString(mazePath.sourceRoom.getCoordinates()), TextFormatting.BLUE, TableDataSourceMazePath.directionFromPath(mazePath).toString(), TextFormatting.RESET);
   }

   public SavedMazePath newEntry(String actionID) {
      return new SavedMazePath(2, new MazeRoom(new int[this.bounds.dimensions]), false);
   }

   @Nonnull
   public TableCell entryCell(boolean enabled, SavedMazePath savedMazePath) {
      return TableCells.edit(enabled, this.navigator, this.tableDelegate, () -> {
         return new TableDataSourceMazePath(savedMazePath, this.bounds, this.tableDelegate, this.navigator);
      });
   }

   @Nonnull
   public String title() {
      return "Paths";
   }
}
