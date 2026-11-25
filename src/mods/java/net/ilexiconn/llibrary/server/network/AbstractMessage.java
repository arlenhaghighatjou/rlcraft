package net.ilexiconn.llibrary.server.network;

import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMessage<T extends AbstractMessage<T>> implements IMessage, IMessageHandler<T, IMessage> {
   public AbstractMessage() {
   }

   public IMessage onMessage(T message, MessageContext messageContext) {
      LLibrary.PROXY.handleMessage(message, messageContext);
      return null;
   }

   @SideOnly(Side.CLIENT)
   public abstract void onClientReceived(Minecraft var1, T var2, EntityPlayer var3, MessageContext var4);

   public abstract void onServerReceived(MinecraftServer var1, T var2, EntityPlayer var3, MessageContext var4);

   public boolean registerOnSide(Side side) {
      return true;
   }
}
