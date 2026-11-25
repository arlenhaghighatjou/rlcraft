package ivorius.reccomplex.gui.nbt;

import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellPresetAction;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceNBTList extends TableDataSourceSegmented {
   protected TableDelegate delegate;
   protected TableNavigator navigator;
   protected NBTTagList nbt;
   protected String earlierTitle = TextFormatting.BOLD + "↑";
   protected String laterTitle = TextFormatting.BOLD + "↓";
   protected String deleteTitle = TextFormatting.RED + "-";
   protected String addTitle = TextFormatting.GREEN + "+";

   public TableDataSourceNBTList(TableDelegate delegate, TableNavigator navigator, NBTTagList nbt) {
      this.delegate = delegate;
      this.navigator = navigator;
      this.nbt = nbt;
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

   @Nonnull
   public String title() {
      return "NBT List";
   }

   public int numberOfSegments() {
      return 4;
   }

   public int sizeOfSegment(int segment) {
      return segment == 2 ? this.nbt.tagCount() : 1;
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 0) {
         return new TableCellPresetAction((String)null, (List)IntStream.range(0, 12).mapToObj((id) -> {
            return TableDataSourceNBT.typeButton(id, () -> {
               while(!this.nbt.hasNoTags()) {
                  this.nbt.removeTag(this.nbt.tagCount() - 1);
               }

               ReflectionHelper.setPrivateValue(NBTTagList.class, this.nbt, Byte.valueOf((byte)id), "tagType", "tagType");
               this.delegate.reloadData();
            });
         }).collect(Collectors.toList()));
      } else if (segment != 1 && segment != 3) {
         if (segment == 2) {
            TableCellMulti multi = new TableCellMulti(this.getEntryActions(index));
            multi.setSize(0, 8.0F);
            return new TitledCell("" + index, multi);
         } else {
            return super.cellForIndexInSegment(table, index, segment);
         }
      } else {
         return TableDataSourceNBT.addButton(this.nbt.getTagType(), (n) -> {
            if (segment == 1) {
               List<NBTBase> cur = new ArrayList(this.nbt.tagCount());

               while(!this.nbt.hasNoTags()) {
                  cur.add(this.nbt.removeTag(this.nbt.tagCount() - 1));
               }

               this.nbt.appendTag(n);

               for(NBTBase prev : cur) {
                  this.nbt.appendTag(prev);
               }
            } else {
               this.nbt.appendTag(n);
            }

            this.delegate.reloadData();
         });
      }
   }

   public List<TableCell> getEntryActions(int index) {
      boolean enabled = true;
      NBTBase cellNBT = this.nbt.get(index);
      TableCell entryCell = TableDataSourceNBT.cell(cellNBT, this.delegate, this.navigator);
      TableCellButton earlier = new TableCellButton("", "earlier", this.getEarlierTitle(), index > 0 && enabled);
      earlier.addAction(() -> {
         this.nbt.set(index, this.nbt.get(index - 1));
         this.nbt.set(index - 1, cellNBT);
         this.delegate.reloadData();
      });
      TableCellButton later = new TableCellButton("", "later", this.getLaterTitle(), index < this.nbt.tagCount() - 1 && enabled);
      later.addAction(() -> {
         this.nbt.set(index, this.nbt.get(index + 1));
         this.nbt.set(index + 1, cellNBT);
         this.delegate.reloadData();
      });
      TableCellButton delete = new TableCellButton("", "delete", this.getDeleteTitle(), enabled);
      delete.addAction(() -> {
         this.nbt.removeTag(index);
         this.delegate.reloadData();
      });
      return Arrays.asList(entryCell, earlier, later, delete);
   }
}
