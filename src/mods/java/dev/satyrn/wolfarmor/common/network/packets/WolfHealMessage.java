package dev.satyrn.wolfarmor.common.network.packets;

import dev.satyrn.wolfarmor.common.network.MessageBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class WolfHealMessage extends MessageBase.ClientMessageBase<WolfHealMessage> {
   private int entityId;

   public WolfHealMessage() {
   }

   public WolfHealMessage(int entityId) {
      this.entityId = entityId;
   }

   protected void read(PacketBuffer buffer) {
      this.entityId = buffer.readVarInt();
   }

   protected void write(PacketBuffer buffer) {
      buffer.writeVarInt(this.entityId);
   }

   protected IMessage process(EntityPlayer player, Side side) {
      World world = player.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityId);
      if (entity != null) {
         for(int particleIndex = 0; particleIndex < 3 + world.rand.nextInt(5); ++particleIndex) {
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, entity.posX + (double)(world.rand.nextFloat() * entity.width * 2.0F) - (double)entity.width, entity.posY + 0.5D + (double)(world.rand.nextFloat() * entity.height), entity.posZ + (double)(world.rand.nextFloat() * entity.width * 2.0F) - (double)entity.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

      return null;
   }
}
