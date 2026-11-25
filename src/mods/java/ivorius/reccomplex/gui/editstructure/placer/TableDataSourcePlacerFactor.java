package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourcePlacerFactor extends TableDataSourceSegmented {
   public GenericPlacer.Factor placer;

   public TableDataSourcePlacerFactor(GenericPlacer.Factor factor, TableDelegate delegate, TableNavigator navigator) {
      this.placer = factor;
      this.addSegment(0, new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellFloatSlider priority = new TableCellFloatSlider((String)null, factor.priority, 0.0F, 10.0F);
         priority.addListener((v) -> {
            factor.priority = v.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.placer.factor.priority"), priority)).withTitleTooltip(IvTranslations.getLines("reccomplex.placer.factor.priority.tooltip"));
      }}));
   }

   @Nonnull
   public String title() {
      return this.placer.displayString();
   }
}
