package ivorius.reccomplex.gui.editstructure.gentypes;

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
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceGenerationType extends TableDataSourceSegmented {
   public GenerationType genInfo;
   public TableDelegate delegate;

   public TableDataSourceGenerationType(GenerationType genInfo, TableNavigator navigator, TableDelegate delegate) {
      this.genInfo = genInfo;
      this.delegate = delegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString idCell = new TableCellString("genInfoID", genInfo.id());
         idCell.setShowsValidityState(true);
         idCell.setValidityState(this.currentIDState());
         idCell.addListener((val) -> {
            genInfo.setID(val);
            idCell.setValidityState(this.currentIDState());
         });
         TableCellButton randomizeCell = new TableCellButton((String)null, (String)null, IvTranslations.get("reccomplex.gui.randomize.short"), IvTranslations.getLines("reccomplex.gui.randomize"));
         randomizeCell.addAction(() -> {
            genInfo.setID(GenerationType.randomID(genInfo.getClass()));
            delegate.reloadData();
         });
         TableCellMulti cell = new TableCellMulti(new TableCell[]{idCell, randomizeCell});
         cell.setSize(1, 0.1F);
         return (new TitledCell(IvTranslations.get("reccomplex.structure.generation.id"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.structure.generation.id.tooltip", new Object[0]));
      }});
   }

   @Nonnull
   public String title() {
      return this.genInfo.displayString();
   }

   protected GuiValidityStateIndicator.State currentIDState() {
      return Structures.isSimpleIDState(this.genInfo.id());
   }
}
