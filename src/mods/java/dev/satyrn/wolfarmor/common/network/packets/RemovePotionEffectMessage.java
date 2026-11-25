package dev.satyrn.wolfarmor.common.network.packets;

import dev.satyrn.wolfarmor.common.network.MessageBase;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class RemovePotionEffectMessage extends MessageBase.ClientMessageBase<RemovePotionEffectMessage> {
   private int entityId;
   private Potion potion;

   public RemovePotionEffectMessage() {
   }

   public RemovePotionEffectMessage(int entityId, Potion effect) {
      this.entityId = entityId;
      this.potion = effect;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.entityId = buffer.readVarInt();
      this.potion = Potion.getPotionById(buffer.readUnsignedByte());
   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.entityId);
      buffer.writeByte(Potion.getIdFromPotion(this.potion));
   }

   protected IMessage process(EntityPlayer player, Side side) {
      Entity entity = player.getEntityWorld().getEntityByID(this.entityId);
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).removeActivePotionEffect(this.potion);
      }

      return null;
   }
}
