package ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorLimit;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.StructurePlaceContext;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RayMove extends FactorLimit.Ray {
   public int distance;

   public RayMove() {
      this((Float)null, 0);
   }

   public RayMove(Float weight, int distance) {
      super(weight);
      this.distance = distance;
   }

   public OptionalInt cast(WorldCache cache, StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface, int y) {
      y = y + this.distance;
      return y >= 0 && y < cache.world.getHeight() ? OptionalInt.of(y) : OptionalInt.empty();
   }

   public String displayString() {
      return String.format("%s %s %s", directionArrow(this.distance == 0 ? null : this.distance > 0), IvTranslations.get(String.format("reccomplex.placer.factors.limit.rays.%s.title%s", FactorLimit.getRayRegistry().iDForType(this.getClass()), this.weight != null ? ".mark" : "")), String.valueOf(Math.abs(this.distance)));
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSegmented(new TableDataSource[]{this.rayTableDataSource(navigator, delegate), new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellIntTextField cell = new TableCellIntTextField((String)null, this.distance);
         cell.addListener((v) -> {
            this.distance = v.intValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.move.distance"), cell);
      }})});
   }
}
