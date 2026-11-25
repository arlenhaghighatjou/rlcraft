package ivorius.reccomplex.gui.table.cell;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellMultiBuilder {
   protected final List<Supplier<TableCellDefault>> cells = new ArrayList();
   protected final List<BooleanSupplier> enabledSuppliers = new ArrayList();
   protected final TIntObjectMap<Supplier<Float>> sizers = new TIntObjectHashMap();
   public TableNavigator navigator;
   public TableDelegate delegate;
   public String title;
   public List<String> titleTooltip;

   private TableCellMultiBuilder(TableNavigator navigator, TableDelegate delegate) {
      this.navigator = navigator;
      this.delegate = delegate;
   }

   public static TableCellMultiBuilder create(TableNavigator navigator, TableDelegate delegate) {
      return new TableCellMultiBuilder(navigator, delegate);
   }

   @Nonnull
   private static TableCellButton defaultCell(Object title, Supplier<List<String>> tooltip, Runnable action) {
      TableCellButton cell = new TableCellButton((String)null, (String)null, "");
      setVisuals(title, tooltip, cell);
      cell.addAction(action);
      return cell;
   }

   private static void setVisuals(Object title, Supplier<List<String>> tooltip, TableCellButton cell) {
      cell.setTitle("");
      cell.setTexture((ResourceLocation)null);
      if (title instanceof ResourceLocation) {
         cell.setTexture((ResourceLocation)title);
      } else if (title instanceof String) {
         cell.setTitle((String)title);
      }

      cell.setTooltip(tooltip != null ? (List)tooltip.get() : null);
   }

   @Nonnull
   public TableCellMultiBuilder addCell(Supplier<TableCellDefault> cell) {
      this.cells.add(cell);
      this.enabledSuppliers.add((Object)null);
      return this;
   }

   public TableCellMultiBuilder addAction(Runnable action, Supplier<Object> title, @Nullable Supplier<List<String>> tooltip) {
      return this.addCell(() -> {
         return defaultCell(title.get(), tooltip, () -> {
            action.run();
            this.delegate.reloadData();
         });
      });
   }

   public TableCellMultiBuilder addSimpleNavigation(Supplier<TableDataSource> dataSource, Supplier<Object> title, @Nullable Supplier<List<String>> tooltip) {
      return this.addCell(() -> {
         return defaultCell(title.get(), tooltip, () -> {
            this.navigator.pushTable(new GuiTable(this.delegate, (TableDataSource)dataSource.get()));
         });
      });
   }

   public TableCellMultiBuilder addNavigation(Supplier<TableDataSource> dataSource, Supplier<Object> title, @Nullable Supplier<List<String>> tooltip) {
      return this.addCell(() -> {
         TableCellButton edit = TableCells.edit(true, this.navigator, this.delegate, dataSource);
         setVisuals(title.get(), tooltip, edit);
         return edit;
      });
   }

   public TableCellMultiBuilder addNavigation(Supplier<TableDataSource> dataSource, Supplier<Object> title) {
      return this.addNavigation(dataSource, title, (Supplier)null);
   }

   public TableCellMultiBuilder addNavigation(Supplier<TableDataSource> dataSource) {
      return this.addNavigation(dataSource, () -> {
         return IvTranslations.get("reccomplex.gui.edit");
      });
   }

   public TableCellMultiBuilder enabled(BooleanSupplier enabled) {
      this.enabledSuppliers.remove(this.enabledSuppliers.size() - 1);
      this.enabledSuppliers.add(enabled);
      return this;
   }

   public TableCellMultiBuilder sized(Supplier<Float> sizer) {
      this.sizers.put(this.cells.size() - 1, sizer);
      return this;
   }

   @Nonnull
   public TableDataSource buildDataSource() {
      return new TableDataSourceSupplied(new Supplier[]{this::build});
   }

   @Nonnull
   public TableCellMultiBuilder withTitle(@Nullable String title) {
      this.title = title;
      return this;
   }

   @Nonnull
   public TableCellMultiBuilder withTitle(@Nullable String title, List<String> tooltip) {
      this.title = title;
      this.titleTooltip = tooltip;
      return this;
   }

   @Nonnull
   public TableCellDefault build() {
      List<TableCellDefault> cells = new ArrayList();

      for(int i = 0; i < this.cells.size(); ++i) {
         TableCellDefault cell = (TableCellDefault)((Supplier)this.cells.get(i)).get();
         BooleanSupplier enabled = (BooleanSupplier)this.enabledSuppliers.get(i);
         if (enabled != null) {
            cell.setEnabled(enabled.getAsBoolean());
         }

         cells.add(cell);
      }

      TableCellMulti multi = new TableCellMulti(cells);
      this.sizers.forEachEntry((idx, supplier) -> {
         multi.setSize(idx, ((Float)supplier.get()).floatValue());
         return true;
      });
      return (TableCellDefault)(this.title != null ? (new TitledCell(this.title, multi)).withTitleTooltip(this.titleTooltip) : multi);
   }
}
