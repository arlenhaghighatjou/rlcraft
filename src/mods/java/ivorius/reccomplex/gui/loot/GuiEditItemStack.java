package ivorius.reccomplex.gui.loot;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import ivorius.reccomplex.network.PacketSyncItem;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEditItemStack<T extends TableDataSourceItem> extends GuiScreenEditTable<T> {
   protected int playerSlot;
   protected EntityPlayer player;

   public GuiEditItemStack(EntityPlayer player, int playerSlot, T dataSource) {
      this.playerSlot = playerSlot;
      this.player = player;
      dataSource.setStack(player.inventory.getStackInSlot(playerSlot).copy());
      GuiTable table = this.setDataSource(dataSource, this::saveAndSend);
      table.setHideScrollbarIfUnnecessary(true);
   }

   public void saveAndSend(T t) {
      ItemStack stack = t.stack.copy();
      this.player.inventory.setInventorySlotContents(this.playerSlot, stack);
      RecurrentComplex.network.sendToServer(new PacketSyncItem(this.playerSlot, stack));
   }
}
