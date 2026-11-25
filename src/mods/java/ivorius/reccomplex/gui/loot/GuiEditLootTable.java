package ivorius.reccomplex.gui.loot;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import ivorius.reccomplex.network.PacketSaveLootTable;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEditLootTable extends GuiScreenEditTable<TableDataSourceLootTableComponent> {
   public GuiEditLootTable(EntityPlayer player, GenericLootTable.Component component, String key, SaveDirectoryData saveDirectoryData) {
      this.setDataSource(new TableDataSourceLootTableComponent(key, component, saveDirectoryData, player, this, this), (ds) -> {
         RecurrentComplex.network.sendToServer(new PacketSaveLootTable(ds.key, ds.component, ds.getSaveDirectoryData().getResult()));
      });
   }
}
