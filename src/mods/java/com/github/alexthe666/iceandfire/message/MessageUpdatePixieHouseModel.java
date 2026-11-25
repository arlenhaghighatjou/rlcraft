package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdatePixieHouseModel extends AbstractMessage<MessageUpdatePixieHouseModel> {
   public long blockPos;
   public int houseType;

   public MessageUpdatePixieHouseModel(long blockPos, int houseType) {
      this.blockPos = blockPos;
      this.houseType = houseType;
   }

   public MessageUpdatePixieHouseModel() {
   }

   public void fromBytes(ByteBuf buf) {
      this.blockPos = buf.readLong();
      this.houseType = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.blockPos);
      buf.writeInt(this.houseType);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageUpdatePixieHouseModel message, EntityPlayer player, MessageContext messageContext) {
      BlockPos pos = BlockPos.fromLong(message.blockPos);
      if (client.world.getTileEntity(pos) != null) {
         if (client.world.getTileEntity(pos) instanceof TileEntityPixieHouse) {
            TileEntityPixieHouse house = (TileEntityPixieHouse)client.world.getTileEntity(pos);
            house.houseType = message.houseType;
         }

         if (client.world.getTileEntity(pos) instanceof TileEntityJar) {
            TileEntityJar jar = (TileEntityJar)client.world.getTileEntity(pos);
            jar.pixieType = message.houseType;
         }
      }

   }

   public void onServerReceived(MinecraftServer server, MessageUpdatePixieHouseModel message, EntityPlayer player, MessageContext messageContext) {
   }
}
