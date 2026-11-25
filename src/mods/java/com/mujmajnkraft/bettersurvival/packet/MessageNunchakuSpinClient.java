package com.mujmajnkraft.bettersurvival.packet;

import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNunchakuSpinClient implements IMessage {
   private boolean isSpinning;

   public MessageNunchakuSpinClient() {
   }

   public MessageNunchakuSpinClient(boolean b) {
      this.isSpinning = b;
   }

   public void fromBytes(ByteBuf buf) {
      this.isSpinning = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.isSpinning);
   }

   public static class Handler implements IMessageHandler<MessageNunchakuSpinClient, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageNunchakuSpinClient message, MessageContext ctx) {
         EntityPlayerMP thePlayer = ctx.getServerHandler().player;
         thePlayer.getServer().addScheduledTask(() -> {
            INunchakuCombo cap = (INunchakuCombo)thePlayer.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
            cap.setSpinning(message.isSpinning);
            if (!message.isSpinning) {
               cap.setComboTime(0);
            }

            BetterSurvivalPacketHandler.NETWORK.sendToAllTracking(new MessageNunchakuSpinServer(message.isSpinning, thePlayer.getEntityId()), thePlayer);
         });
         return null;
      }
   }
}
