package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules;

import ivorius.ivtoolkit.maze.components.ConnectionStrategy;
import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.Connector;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.ConnectorFactory;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class MazeRule implements NBTCompoundObject {
   public MazeRule() {
   }

   public abstract String displayString();

   public abstract TableDataSource tableDataSource(TableNavigator var1, TableDelegate var2, List<SavedMazePathConnection> var3, Selection var4);

   public abstract MazePredicate<Connector> build(WorldScriptMazeGenerator var1, Set<Connector> var2, ConnectorFactory var3, Collection<? extends MazeComponent<Connector>> var4, ConnectionStrategy<Connector> var5);
}
