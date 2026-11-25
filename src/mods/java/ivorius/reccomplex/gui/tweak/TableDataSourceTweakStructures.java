package ivorius.reccomplex.gui.tweak;

import gnu.trove.map.TObjectFloatMap;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceTweakStructures extends TableDataSourceSegmented {
   protected TObjectFloatMap<String> tweaks;
   protected String search = "";

   public TableDataSourceTweakStructures(TableDelegate delegate, TObjectFloatMap<String> tweaks) {
      this.tweaks = tweaks;
      TableDataSourceTweakStructuresList tdsList = new TableDataSourceTweakStructuresList(delegate, tweaks);
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, this.search);
         cell.addListener((s) -> {
            delegate.setLocked("search", true);
            this.search = s;
            tdsList.search(s);
            delegate.setLocked("search", false);
         });
         return (void)(new TitledCell("search", "Search", cell));
      }});
      this.addSegment(1, tdsList);
   }

   @Nonnull
   public String title() {
      return "Tweak Structure Spawn Rates";
   }
}
