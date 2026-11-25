package ivorius.reccomplex.gui.worldscripts.multi;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.TableDataSourceWorldScript;
import ivorius.reccomplex.world.gen.script.WorldScriptMulti;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptMulti extends TableDataSourceSegmented {
   public WorldScriptMulti script;

   public TableDataSourceWorldScriptMulti(WorldScriptMulti script, BlockPos realWorldPos, TableDelegate delegate, TableNavigator navigator) {
      this.script = script;
      this.addSegment(0, new TableDataSourceWorldScript(script));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.worldscript.multi.condition"), script.environmentExpression, (Object)null));
      this.addSegment(2, new TableDataSourceWorldScriptList(script.scripts, realWorldPos, delegate, navigator));
   }
}
