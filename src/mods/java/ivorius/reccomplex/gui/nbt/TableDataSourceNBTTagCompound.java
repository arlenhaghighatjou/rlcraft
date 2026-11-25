package ivorius.reccomplex.gui.nbt;

import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellPresetAction;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceNBTTagCompound extends TableDataSourceSegmented {
   protected TableDelegate delegate;
   protected TableNavigator navigator;
   protected NBTTagCompound nbt;
   protected final List<String> sortedKeys = new ArrayList();

   public TableDataSourceNBTTagCompound(TableDelegate delegate, TableNavigator navigator, NBTTagCompound nbt) {
      this.delegate = delegate;
      this.navigator = navigator;
      this.nbt = nbt;
      this.resetSortedKeys();
   }

   protected void resetSortedKeys() {
      this.sortedKeys.clear();
      this.sortedKeys.addAll(this.nbt.getKeySet());
      this.sortedKeys.sort(String::compareToIgnoreCase);
   }

   public NBTTagCompound getNbt() {
      return this.nbt;
   }

   public void setNbt(NBTTagCompound nbt) {
      this.nbt = nbt;
   }

   @Nonnull
   public String title() {
      return "NBT Compound";
   }

   public int numberOfSegments() {
      return 4;
   }

   public int sizeOfSegment(int segment) {
      return segment == 2 ? this.nbt.getKeySet().size() : 1;
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 0) {
         TableCellButton load = new TableCellButton((String)null, "toString", "->", Collections.singletonList("Convert to String (slightly lossy)"), true);
         TableCellButton perform = new TableCellButton((String)null, "fromString", "O", Collections.singletonList("Load from String"), false);
         TableCellString cell = new TableCellString("tileEntityInfo", "");
         cell.addListener((val) -> {
            perform.setEnabled(WeightedBlockState.tryParse(val) != null);
         });
         cell.setMaxStringLength(32500);
         load.addAction(() -> {
            cell.setPropertyValue(this.nbt.toString());
            perform.setEnabled(true);
         });
         perform.addAction(() -> {
            NBTTagCompound other = WeightedBlockState.tryParse((String)cell.getPropertyValue());
            if (other != null) {
               this.nbt.getKeySet().clear();
               other.getKeySet().forEach((s) -> {
                  this.nbt.setTag(s, other.getTag(s));
               });
               this.resetSortedKeys();
               this.delegate.reloadData();
            }

         });
         TableCellMulti multi = new TableCellMulti(new TableCell[]{load, cell, perform});
         multi.setSize(0, 0.1F);
         multi.setSize(2, 0.1F);
         return new TitledCell("As String", multi);
      } else if (segment != 1 && segment != 3) {
         String key = (String)this.sortedKeys.get(index);
         String[] nextKey = new String[]{key};
         NBTBase cellNBT = this.nbt.getTag(key);
         TableCell nbtCell = TableDataSourceNBT.cell(cellNBT, this.delegate, this.navigator);
         TableCellString keyCell = new TableCellString((String)null, key);
         TableCellButton setKeyCell = new TableCellButton((String)null, "setKey", "O", Collections.singletonList("Set Key"), false);
         setKeyCell.addAction(() -> {
            this.sortedKeys.set(index, nextKey[0]);
            this.sortedKeys.subList(index + 1, this.sortedKeys.size()).remove(nextKey[0]);
            this.sortedKeys.subList(0, index).remove(nextKey[0]);
            this.nbt.removeTag(key);
            this.nbt.setTag(nextKey[0], cellNBT);
            this.delegate.reloadData();
         });
         keyCell.addListener((value) -> {
            nextKey[0] = value;
            setKeyCell.setEnabled(true);
         });
         TableCellButton deleteCell = new TableCellButton((String)null, (String)null, TextFormatting.RED + "-");
         deleteCell.addAction(() -> {
            this.sortedKeys.remove(key);
            this.nbt.removeTag(key);
            this.delegate.reloadData();
         });
         TableCellMulti multi = new TableCellMulti(new TableCell[]{keyCell, setKeyCell, nbtCell, deleteCell});
         multi.setSize(1, 0.2F);
         multi.setSize(3, 0.2F);
         return multi;
      } else {
         return new TableCellPresetAction((String)null, (List)IntStream.range(0, 12).mapToObj((id) -> {
            return TableDataSourceNBT.addButton(id, (n) -> {
               this.nbt.setTag("", n);
               this.sortedKeys.removeIf((s) -> {
                  return s.equals("");
               });
               this.sortedKeys.add(segment == 1 ? 0 : this.sortedKeys.size(), "");
               this.delegate.reloadData();
            });
         }).collect(Collectors.toList()));
      }
   }
}
