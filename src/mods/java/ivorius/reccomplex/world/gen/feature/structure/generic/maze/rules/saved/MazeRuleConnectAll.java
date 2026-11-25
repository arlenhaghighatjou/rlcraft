package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved;

import ivorius.ivtoolkit.maze.components.ConnectionStrategy;
import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.rules.TableDataSourceMazeRuleConnectAll;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.Connector;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.ConnectorFactory;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.LimitAABBStrategy;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRule;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.ReachabilityStrategy;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class MazeRuleConnectAll extends MazeRule {
   public final List<SavedMazePath> exits = new ArrayList();
   public boolean additive = false;
   public boolean preventConnection = false;

   public MazeRuleConnectAll() {
   }

   public static Stream<SavedMazePath> getPaths(List<SavedMazePath> paths, List<SavedMazePathConnection> omega, Set<Connector> blockedConnections, ConnectorFactory connectorFactory) {
      return omega.stream().filter((e) -> {
         return !blockedConnections.contains(e.connector.toConnector(connectorFactory));
      }).map((e) -> {
         return e.path;
      }).filter((e) -> {
         return !paths.contains(e);
      });
   }

   public String displayString() {
      return String.format("%s%s %s", this.preventConnection ? TextFormatting.GOLD + "Split" : TextFormatting.GREEN + "Connect", TextFormatting.RESET, !this.additive && this.exits.size() == 0 ? "All" : "Some");
   }

   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate, List<SavedMazePathConnection> expected, Selection bounds) {
      return new TableDataSourceMazeRuleConnectAll(this, delegate, navigator, expected, bounds);
   }

   public MazePredicate<Connector> build(WorldScriptMazeGenerator script, Set<Connector> blockedConnections, ConnectorFactory connectorFactory, Collection<? extends MazeComponent<Connector>> components, ConnectionStrategy<Connector> connectionStrategy) {
      List<SavedMazePath> paths = this.additive ? this.exits : (List)getPaths(this.exits, script.mazeComponent.exitPaths, blockedConnections, connectorFactory).collect(Collectors.toList());
      if (paths.size() > 1) {
         List<Collection<MazePassage>> points = (List)paths.stream().map(SavedMazePath::build).map(Collections::singleton).collect(Collectors.toList());
         Predicate<MazeRoom> confiner = new LimitAABBStrategy<MazeRoom>(script.mazeComponent.boundsSize());
         return this.preventConnection ? ReachabilityStrategy.preventConnection(points, confiner, connectionStrategy) : ReachabilityStrategy.connect(points, confiner, ReachabilityStrategy.compileAbilities(components), connectionStrategy);
      } else {
         return null;
      }
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.additive = compound.getBoolean("additive");
      this.exits.clear();
      this.exits.addAll(NBTCompoundObjects.readListFrom(compound, "exits", SavedMazePath::new));
      this.preventConnection = compound.getBoolean("preventConnection");
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setBoolean("additive", this.additive);
      NBTCompoundObjects.writeListTo(compound, "exits", this.exits);
      compound.setBoolean("preventConnection", this.preventConnection);
   }
}
