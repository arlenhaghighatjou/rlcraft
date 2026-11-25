package net.mcft.copy.backpacks.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.mcft.copy.backpacks.client.GuiBackpack;
import net.mcft.copy.backpacks.container.ContainerBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageOpenGui implements IMessage {
   private int _windowId;
   private NBTTagCompound _data;

   public MessageOpenGui() {
   }

   public static MessageOpenGui create(ContainerBackpack container) {
      MessageOpenGui message = new MessageOpenGui();
      message._windowId = container.windowId;
      message._data = new NBTTagCompound();
      container.writeToNBT(message._data);
      return message;
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer buffer = new PacketBuffer(buf);

      try {
         this._windowId = buffer.readInt();
         this._data = buffer.readCompoundTag();
      } catch (Exception var4) {
         this._windowId = -1;
         this._data = null;
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer buffer = new PacketBuffer(buf);
      buffer.writeInt(this._windowId);
      buffer.writeCompoundTag(this._data);
   }

   public static class Handler extends BackpacksMessageHandler<MessageOpenGui> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public void handle(MessageOpenGui message, MessageContext ctx) {
         ContainerBackpack container = new ContainerBackpack(getPlayer(ctx), message._data) {
            public boolean canInteractWith(EntityPlayer player) {
               return true;
            }
         };
         container.windowId = message._windowId;
         Minecraft.getMinecraft().displayGuiScreen(new GuiBackpack(container));
      }
   }
}
