package cursedflames.bountifulbaubles.proxy;

import cursedflames.bountifulbaubles.block.ContainerReforger;
import cursedflames.bountifulbaubles.block.GuiReforger;
import cursedflames.bountifulbaubles.block.TileReforger;
import cursedflames.bountifulbaubles.client.gui.GuiPhantomPrism;
import cursedflames.bountifulbaubles.container.ContainerPhantomPrism;
import cursedflames.bountifulbaubles.wormhole.ContainerWormhole;
import cursedflames.bountifulbaubles.wormhole.GuiWormhole;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {
   public GuiProxy() {
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == 2) {
         return new ContainerWormhole(player);
      } else if (id == 3) {
         return new ContainerPhantomPrism(player);
      } else {
         BlockPos pos = new BlockPos(x, y, z);
         TileEntity te = world.getTileEntity(pos);
         return te instanceof TileReforger ? new ContainerReforger(player.inventory, player, (TileReforger)te) : null;
      }
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == 2) {
         return new GuiWormhole(new ContainerWormhole(player));
      } else if (id == 3) {
         return new GuiPhantomPrism(new ContainerPhantomPrism(player));
      } else {
         BlockPos pos = new BlockPos(x, y, z);
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileReforger) {
            TileReforger containerTileEntity = (TileReforger)te;
            return new GuiReforger(containerTileEntity, new ContainerReforger(player.inventory, player, containerTileEntity));
         } else {
            return null;
         }
      }
   }
}
