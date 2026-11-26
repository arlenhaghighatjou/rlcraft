package noppes.vc;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.blocks.tiles.TileBook;
import noppes.vc.constants.PacketServer;
import noppes.vc.containers.ContainerTradingBlock;

public class PacketHandlerServer {
   public PacketHandlerServer() {
   }

   public void onServerPacket(ServerCustomPacketEvent event) {
      final EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;
      final ByteBuf buffer = event.getPacket().payload();
      player.getServer().addScheduledTask(new Runnable() {
         public void run() {
            PacketServer type = null;

            try {
               type = PacketServer.values()[buffer.readInt()];
               PacketHandlerServer.this.handlePacket(type, buffer, player);
            } catch (Exception var3) {
               VariedCommodities.Log.error("Error with EnumPacketServer." + type, var3);
            }

         }
      });
   }

   private void handlePacket(PacketServer type, ByteBuf buffer, EntityPlayerMP player) throws Exception {
      if (type == PacketServer.TRADE_ACCEPT) {
         if (!(player.openContainer instanceof ContainerTradingBlock)) {
            return;
         }

         ContainerTradingBlock con = (ContainerTradingBlock)player.openContainer;
         if (!con.tile.isFull()) {
            return;
         }

         ContainerTradingBlock con2 = (ContainerTradingBlock)con.tile.other(player).openContainer;
         if (con.state == 0) {
            con.setState(2);
            con2.setState(1);
         } else if (con.state == 1 || con.state == 2) {
            con.setState(3);
            con2.setState(3);

            for(int i = 0; i < 9; ++i) {
               ItemStack item = con.craftMatrix.getStackInSlot(i);
               con.craftMatrix.setInventorySlotContents(i, con2.craftMatrix.getStackInSlot(i));
               con2.craftMatrix.setInventorySlotContents(i, item);
            }
         }
      } else if (type == PacketServer.SAVE_SIGN) {
         BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
         TileEntity tile = player.world.getTileEntity(pos);
         if (tile == null || !(tile instanceof TileBigSign)) {
            return;
         }

         TileBigSign sign = (TileBigSign)tile;
         if (sign.canEdit) {
            sign.setText(Server.readString(buffer));
            sign.canEdit = false;
            tile.markDirty();
            IBlockState state = player.world.getBlockState(pos);
            player.world.notifyBlockUpdate(pos, state, state, 3);
         }
      } else if (type == PacketServer.SAVE_BOOK) {
         BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
         TileEntity tileentity = player.world.getTileEntity(pos);
         if (!(tileentity instanceof TileBook)) {
            return;
         }

         TileBook tile = (TileBook)tileentity;
         if (tile.book.getItem() == Items.WRITTEN_BOOK) {
            return;
         }

         boolean sign = buffer.readBoolean();
         ItemStack book = new ItemStack(Server.readNBT(buffer));
         if (book.isEmpty()) {
            return;
         }

         if (book.getItem() == Items.WRITABLE_BOOK && !sign && ItemWritableBook.isNBTValid(book.getTagCompound())) {
            tile.book.setTagInfo("pages", book.getTagCompound().getTagList("pages", 8));
         }

         if (book.getItem() == Items.WRITTEN_BOOK && sign && ItemWrittenBook.validBookTagContents(book.getTagCompound())) {
            tile.book.setTagInfo("author", new NBTTagString(player.getName()));
            tile.book.setTagInfo("title", new NBTTagString(book.getTagCompound().getString("title")));
            tile.book.setTagInfo("pages", book.getTagCompound().getTagList("pages", 8));
            tile.book = CommonUtils.ChangeItemStack(tile.book, Items.WRITTEN_BOOK);
         }
      }

   }

   private void warn(EntityPlayer player, String warning) {
      player.getServer().logWarning(player.getName() + ": " + warning);
   }
}
