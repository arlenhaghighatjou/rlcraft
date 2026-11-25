package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellPropertyDefault;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockPos extends TableDataSourceSegmented {
   private BlockPos coord;
   private Consumer<BlockPos> consumer;
   private String title;
   private List<String> tooltip;
   protected BooleanSupplier enabled;

   public TableDataSourceBlockPos(BlockPos coord, Consumer<BlockPos> consumer, String title) {
      this.enabled = () -> {
         return (void)true;
      };
      this.coord = coord;
      this.consumer = consumer;
      this.title = title;
      this.addSegment(0, new Supplier[]{() -> {
         boolean enabled = this.enabled.getAsBoolean();
         TableCellPropertyDefault<Integer> x = new TableCellIntTextField((String)null, this.coord.getX());
         x.setTooltip(IvTranslations.getLines("reccomplex.gui.blockpos.x"));
         x.addListener((i) -> {
            this.coord = new BlockPos(i.intValue(), this.coord.getY(), this.coord.getZ());
            consumer.accept(this.coord);
         });
         x.setEnabled(enabled);
         TableCellPropertyDefault<Integer> y = new TableCellIntTextField((String)null, this.coord.getY());
         y.setTooltip(IvTranslations.getLines("reccomplex.gui.blockpos.y"));
         y.addListener((i) -> {
            this.coord = new BlockPos(this.coord.getX(), i.intValue(), this.coord.getZ());
            consumer.accept(this.coord);
         });
         y.setEnabled(enabled);
         TableCellPropertyDefault<Integer> z = new TableCellIntTextField((String)null, this.coord.getZ());
         z.setTooltip(IvTranslations.getLines("reccomplex.gui.blockpos.z"));
         z.addListener((i) -> {
            this.coord = new BlockPos(this.coord.getX(), this.coord.getY(), i.intValue());
            consumer.accept(this.coord);
         });
         z.setEnabled(enabled);
         return (void)(new TitledCell(title, new TableCellMulti(new TableCell[]{x, y, z}))).withTitleTooltip(this.tooltip);
      }});
   }

   public TableDataSourceBlockPos(BlockPos coord, Consumer<BlockPos> consumer, String title, List<String> tooltip) {
      this(coord, consumer, title);
      this.setTooltip(tooltip);
   }

   public TableDataSourceBlockPos(BlockPos coord, Consumer<BlockPos> consumer) {
      this(coord, consumer, IvTranslations.get("reccomplex.gui.blockpos"), IvTranslations.getLines("reccomplex.gui.blockpos.tooltip"));
   }

   public List<String> getTooltip() {
      return this.tooltip;
   }

   public void setTooltip(List<String> tooltip) {
      this.tooltip = tooltip;
   }

   public void setEnabled(BooleanSupplier enabled) {
      this.enabled = enabled;
   }
}
