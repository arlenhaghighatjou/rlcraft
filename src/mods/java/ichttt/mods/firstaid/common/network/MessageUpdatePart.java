package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdatePart implements IMessage {
   private byte id;
   private int maxHealth;
   private float absorption;
   private float currentHealth;

   public MessageUpdatePart() {
   }

   public MessageUpdatePart(AbstractDamageablePart part) {
      this.id = part.part.id;
      this.maxHealth = part.getMaxHealth();
      this.absorption = part.getAbsorption();
      this.currentHealth = part.currentHealth;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readByte();
      this.maxHealth = buf.readInt();
      this.absorption = buf.readFloat();
      this.currentHealth = buf.readFloat();
      this.validate();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.id);
      buf.writeInt(this.maxHealth);
      buf.writeFloat(this.absorption);
      buf.writeFloat(this.currentHealth);
      this.validate();
   }

   private void validate() {
      if (this.currentHealth < 0.0F) {
         throw new RuntimeException("Negative currentHealth!");
      } else if (this.absorption < 0.0F) {
         throw new RuntimeException("Negative absorption!");
      } else if (this.maxHealth < 0) {
         throw new RuntimeException("Negative maxHealth!");
      } else if (EnumPlayerPart.fromID(this.id).id != this.id) {
         throw new RuntimeException("Wrong player mapping!");
      }
   }

   public static class Handler implements IMessageHandler<MessageUpdatePart, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageUpdatePart message, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            AbstractDamageablePart damageablePart = ((AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).getFromEnum(EnumPlayerPart.fromID(message.id));
            damageablePart.setMaxHealth(message.maxHealth);
            damageablePart.setAbsorption(message.absorption);
            damageablePart.currentHealth = message.currentHealth;
         });
         return null;
      }
   }
}
