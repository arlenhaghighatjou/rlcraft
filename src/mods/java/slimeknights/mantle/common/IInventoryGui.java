package slimeknights.mantle.common;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IInventoryGui {
   Container createContainer(InventoryPlayer var1, World var2, BlockPos var3);

   @SideOnly(Side.CLIENT)
   GuiContainer createGui(InventoryPlayer var1, World var2, BlockPos var3);
}
