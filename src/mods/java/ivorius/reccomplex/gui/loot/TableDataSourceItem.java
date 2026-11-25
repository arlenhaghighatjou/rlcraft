package ivorius.reccomplex.gui.loot;

import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceItem<T extends Item> extends TableDataSourceSegmented {
   public ItemStack stack;
   public T item;

   public TableDataSourceItem() {
   }

   public void setStack(ItemStack stack) {
      this.stack = stack;
      this.item = stack.getItem();
   }
}
