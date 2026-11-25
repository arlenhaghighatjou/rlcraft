package de.Whitedraco.switchbow.proxy.packets;

import de.Whitedraco.switchbow.tileentity.TileEntityPodest;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TEPodestUpdatePacket implements IMessage {
   int x;
   int y;
   int z;
   ItemStack stack;

   public TEPodestUpdatePacket() {
   }

   public TEPodestUpdatePacket(int i, int j, int k, ItemStack stack) {
      this.x = i;
      this.y = j;
      this.z = k;
      this.stack = stack;
   }

   public void fromBytes(ByteBuf buf) {
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.stack = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      ByteBufUtils.writeItemStack(buf, this.stack);
   }

   public void execute(MessageContext ctx) {
      TileEntity te = Minecraft.getMinecraft().world.getTileEntity(new BlockPos(this.x, this.y, this.z));
      if (te != null && te instanceof TileEntityPodest) {
         TileEntityPodest tePodest = (TileEntityPodest)te;
         tePodest.setInventorySlotContents(0, this.stack);
      }

   }

   public static class Handler implements IMessageHandler<TEPodestUpdatePacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(TEPodestUpdatePacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
