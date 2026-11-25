package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageAddHealth implements IMessage {
   private float[] table;

   public MessageAddHealth() {
   }

   public MessageAddHealth(float[] table) {
      this.table = table;
   }

   public void fromBytes(ByteBuf buf) {
      this.table = new float[8];

      for(int i = 0; i < 8; ++i) {
         this.table[i] = buf.readFloat();
      }

   }

   public void toBytes(ByteBuf buf) {
      for(float f : this.table) {
         buf.writeFloat(f);
      }

   }

   public static class Handler implements IMessageHandler<MessageAddHealth, IMessage> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessageAddHealth message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(playerSP.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));

            for(int i = 0; i < message.table.length; ++i) {
               float f = message.table[i];
               EnumPlayerPart part = EnumPlayerPart.fromID(i + 1);
               damageModel.getFromEnum(part).heal(f, playerSP, false);
            }

         });
         return null;
      }
   }
}
