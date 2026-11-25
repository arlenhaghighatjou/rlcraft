package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceTransformer extends TableDataSourceSegmented {
   public Transformer transformer;
   public TableDelegate delegate;

   public TableDataSourceTransformer(Transformer transformer, TableDelegate delegate, TableNavigator navigator) {
      this.transformer = transformer;
      this.delegate = delegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString idCell = new TableCellString("transformerID", transformer.id());
         idCell.setShowsValidityState(true);
         idCell.setValidityState(this.currentIDState());
         idCell.addListener((val) -> {
            transformer.setID(val);
            idCell.setValidityState(this.currentIDState());
         });
         TableCellButton randomizeCell = new TableCellButton((String)null, (String)null, IvTranslations.get("reccomplex.gui.randomize.short"), IvTranslations.getLines("reccomplex.gui.randomize"));
         randomizeCell.addAction(() -> {
            transformer.setID(Transformer.randomID(transformer.getClass()));
            delegate.reloadData();
         });
         TableCellMulti cell = new TableCellMulti(new TableCell[]{idCell, randomizeCell});
         cell.setSize(1, 0.1F);
         return (new TitledCell(IvTranslations.get("reccomplex.transformer.id"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.transformer.id.tooltip", new Object[0]));
      }});
   }

   protected GuiValidityStateIndicator.State currentIDState() {
      return Structures.isSimpleIDState(this.transformer.id());
   }

   @Nonnull
   public String title() {
      return this.transformer.getDisplayString();
   }
}
