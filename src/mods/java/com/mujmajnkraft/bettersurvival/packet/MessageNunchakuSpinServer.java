package com.mujmajnkraft.bettersurvival.packet;

import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNunchakuSpinServer implements IMessage {
   private boolean isSpinning;
   private int entityId;

   public MessageNunchakuSpinServer() {
   }

   public MessageNunchakuSpinServer(boolean b, int id) {
      this.isSpinning = b;
      this.entityId = id;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.isSpinning = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.isSpinning);
   }

   public static class Handler implements IMessageHandler<MessageNunchakuSpinServer, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageNunchakuSpinServer message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
            if (entity instanceof EntityPlayer) {
               INunchakuCombo cap = (INunchakuCombo)entity.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
               cap.setSpinning(message.isSpinning);
            }

         });
         return null;
      }
   }
}
