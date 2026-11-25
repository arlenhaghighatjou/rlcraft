package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellPropertyDefault;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockSurfacePos extends TableDataSourceSegmented {
   private BlockSurfacePos coord;
   private Consumer<BlockSurfacePos> consumer;
   private String title;
   private List<String> tooltip;

   public TableDataSourceBlockSurfacePos(BlockSurfacePos coord, Consumer<BlockSurfacePos> consumer, String title) {
      this.coord = coord;
      this.consumer = consumer;
      this.title = title;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellPropertyDefault<Integer> x = new TableCellIntTextField((String)null, this.coord.getX());
         x.setTooltip(IvTranslations.getLines("reccomplex.gui.surfacepos.x"));
         x.addListener((i) -> {
            this.coord = new BlockSurfacePos(i.intValue(), this.coord.getZ());
            consumer.accept(this.coord);
         });
         TableCellPropertyDefault<Integer> z = new TableCellIntTextField((String)null, this.coord.getZ());
         z.setTooltip(IvTranslations.getLines("reccomplex.gui.surfacepos.z"));
         z.addListener((i) -> {
            this.coord = new BlockSurfacePos(this.coord.getX(), i.intValue());
            consumer.accept(this.coord);
         });
         return (void)(new TitledCell(this.title, new TableCellMulti(new TableCell[]{x, z}))).withTitleTooltip(this.tooltip);
      }});
   }

   public TableDataSourceBlockSurfacePos(BlockSurfacePos coord, Consumer<BlockSurfacePos> consumer, String title, List<String> tooltip) {
      this(coord, consumer, title);
      this.setTooltip(tooltip);
   }

   public TableDataSourceBlockSurfacePos(BlockSurfacePos coord, Consumer<BlockSurfacePos> consumer) {
      this(coord, consumer, IvTranslations.get("reccomplex.gui.surfacepos"), IvTranslations.getLines("reccomplex.gui.surfacepos.tooltip"));
   }

   public List<String> getTooltip() {
      return this.tooltip;
   }

   public void setTooltip(List<String> tooltip) {
      this.tooltip = tooltip;
   }
}
