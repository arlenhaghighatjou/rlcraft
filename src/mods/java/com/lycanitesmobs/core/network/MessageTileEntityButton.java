package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.tileentity.TileEntityBase;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTileEntityButton implements IMessage {
   public byte buttonId;
   public BlockPos tileEntityPos;

   public MessageTileEntityButton() {
   }

   public MessageTileEntityButton(byte buttonId, BlockPos tileEntityPos) {
      this.buttonId = buttonId;
      this.tileEntityPos = tileEntityPos;
   }

   public static void onMessage(MessageTileEntityButton message, MessageContext ctx, EntityPlayer player) {
      World world = player.getEntityWorld();
      if (world != null) {
         TileEntity tileEntity = world.getTileEntity(message.tileEntityPos);
         if (tileEntity instanceof TileEntityBase) {
            TileEntityBase tileEntityBase = (TileEntityBase)tileEntity;
            tileEntityBase.onGuiButton(message.buttonId);
         }
      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.buttonId = packet.readByte();
      this.tileEntityPos = new BlockPos(packet.readInt(), packet.readInt(), packet.readInt());
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeByte(this.buttonId);
      packet.writeInt(this.tileEntityPos.getX());
      packet.writeInt(this.tileEntityPos.getY());
      packet.writeInt(this.tileEntityPos.getZ());
   }
}
