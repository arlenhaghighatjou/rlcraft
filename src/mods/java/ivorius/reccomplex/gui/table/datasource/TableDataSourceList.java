package ivorius.reccomplex.gui.table.datasource;

import ivorius.ivtoolkit.lang.IvClasses;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.ExpandableCell;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellPresetAction;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;

public abstract class TableDataSourceList<T, L extends List<T>> extends TableDataSourceSegmented {
   protected L list;
   protected TableDelegate tableDelegate;
   protected TableNavigator navigator;
   @Nullable
   protected String earlierTitle = TextFormatting.BOLD + "↑";
   @Nullable
   protected String laterTitle = TextFormatting.BOLD + "↓";
   @Nullable
   protected String deleteTitle = TextFormatting.RED + "-";
   @Nullable
   protected String addTitle = TextFormatting.GREEN + "+";
   @Nullable
   protected String duplicateTitle = null;
   protected boolean usesPresetActionForAdding;

   public TableDataSourceList(L list, TableDelegate tableDelegate, TableNavigator navigator) {
      this.list = list;
      this.tableDelegate = tableDelegate;
      this.navigator = navigator;
   }

   @Nullable
   protected T tryInstantiate(String actionID, Class<? extends T> clazz, String format) {
      if (clazz == null) {
         RecurrentComplex.logger.error(String.format(format, actionID));
         return (T)null;
      } else {
         return (T)IvClasses.instantiate(clazz);
      }
   }

   public L getList() {
      return this.list;
   }

   public void setList(L list) {
      this.list = list;
   }

   public TableDelegate getTableDelegate() {
      return this.tableDelegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.tableDelegate = tableDelegate;
   }

   public TableNavigator getNavigator() {
      return this.navigator;
   }

   public void setNavigator(TableNavigator navigator) {
      this.navigator = navigator;
   }

   public String getEarlierTitle() {
      return this.earlierTitle;
   }

   public void setEarlierTitle(String earlierTitle) {
      this.earlierTitle = earlierTitle;
   }

   public String getLaterTitle() {
      return this.laterTitle;
   }

   public void setLaterTitle(String laterTitle) {
      this.laterTitle = laterTitle;
   }

   public String getDeleteTitle() {
      return this.deleteTitle;
   }

   public void setDeleteTitle(String deleteTitle) {
      this.deleteTitle = deleteTitle;
   }

   public String getAddTitle() {
      return this.addTitle;
   }

   public void setAddTitle(String addTitle) {
      this.addTitle = addTitle;
   }

   @Nullable
   public String getDuplicateTitle() {
      return this.duplicateTitle;
   }

   public void setDuplicateTitle(@Nullable String duplicateTitle) {
      this.duplicateTitle = duplicateTitle;
   }

   public boolean isUsesPresetActionForAdding() {
      return this.usesPresetActionForAdding;
   }

   public void setUsesPresetActionForAdding(boolean usesPresetActionForAdding) {
      this.usesPresetActionForAdding = usesPresetActionForAdding;
   }

   public int numberOfSegments() {
      return 3;
   }

   public int sizeOfSegment(int segment) {
      if (this.isListSegment(segment)) {
         return Math.max(this.list.size(), 1);
      } else {
         int addIndex = this.getAddIndex(segment);
         return addIndex >= 0 ? 1 : 0;
      }
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (this.isListSegment(segment)) {
         if (this.list.size() == 0) {
            return new TitledCell(new TableCellTitle((String)null, String.format("%s%s%s", TextFormatting.GRAY, TextFormatting.ITALIC, IvTranslations.get("reccomplex.gui.list.noelements"))));
         } else {
            T t = (T)this.list.get(index);
            TableCellMulti multi = new TableCellMulti(this.entryCells(index));
            multi.setSize(0, 8.0F);
            return (new TitledCell(this.getDisplayString(t), multi)).withTitleTooltip(this.getTooltip(t));
         }
      } else {
         int addIndex = this.getAddIndex(segment);
         if (addIndex < 0) {
            return null;
         } else if (this.isUsesPresetActionForAdding()) {
            TableCellPresetAction cell = new TableCellPresetAction("add" + addIndex, this.getAddActions());
            cell.addAction((actionID) -> {
               this.createAddAction(addIndex, actionID).run();
            });
            return new TitledCell(new ExpandableCell(this.addTitle, cell, this.canEditList()));
         } else {
            List<TableCellButton> cells = this.getAddActions();

            for(TableCellButton cell : cells) {
               cell.addAction(this.createAddAction(addIndex, cell.actionID));
               cell.setId("add" + addIndex);
            }

            return new TitledCell(new TableCellMulti(cells));
         }
      }
   }

   @Nonnull
   protected Runnable createAddAction(int addIndex, String actionID) {
      return () -> {
         T entry = (T)this.newEntry(actionID);
         if (entry != null) {
            this.list.add(addIndex, entry);
            this.getTableDelegate().reloadData();
         }

      };
   }

   public boolean isListSegment(int segment) {
      return segment == 1;
   }

   public int getAddIndex(int segment) {
      return segment == 0 ? 0 : (segment == 2 ? this.list.size() : -1);
   }

   public List<TableCellButton> getAddActions() {
      boolean enabled = this.canEditList();
      return Collections.singletonList(new TableCellButton("", "add", this.getAddTitle(), enabled));
   }

   public List<TableCell> entryCells(int index) {
      boolean enabled = this.canEditList();
      T t = (T)this.list.get(index);
      List<TableCell> cells = new ArrayList();
      cells.add(this.entryCell(enabled, t));
      if (this.getEarlierTitle() != null) {
         TableCellButton earlier = new TableCellButton("", "earlier", this.getEarlierTitle(), index > 0 && enabled);
         earlier.addAction(() -> {
            this.list.set(index, this.list.get(index - 1));
            this.list.set(index - 1, t);
            this.tableDelegate.reloadData();
         });
         cells.add(earlier);
      }

      if (this.getLaterTitle() != null) {
         TableCellButton later = new TableCellButton("", "later", this.getLaterTitle(), index < this.list.size() - 1 && enabled);
         later.addAction(() -> {
            this.list.set(index, this.list.get(index + 1));
            this.list.set(index + 1, t);
            this.tableDelegate.reloadData();
         });
         cells.add(later);
      }

      if (this.getDuplicateTitle() != null) {
         TableCellButton duplicate = new TableCellButton("", "duplicate", this.getDuplicateTitle(), enabled);
         duplicate.addAction(() -> {
            this.list.add(index, this.copyEntry(t));
            this.tableDelegate.reloadData();
         });
         cells.add(duplicate);
      }

      if (this.getDeleteTitle() != null) {
         TableCellButton delete = new TableCellButton("", "delete", this.getDeleteTitle(), enabled);
         delete.addAction(() -> {
            this.list.remove(index);
            this.tableDelegate.reloadData();
         });
         cells.add(delete);
      }

      return cells;
   }

   @Nonnull
   public abstract TableCell entryCell(boolean var1, T var2);

   public boolean canEditList() {
      return true;
   }

   public abstract String getDisplayString(T var1);

   public List<String> getTooltip(T t) {
      return null;
   }

   public abstract T newEntry(String var1);

   public T copyEntry(T t) {
      throw new UnsupportedOperationException();
   }
}
