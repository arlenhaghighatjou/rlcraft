package dev.satyrn.wolfarmor.common.network.packets;

import dev.satyrn.wolfarmor.common.network.MessageBase;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdatePotionEffectMessage extends MessageBase.ClientMessageBase<UpdatePotionEffectMessage> {
   private int entityId;
   private byte effectId;
   private byte amplifier;
   private int duration;
   private byte flags;

   public UpdatePotionEffectMessage() {
   }

   public UpdatePotionEffectMessage(int entityId, PotionEffect potionEffect) {
      this.entityId = entityId;
      this.effectId = (byte)(Potion.getIdFromPotion(potionEffect.getPotion()) & 255);
      this.amplifier = (byte)(potionEffect.getAmplifier() & 255);
      this.duration = Math.min(32767, potionEffect.getDuration());
      this.flags = 0;
      if (potionEffect.getIsAmbient()) {
         this.flags = (byte)(this.flags | 1);
      }

      if (potionEffect.doesShowParticles()) {
         this.flags = (byte)(this.flags | 2);
      }

   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.entityId = buffer.readVarInt();
      this.effectId = buffer.readByte();
      this.amplifier = buffer.readByte();
      this.duration = buffer.readVarInt();
      this.flags = buffer.readByte();
   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.entityId);
      buffer.writeByte(this.effectId);
      buffer.writeByte(this.amplifier);
      buffer.writeVarInt(this.duration);
      buffer.writeByte(this.flags);
   }

   @SideOnly(Side.CLIENT)
   public boolean doesShowParticles() {
      return (this.flags & 2) == 2;
   }

   @SideOnly(Side.CLIENT)
   public boolean getIsAmbient() {
      return (this.flags & 1) == 1;
   }

   @SideOnly(Side.CLIENT)
   public boolean isMaxDuration() {
      return this.duration == 32767;
   }

   protected IMessage process(EntityPlayer player, Side side) {
      Entity entity = player.getEntityWorld().getEntityByID(this.entityId);
      if (entity instanceof EntityLivingBase) {
         Potion potion = Potion.getPotionById(this.effectId & 255);
         if (potion != null) {
            PotionEffect potioneffect = new PotionEffect(potion, this.duration, this.amplifier, this.getIsAmbient(), this.doesShowParticles());
            potioneffect.setPotionDurationMax(this.isMaxDuration());
            ((EntityLivingBase)entity).addPotionEffect(potioneffect);
         }
      }

      return null;
   }
}
