package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageApplyAbsorption implements IMessage {
   private float amount;

   public MessageApplyAbsorption() {
   }

   public MessageApplyAbsorption(float amount) {
      this.amount = amount;
   }

   public void fromBytes(ByteBuf buf) {
      this.amount = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.amount);
   }

   public static class Handler implements IMessageHandler<MessageApplyAbsorption, IMessage> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessageApplyAbsorption message, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            ((AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).setAbsorption(message.amount);
         });
         return null;
      }
   }
}
