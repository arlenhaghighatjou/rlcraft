package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorLimit;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceFactorLimit extends TableDataSourceSegmented {
   public FactorLimit placer;

   public TableDataSourceFactorLimit(FactorLimit factor, TableDelegate delegate, TableNavigator navigator) {
      this.placer = factor;
      this.addSegment(0, new TableDataSourcePlacerFactor(factor, delegate, navigator));
      this.addSegment(1, new TableDataSourceLimitRayList(factor.rays, delegate, navigator));
   }
}
