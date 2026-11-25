package ivorius.reccomplex.gui.loot;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.item.ItemLootGenSingleTag;
import ivorius.reccomplex.utils.scale.Scales;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceLootGeneratorSingleTag extends TableDataSourceItem<ItemLootGenSingleTag> {
   public TableDataSourceLootGeneratorSingleTag() {
      this.addSegment(0, new Supplier[]{() -> {
         TableCellFloatSlider cell = new TableCellFloatSlider("itemCount", ((ItemLootGenSingleTag)this.item).getItemChance(this.stack), 0.0F, 1.0F);
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((range) -> {
            ((ItemLootGenSingleTag)this.item).setItemChance(this.stack, range.floatValue());
         });
         return new TitledCell(IvTranslations.get("reccomplex.gui.inventorygen.single.chance"), cell);
      }});
   }
}
