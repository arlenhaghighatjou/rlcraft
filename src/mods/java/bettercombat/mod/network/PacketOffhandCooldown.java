package bettercombat.mod.network;

import bettercombat.mod.util.ScheduledPacketTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOffhandCooldown implements IMessage {
   public int cooldown;
   public int cooldownBeginning;

   public PacketOffhandCooldown(int cooldown, int cooldownBeginning) {
      this.cooldown = cooldown;
      this.cooldownBeginning = cooldownBeginning;
   }

   public PacketOffhandCooldown() {
   }

   public void fromBytes(ByteBuf buf) {
      this.cooldown = buf.readInt();
      this.cooldownBeginning = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.cooldown);
      buf.writeInt(this.cooldownBeginning);
   }

   @SideOnly(Side.CLIENT)
   public static class ClientHandler implements IMessageHandler<PacketOffhandCooldown, IMessage> {
      public ClientHandler() {
      }

      public IMessage onMessage(PacketOffhandCooldown message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(new ScheduledPacketTask(Minecraft.getMinecraft().player, message));
         return null;
      }
   }

   public static class ServerHandler implements IMessageHandler<PacketOffhandCooldown, IMessage> {
      public ServerHandler() {
      }

      public IMessage onMessage(PacketOffhandCooldown message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new ScheduledPacketTask(ctx.getServerHandler().player, message));
         return null;
      }
   }
}
