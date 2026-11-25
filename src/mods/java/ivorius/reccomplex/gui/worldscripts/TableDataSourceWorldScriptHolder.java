package ivorius.reccomplex.gui.worldscripts;

import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDataSourceBlockState;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.network.PacketWorldDataHandler;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.script.WorldScriptHolder;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptHolder extends TableDataSourceSegmented {
   public WorldScriptHolder script;
   protected TableNavigator tableNavigator;
   protected TableDelegate tableDelegate;
   protected BlockPos pos2;

   public TableDataSourceWorldScriptHolder(WorldScriptHolder script, BlockPos worldPos, TableNavigator tableNavigator, TableDelegate tableDelegate) {
      this.script = script;
      this.tableNavigator = tableNavigator;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new Supplier[]{() -> {
         if (script.worldData == null) {
            TableCellButton cell = new TableCellButton((String)null, (String)null, IvTranslations.get("reccomplex.worldscript.holder.capture"));
            cell.addAction(() -> {
               PacketWorldDataHandler.capture(script, worldPos, this.pos2);
            });
            return cell;
         } else {
            TableCellButton cell = new TableCellButton((String)null, (String)null, IvTranslations.get("reccomplex.worldscript.holder.delete"));
            cell.addAction(() -> {
               script.worldData = null;
               tableDelegate.reloadData();
            });
            return cell;
         }
      }, () -> {
         TableCellButton cell = new TableCellButton((String)null, (String)null, IvTranslations.get("reccomplex.worldscript.holder.switch"));
         cell.addAction(() -> {
            PacketWorldDataHandler.swap(script, worldPos);
         });
         cell.setEnabled(script.worldData != null);
         return cell;
      }});
      TableDataSourceBlockPos cellPoint1 = new TableDataSourceBlockPos(script.origin, (p) -> {
         script.origin = p;
      }, IvTranslations.get("reccomplex.worldscript.holder.point.1"), IvTranslations.getLines("reccomplex.worldscript.holder.point.1.tooltip"));
      cellPoint1.setEnabled(() -> {
         return script.worldData == null;
      });
      this.addSegment(1, cellPoint1);
      this.pos2 = script.worldData != null ? script.origin.add(BlockPositions.fromIntArray(Structures.size(script.worldData, (int[])null))).add(-1, -1, -1) : script.origin.add(0, 0, 0);
      TableDataSourceBlockPos cellPoint2 = new TableDataSourceBlockPos(this.pos2, (p) -> {
         this.pos2 = p;
      }, IvTranslations.get("reccomplex.worldscript.holder.point.2"), IvTranslations.getLines("reccomplex.worldscript.holder.point.2.tooltip"));
      cellPoint2.setEnabled(() -> {
         return script.worldData == null;
      });
      this.addSegment(2, cellPoint2);
      this.addSegment(3, new Supplier[]{() -> {
         return new TitledCell((new TableCellTitle((String)null, IvTranslations.get("reccomplex.worldscript.holder.self"))).withTooltip(IvTranslations.getLines("reccomplex.worldscript.holder.self.tooltip")));
      }});
      this.addSegment(4, new TableDataSourceBlockState(script.replaceState, (s) -> {
         script.replaceState = s;
      }, tableNavigator, tableDelegate));
   }

   @Nonnull
   public String title() {
      return "Holder";
   }
}
