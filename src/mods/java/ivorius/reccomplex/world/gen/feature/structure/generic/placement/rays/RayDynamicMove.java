package ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays;

import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
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

public class RayDynamicMove extends FactorLimit.Ray {
   public RayDynamicMove.Type type;
   public boolean up;

   public RayDynamicMove() {
      this((Float)null, RayDynamicMove.Type.STRUCTURE_HEIGHT);
   }

   public RayDynamicMove(Float weight, RayDynamicMove.Type type) {
      super(weight);
      this.type = type;
   }

   public OptionalInt cast(WorldCache cache, StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface, int y) {
      int mod = this.up ? 1 : -1;
      switch(this.type) {
      case STRUCTURE_HEIGHT:
         return OptionalInt.of(y + StructureBoundingBoxes.size(context.boundingBox)[1] * mod);
      default:
         throw new IllegalStateException();
      }
   }

   public String displayString() {
      return String.format("%s %s", directionArrow(Boolean.valueOf(this.up)), IvTranslations.get("reccomplex.placer.factors.limit.rays.dynmove.type." + IvGsonHelper.serializedName(this.type)));
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSegmented(new TableDataSource[]{this.rayTableDataSource(navigator, delegate), new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellEnum<RayDynamicMove.Type> cell = new TableCellEnum<RayDynamicMove.Type>((String)null, this.type, TableCellEnum.options(RayDynamicMove.Type.values(), "reccomplex.placer.factors.limit.rays.dynmove.type.", true));
         cell.addListener((v) -> {
            this.type = v;
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.dynmove.type"), cell);
      }, () -> {
         TableCellBoolean cell = new TableCellBoolean((String)null, this.up, IvTranslations.get("reccomplex.direction.up"), IvTranslations.get("reccomplex.direction.down"));
         cell.addListener((v) -> {
            this.up = v.booleanValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.direction"), cell);
      }})});
   }

   public static enum Type {
      @SerializedName("structure_height")
      STRUCTURE_HEIGHT;

      private Type() {
      }
   }
}
