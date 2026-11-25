package svenhjol.charm.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import svenhjol.charm.crafting.container.ContainerBarrel;
import svenhjol.charm.crafting.container.ContainerBookshelfChest;
import svenhjol.charm.crafting.container.ContainerCrate;
import svenhjol.charm.crafting.gui.GuiContainerBarrel;
import svenhjol.charm.crafting.gui.GuiContainerBookshelfChest;
import svenhjol.charm.crafting.gui.GuiContainerCrate;
import svenhjol.charm.crafting.tile.TileBarrel;
import svenhjol.charm.crafting.tile.TileBookshelfChest;
import svenhjol.charm.crafting.tile.TileCrate;

public class GuiHandler implements IGuiHandler {
   public static final int CRATE = 0;
   public static final int BOOKSHELF_CHEST = 1;
   public static final int BARREL = 2;

   public GuiHandler() {
   }

   public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
         if (tile instanceof TileCrate) {
            return new ContainerCrate(player.inventory, (TileCrate)tile);
         }
      case 1:
         TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
         if (tile instanceof TileBookshelfChest) {
            return new ContainerBookshelfChest(player.inventory, (TileBookshelfChest)tile);
         }
      case 2:
         TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
         if (tile instanceof TileBarrel) {
            return new ContainerBarrel(player.inventory, (TileBarrel)tile);
         }
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         return new GuiContainerCrate(this.getServerGuiElement(ID, player, world, x, y, z), player.inventory);
      case 1:
         return new GuiContainerBookshelfChest(this.getServerGuiElement(ID, player, world, x, y, z), player.inventory);
      case 2:
         return new GuiContainerBarrel(this.getServerGuiElement(ID, player, world, x, y, z), player.inventory);
      default:
         return null;
      }
   }
}
