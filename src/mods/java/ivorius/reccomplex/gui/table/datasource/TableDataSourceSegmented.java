package ivorius.reccomplex.gui.table.datasource;

import com.google.common.primitives.Ints;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSegmented implements TableDataSource {
   protected final TIntObjectMap<TableDataSource> managedSegments;

   public TableDataSourceSegmented() {
      this.managedSegments = new TIntObjectHashMap();
   }

   public TableDataSourceSegmented(List<TableDataSource> segments) {
      this.managedSegments = new TIntObjectHashMap();

      for(int i = 0; i < segments.size(); ++i) {
         this.managedSegments.put(i, segments.get(i));
      }

   }

   public TableDataSourceSegmented(TableDataSource... segments) {
      this(Arrays.asList(segments));
   }

   public void addSegment(int segment, TableDataSource source) {
      this.managedSegments.put(segment, source);
   }

   @SafeVarargs
   public final void addSegment(int segment, Supplier<TableCell>... suppliers) {
      this.managedSegments.put(segment, new TableDataSourceSupplied(suppliers));
   }

   public void removeManagedSection(int section) {
      this.managedSegments.remove(section);
   }

   public TIntSet managedSections() {
      return this.managedSegments.keySet();
   }

   @Nonnull
   public String title() {
      for(int i : this.managedSegments.keys()) {
         String title = ((TableDataSource)this.managedSegments.get(i)).title();
         if (!title.trim().isEmpty()) {
            return title;
         }
      }

      return "";
   }

   public int numberOfCells() {
      int cells = 0;
      int segments = this.numberOfSegments();

      for(int i = 0; i < segments; ++i) {
         cells += this.sizeOfSegment(i);
      }

      return cells;
   }

   public TableCell cellForIndex(GuiTable table, int index) {
      for(int seg = 0; seg < this.numberOfSegments(); ++seg) {
         if (index - this.sizeOfSegment(seg) < 0) {
            return this.cellForIndexInSegment(table, index, seg);
         }

         index -= this.sizeOfSegment(seg);
      }

      return null;
   }

   public int numberOfSegments() {
      return this.managedSegments.isEmpty() ? 0 : Ints.max(this.managedSegments.keys()) + 1;
   }

   public int sizeOfSegment(int segment) {
      TableDataSource managed = (TableDataSource)this.managedSegments.get(segment);
      return managed != null ? managed.numberOfCells() : 0;
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      TableDataSource managed = (TableDataSource)this.managedSegments.get(segment);
      return managed != null ? managed.cellForIndex(table, index) : null;
   }

   public boolean canVisualize() {
      return this.managedSegments.valueCollection().stream().filter((s) -> {
         return s.canVisualize();
      }).count() == 1L;
   }

   public GuiHider.Visualizer visualizer() {
      return ((TableDataSource)this.managedSegments.valueCollection().stream().filter((s) -> {
         return s.canVisualize();
      }).findFirst().orElseThrow(InternalError::new)).visualizer();
   }
}
