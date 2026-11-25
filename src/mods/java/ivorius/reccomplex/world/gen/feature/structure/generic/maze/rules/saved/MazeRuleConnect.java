package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved;

import ivorius.ivtoolkit.maze.components.ConnectionStrategy;
import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.rules.TableDataSourceMazeRuleConnect;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class MazeRuleConnect extends MazeRule {
   public final List<SavedMazePath> start = new ArrayList();
   public final List<SavedMazePath> end = new ArrayList();
   public boolean preventConnection = false;

   public MazeRuleConnect() {
   }

   protected static Set<MazePassage> buildPaths(List<SavedMazePath> start) {
      return (Set)start.stream().map(SavedMazePath::build).collect(Collectors.toSet());
   }

   public String displayString() {
      return String.format("%s %s->%s %s", this.summarize(this.start), this.preventConnection ? TextFormatting.GOLD : TextFormatting.GREEN, TextFormatting.RESET, this.summarize(this.end));
   }

   private String summarize(List<SavedMazePath> list) {
      return list.size() == 0 ? "?" : String.format("%s%s", ((SavedMazePath)list.get(0)).getSourceRoom().toString(), list.size() > 1 ? "..." : "");
   }

   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate, List<SavedMazePathConnection> expected, Selection bounds) {
      return new TableDataSourceMazeRuleConnect(this, delegate, navigator, bounds);
   }

   public ReachabilityStrategy<Connector> build(WorldScriptMazeGenerator script, Set<Connector> blockedConnections, ConnectorFactory connectorFactory, Collection<? extends MazeComponent<Connector>> components, ConnectionStrategy<Connector> connectionStrategy) {
      if (this.start.size() > 0 && this.end.size() > 0) {
         List<Collection<MazePassage>> points = Arrays.asList(buildPaths(this.start), buildPaths(this.end));
         LimitAABBStrategy<Object> confiner = new LimitAABBStrategy<Object>(script.mazeComponent.boundsSize());
         return this.preventConnection ? ReachabilityStrategy.preventConnection(points, confiner, connectionStrategy) : ReachabilityStrategy.connect(points, confiner, ReachabilityStrategy.compileAbilities(components), connectionStrategy);
      } else {
         return null;
      }
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.start.clear();
      this.start.addAll(NBTCompoundObjects.readListFrom(compound, "expect", SavedMazePath::new));
      this.end.clear();
      this.end.addAll(NBTCompoundObjects.readListFrom(compound, "end", SavedMazePath::new));
      this.preventConnection = compound.getBoolean("preventConnection");
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTCompoundObjects.writeListTo(compound, "expect", this.start);
      NBTCompoundObjects.writeListTo(compound, "end", this.end);
      compound.setBoolean("preventConnection", this.preventConnection);
   }
}
