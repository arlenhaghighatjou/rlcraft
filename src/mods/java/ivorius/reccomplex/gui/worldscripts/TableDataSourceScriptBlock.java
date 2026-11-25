package ivorius.reccomplex.gui.worldscripts;

import ivorius.reccomplex.block.TileEntityBlockScript;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.gui.worldscripts.multi.TableDataSourceWorldScriptMulti;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceScriptBlock extends TableDataSourceSegmented {
   public TileEntityBlockScript script;

   public TableDataSourceScriptBlock(TileEntityBlockScript script, TableDelegate delegate, TableNavigator navigator) {
      this.script = script;
      this.addSegment(0, new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellBoolean spawn = new TableCellBoolean((String)null, script.spawnTriggerable);
         spawn.addListener((b) -> {
            script.spawnTriggerable = b.booleanValue();
         });
         spawn.setTrueTitle(TextFormatting.GREEN + "Spawn");
         spawn.setFalseTitle(TextFormatting.GRAY + "Spawn");
         TableCellBoolean redstone = new TableCellBoolean((String)null, script.redstoneTriggerable);
         redstone.addListener((b) -> {
            script.redstoneTriggerable = b.booleanValue();
         });
         redstone.setTrueTitle(TextFormatting.GREEN + "Redstone");
         redstone.setFalseTitle(TextFormatting.GRAY + "Redstone");
         return new TitledCell("Triggerable", new TableCellMulti(new TableCell[]{spawn, redstone}));
      }}));
      this.addSegment(1, new TableDataSourceWorldScriptMulti(script.script, script.getPos(), delegate, navigator));
   }

   @Nonnull
   public String title() {
      return "Script Block";
   }
}
