package ivorius.reccomplex.gui.loot;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.cell.TableCellIntegerRange;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.item.ItemLootGenMultiTag;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceLootGenMultiTag extends TableDataSourceItem<ItemLootGenMultiTag> {
   public TableDataSourceLootGenMultiTag() {
      this.addSegment(0, new Supplier[]{() -> {
         TableCellIntegerRange cell = new TableCellIntegerRange("itemCount", ((ItemLootGenMultiTag)this.item).getGenerationCount(this.stack), 0, 64);
         cell.addListener((range) -> {
            ((ItemLootGenMultiTag)this.item).setGenerationCount(this.stack, range);
         });
         return new TitledCell(IvTranslations.get("reccomplex.gui.inventorygen.multi.count"), cell);
      }});
   }
}
