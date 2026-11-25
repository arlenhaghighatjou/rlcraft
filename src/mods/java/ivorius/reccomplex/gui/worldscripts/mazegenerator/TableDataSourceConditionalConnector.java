package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceConditionalConnector extends TableDataSourceSegmented {
   public SavedMazePathConnection.ConditionalConnector conditionalConnector;

   public TableDataSourceConditionalConnector(SavedMazePathConnection.ConditionalConnector conditionalConnector) {
      this.conditionalConnector = conditionalConnector;
      this.addSegment(0, new TableDataSourceConnector(conditionalConnector.connector, IvTranslations.get("reccomplex.maze.connector")));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.maze.conditional_connector.condition"), conditionalConnector.expression, (Object)null));
   }

   @Nonnull
   public String title() {
      return "Conditional Connector";
   }
}
