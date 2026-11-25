package ivorius.reccomplex.gui.worldscripts.mazegenerator.rules;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourcePreloaded;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceMazePathList;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.Connector;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.ConnectorFactory;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved.MazeRuleConnectAll;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeRuleConnectAll extends TableDataSourceSegmented {
   private final MazeRuleConnectAll rule;
   private List<SavedMazePathConnection> expected;
   private TableDelegate tableDelegate;

   public TableDataSourceMazeRuleConnectAll(MazeRuleConnectAll rule, TableDelegate tableDelegate, TableNavigator navigator, List<SavedMazePathConnection> expected, Selection bounds) {
      this.rule = rule;
      this.expected = expected;
      this.tableDelegate = tableDelegate;
      this.addSegment(1, new TableDataSourcePreloaded(new TableCell[]{new TitledCell(new TableCellTitle("", "Paths"))}));
      this.addSegment(3, new TableDataSourceMazePathList(rule.exits, tableDelegate, navigator, bounds));
   }

   @Nonnull
   public String title() {
      return "Connect All";
   }

   public int numberOfSegments() {
      return this.rule.additive ? 4 : 6;
   }

   public int sizeOfSegment(int segment) {
      switch(segment) {
      case 0:
      case 2:
         return 1;
      case 1:
      case 3:
      default:
         return super.sizeOfSegment(segment);
      case 4:
         return 1;
      case 5:
         return this.expected.size() - this.rule.exits.size();
      }
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 0) {
         TableCellBoolean preventCell = new TableCellBoolean("prevent", this.rule.preventConnection, TextFormatting.GOLD + IvTranslations.get("reccomplex.mazerule.connect.prevent"), TextFormatting.GREEN + IvTranslations.get("reccomplex.mazerule.connect.prevent"));
         preventCell.addListener((val) -> {
            this.rule.preventConnection = val.booleanValue();
         });
         return new TitledCell(preventCell);
      } else if (segment == 2) {
         TableCellBoolean cell = new TableCellBoolean("additive", this.rule.additive, TextFormatting.GREEN + IvTranslations.get("reccomplex.mazerule.connectall.additive"), TextFormatting.GOLD + IvTranslations.get("reccomplex.mazerule.connectall.subtractive"));
         cell.addListener((val) -> {
            this.rule.additive = val.booleanValue();
            this.tableDelegate.reloadData();
         });
         return new TitledCell(cell);
      } else if (segment == 4) {
         return new TitledCell(new TableCellTitle("", IvTranslations.get("reccomplex.mazerule.connectall.preview")));
      } else if (segment == 5) {
         ConnectorFactory factory = new ConnectorFactory();
         Set<Connector> blockedConnections = Collections.singleton(factory.get("Wall"));
         List<SavedMazePath> exitPaths = (List)MazeRuleConnectAll.getPaths(this.rule.exits, this.expected, blockedConnections, factory).collect(Collectors.toList());
         return new TitledCell(new TableCellTitle("", ((SavedMazePath)exitPaths.get(index)).toString()));
      } else {
         return super.cellForIndexInSegment(table, index, segment);
      }
   }
}
