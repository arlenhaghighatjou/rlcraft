package knightminer.inspirations.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.mantle.tileentity.TileInventory;

public class InventorySlotSyncPacket extends AbstractPacketThreadsafe {
   public ItemStack itemStack;
   public int slot;
   public BlockPos pos;

   public InventorySlotSyncPacket() {
   }

   public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
      this.itemStack = itemStack;
      this.pos = pos;
      this.slot = slot;
   }

   public void handleClientSafe(NetHandlerPlayClient netHandler) {
      TileEntity tileEntity = Minecraft.getMinecraft().player.getEntityWorld().getTileEntity(this.pos);
      if (tileEntity != null && tileEntity instanceof TileInventory) {
         TileInventory tile = (TileInventory)tileEntity;
         tile.setInventorySlotContents(this.slot, this.itemStack);
         Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate((World)null, this.pos, (IBlockState)null, (IBlockState)null, 0);
      }
   }

   public void handleServerSafe(NetHandlerPlayServer netHandler) {
      throw new UnsupportedOperationException("Clientside only");
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = this.readPos(buf);
      this.slot = buf.readShort();
      this.itemStack = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      this.writePos(this.pos, buf);
      buf.writeShort(this.slot);
      ByteBufUtils.writeItemStack(buf, this.itemStack);
   }
}
