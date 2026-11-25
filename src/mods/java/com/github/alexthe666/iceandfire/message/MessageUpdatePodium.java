package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdatePodium extends AbstractMessage<MessageUpdatePodium> {
   public long blockPos;
   public ItemStack heldStack;

   public MessageUpdatePodium(long blockPos, ItemStack heldStack) {
      this.blockPos = blockPos;
      this.heldStack = heldStack;
   }

   public MessageUpdatePodium() {
   }

   public void fromBytes(ByteBuf buf) {
      this.blockPos = buf.readLong();
      this.heldStack = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.blockPos);
      ByteBufUtils.writeItemStack(buf, this.heldStack);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageUpdatePodium message, EntityPlayer player, MessageContext messageContext) {
      BlockPos pos = BlockPos.fromLong(message.blockPos);
      if (client.world.getTileEntity(pos) != null && client.world.getTileEntity(pos) instanceof TileEntityPodium) {
         TileEntityPodium podium = (TileEntityPodium)client.world.getTileEntity(pos);
         podium.setInventorySlotContents(0, message.heldStack);
      }

   }

   public void onServerReceived(MinecraftServer server, MessageUpdatePodium message, EntityPlayer player, MessageContext messageContext) {
   }
}
