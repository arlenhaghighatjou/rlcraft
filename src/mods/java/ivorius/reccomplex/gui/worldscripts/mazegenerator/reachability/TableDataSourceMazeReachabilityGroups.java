package ivorius.reccomplex.gui.worldscripts.mazegenerator.reachability;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazeReachabilityGroups extends TableDataSourceSegmented {
   private SavedMazeReachability reachability;
   private Set<SavedMazePath> expected;
   private TableDelegate tableDelegate;
   private TableNavigator tableNavigator;

   public TableDataSourceMazeReachabilityGroups(SavedMazeReachability reachability, Set<SavedMazePath> expected, TableDelegate tableDelegate, TableNavigator tableNavigator) {
      this.reachability = reachability;
      this.expected = expected;
      this.tableDelegate = tableDelegate;
      this.tableNavigator = tableNavigator;
   }

   public SavedMazeReachability getReachability() {
      return this.reachability;
   }

   public void setReachability(SavedMazeReachability reachability) {
      this.reachability = reachability;
   }

   public TableDelegate getTableDelegate() {
      return this.tableDelegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.tableDelegate = tableDelegate;
   }

   public TableNavigator getTableNavigator() {
      return this.tableNavigator;
   }

   public void setTableNavigator(TableNavigator tableNavigator) {
      this.tableNavigator = tableNavigator;
   }

   public List<Set<SavedMazePath>> groups() {
      return this.reachability.groups;
   }

   public int numberOfSegments() {
      return 2 + this.groups().size() * 2;
   }

   public int sizeOfSegment(int segment) {
      return segment % 2 == 0 ? 1 : this.getVirtualGroup(segment / 2 - 1).size();
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      int group = segment / 2 - 1;
      if (segment % 2 == 0) {
         return new TitledCell(new TableCellTitle("groupTitle" + group, group < 0 ? IvTranslations.get("reccomplex.reachability.groups.default") : IvTranslations.format("reccomplex.reachability.groups.group", new Object[]{"" + (group + 1)})));
      } else {
         SavedMazePath t = (SavedMazePath)this.getVirtualGroup(group).get(index);
         TableCellButton[] entryActions = this.getEntryActions(group, index);

         for(TableCellButton entryAction : entryActions) {
            entryAction.addAction(() -> {
               SavedMazePath element = (SavedMazePath)this.getVirtualGroup(group).get(index);
               if (group >= 0) {
                  ((Set)this.groups().get(group)).remove(element);
               }

               String var5 = entryAction.actionID;
               byte var6 = -1;
               switch(var5.hashCode()) {
               case -1965778112:
                  if (var5.equals("earlier")) {
                     var6 = 0;
                  }
                  break;
               case 108960:
                  if (var5.equals("new")) {
                     var6 = 2;
                  }
                  break;
               case 102744716:
                  if (var5.equals("later")) {
                     var6 = 1;
                  }
               }

               switch(var6) {
               case 0:
                  ((Set)this.groups().get(group - 1)).add(element);
                  break;
               case 1:
                  ((Set)this.groups().get(group + 1)).add(element);
                  break;
               case 2:
                  this.groups().add(Sets.newHashSet(new SavedMazePath[]{element}));
               }

               if (group >= 0 && ((Set)this.groups().get(group)).isEmpty()) {
                  this.groups().remove(group);
               }

               this.tableDelegate.reloadData();
            });
            entryAction.setId(String.format("entry%d,%d", group, index));
         }

         return new TitledCell(this.getDisplayString(t), new TableCellMulti(entryActions));
      }
   }

   public TableCellButton[] getEntryActions(int group, int index) {
      boolean first = group < 0;
      boolean second = group == 0;
      boolean last = group == this.groups().size() - 1;
      List<SavedMazePath> groupL = this.getVirtualGroup(group);
      boolean enabled = true;
      return new TableCellButton[]{new TableCellButton("", second ? "default" : "earlier", IvTranslations.get("reccomplex.reachability.groups.previous"), enabled && !first), new TableCellButton("", last ? "new" : "later", last ? IvTranslations.get("reccomplex.reachability.groups.new") : IvTranslations.get("reccomplex.reachability.groups.next"), enabled && (!last || groupL.size() > 1))};
   }

   private List<SavedMazePath> getVirtualGroup(int group) {
      Set<SavedMazePath> paths = group < 0 ? this.defaultGroup() : (Set)this.groups().get(group);
      List<SavedMazePath> sorted = Lists.newArrayList(paths);
      Collections.sort(sorted);
      return sorted;
   }

   private Set<SavedMazePath> defaultGroup() {
      Set<SavedMazePath> view = this.expected;

      for(Set<SavedMazePath> group : this.groups()) {
         view = Sets.difference(view, group);
      }

      return view;
   }

   private String getDisplayString(SavedMazePath path) {
      return String.format("%s%s%s %s", TextFormatting.BLUE, path.getEnumFacing(), TextFormatting.RESET, path.getSourceRoom());
   }
}
