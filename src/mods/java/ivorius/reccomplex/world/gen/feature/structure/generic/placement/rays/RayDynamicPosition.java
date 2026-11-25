package ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays;

import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
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

public class RayDynamicPosition extends FactorLimit.Ray {
   public RayDynamicPosition.Type type;

   public RayDynamicPosition() {
      this((Float)null, RayDynamicPosition.Type.BEDROCK);
   }

   public RayDynamicPosition(Float weight, RayDynamicPosition.Type type) {
      super(weight);
      this.type = type;
   }

   public OptionalInt cast(WorldCache cache, StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface, int y) {
      switch(this.type) {
      case BEDROCK:
         return OptionalInt.of(0);
      case SEALEVEL:
         return OptionalInt.of(cache.world.getSeaLevel());
      case WORLD_HEIGHT:
         return OptionalInt.of(cache.world.getHeight() - 1 - collection.height);
      default:
         throw new IllegalStateException();
      }
   }

   public String displayString() {
      return String.format("%s %s", directionArrow((Boolean)null), IvTranslations.get("reccomplex.placer.factors.limit.rays.dynpos.type." + IvGsonHelper.serializedName(this.type)));
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSegmented(new TableDataSource[]{this.rayTableDataSource(navigator, delegate), new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellEnum<RayDynamicPosition.Type> cell = new TableCellEnum<RayDynamicPosition.Type>((String)null, this.type, TableCellEnum.options(RayDynamicPosition.Type.values(), "reccomplex.placer.factors.limit.rays.dynpos.type.", true));
         cell.addListener((v) -> {
            this.type = v;
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.dynpos.type"), cell);
      }})});
   }

   public static enum Type {
      @SerializedName("bedrock")
      BEDROCK,
      @SerializedName("sealevel")
      SEALEVEL,
      @SerializedName("world_height")
      WORLD_HEIGHT;

      private Type() {
      }
   }
}
