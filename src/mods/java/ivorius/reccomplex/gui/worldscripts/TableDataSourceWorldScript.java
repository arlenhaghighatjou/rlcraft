package ivorius.reccomplex.gui.worldscripts;

import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.script.WorldScript;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScript extends TableDataSourceSegmented {
   WorldScript<?> script;

   public TableDataSourceWorldScript(WorldScript<?> script) {
      this.script = script;
   }

   @Nonnull
   public String title() {
      return this.script.getDisplayString();
   }
}
