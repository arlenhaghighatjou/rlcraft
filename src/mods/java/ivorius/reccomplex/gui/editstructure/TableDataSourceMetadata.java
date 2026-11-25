package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.Metadata;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMetadata extends TableDataSourceSegmented {
   protected Metadata metadata;

   public TableDataSourceMetadata(Metadata metadata) {
      this.metadata = metadata;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString("authors", metadata.authors);
         cell.addListener((val) -> {
            metadata.authors = val;
         });
         return new TitledCell("Authors", cell);
      }, () -> {
         TableCellString cell = new TableCellString("weblink", metadata.weblink);
         cell.addListener((val) -> {
            metadata.weblink = val;
         });
         return new TitledCell("Weblink", cell);
      }, () -> {
         TableCellString cell = new TableCellString("comment", metadata.comment);
         cell.addListener((val) -> {
            metadata.comment = val;
         });
         return new TitledCell("Comment", cell);
      }});
   }

   public Metadata getMetadata() {
      return this.metadata;
   }

   public void setMetadata(Metadata metadata) {
      this.metadata = metadata;
   }

   @Nonnull
   public String title() {
      return "Metadata";
   }
}
