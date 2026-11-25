package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import com.google.common.collect.ImmutableList;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellIntSlider;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TableCellPropertyDefault;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeRoom extends TableDataSourceSegmented {
   public MazeRoom room;
   protected Consumer<MazeRoom> consumer;
   protected List<IntegerRange> bounds;
   protected final List<String> titles;
   protected final List<List<String>> tooltips;

   public TableDataSourceMazeRoom(MazeRoom room, Consumer<MazeRoom> consumer, List<IntegerRange> bounds, List<String> titles, List<List<String>> tooltips) {
      if (titles.size() != room.getDimensions()) {
         throw new IllegalArgumentException(String.format("titles: %d, dimensions: %d", titles.size(), room.getDimensions()));
      } else {
         this.room = room;
         this.consumer = consumer;
         this.bounds = bounds;
         this.titles = ImmutableList.copyOf(titles);
         this.tooltips = ImmutableList.copyOf(tooltips);
      }
   }

   @Nonnull
   public String title() {
      return "Room";
   }

   public int numberOfSegments() {
      return 1;
   }

   public int sizeOfSegment(int segment) {
      return this.room.getDimensions();
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      int val = this.room.getCoordinate(index);
      String title = (String)this.titles.get(index);
      List<String> tooltip = (List)this.tooltips.get(index);
      TableCellPropertyDefault<Integer> cell = (TableCellPropertyDefault<Integer>)(this.bounds != null ? new TableCellIntSlider((String)null, val, ((IntegerRange)this.bounds.get(index)).min, ((IntegerRange)this.bounds.get(index)).max) : new TableCellIntTextField((String)null, val, (i) -> {
         return i > 0;
      }));
      cell.addListener(this.createConsumer(index));
      return (new TitledCell(title, cell)).withTitleTooltip(tooltip);
   }

   @Nonnull
   protected Consumer<Integer> createConsumer(int idx) {
      return (val) -> {
         this.consumer.accept(this.room = this.setInDimension(this.room, idx, val.intValue()));
      };
   }

   private MazeRoom setInDimension(MazeRoom room, int dimension, int value) {
      return this.room.addInDimension(dimension, value - room.getCoordinate(dimension));
   }
}
