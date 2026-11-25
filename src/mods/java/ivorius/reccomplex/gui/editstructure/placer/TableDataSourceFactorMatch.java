package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorMatch;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceFactorMatch extends TableDataSourceSegmented {
   public FactorMatch placer;

   public TableDataSourceFactorMatch(FactorMatch factor, TableDelegate delegate, TableNavigator navigator) {
      this.placer = factor;
      this.addSegment(0, new TableDataSourcePlacerFactor(factor, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), factor.sourceMatcher, (Object)null));
      this.addSegment(2, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.placer.factors.match.dest"), factor.destMatcher, (Object)null));
      this.addSegment(3, new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellFloatSlider cell = new TableCellFloatSlider((String)null, factor.requiredConformity, 0.0F, 1.0F);
         cell.addListener((v) -> {
            factor.requiredConformity = v.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.placer.factors.match.conformity"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.placer.factors.match.conformity.tooltip"));
      }}));
   }
}
