package xzeroair.trinkets.network.status;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.capabilities.statushandler.TrinketStatusEffect;
import xzeroair.trinkets.network.ThreadSafePacket;

public class StatusEffectPacket extends ThreadSafePacket {
   boolean sourceNull;
   private int attackerEntityID;
   private int targetEntityID;
   private int effectID;
   private int duration;
   private int level;
   private boolean combine;

   public StatusEffectPacket() {
      this.sourceNull = false;
      this.attackerEntityID = 0;
      this.targetEntityID = 0;
      this.effectID = 0;
      this.duration = 0;
      this.level = 0;
   }

   public StatusEffectPacket(Entity source, EntityLivingBase targetEntity, int ID, int dur, int level, boolean combine) {
      this.sourceNull = false;
      this.attackerEntityID = 0;
      this.targetEntityID = 0;
      this.effectID = 0;
      this.duration = 0;
      this.level = 0;
      if (source != null) {
         this.sourceNull = false;
         this.attackerEntityID = source.getEntityId();
      } else {
         this.sourceNull = true;
      }

      this.targetEntityID = targetEntity.getEntityId();
      this.effectID = ID;
      this.duration = dur;
      this.level = level;
      this.combine = combine;
   }

   public StatusEffectPacket(Entity source, EntityLivingBase targetEntity, TrinketStatusEffect effect) {
      this(source, targetEntity, effect.getEffectID(), effect.getDuration(), effect.getLevel(), false);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.sourceNull);
      if (this.sourceNull) {
         buf.writeInt(this.attackerEntityID);
      }

      buf.writeInt(this.targetEntityID);
      buf.writeInt(this.effectID);
      buf.writeInt(this.duration);
      buf.writeInt(this.level);
      buf.writeBoolean(this.combine);
   }

   public void fromBytes(ByteBuf buf) {
      this.sourceNull = buf.readBoolean();
      if (this.sourceNull) {
         this.attackerEntityID = buf.readInt();
      }

      this.targetEntityID = buf.readInt();
      this.effectID = buf.readInt();
      this.duration = buf.readInt();
      this.level = buf.readInt();
      this.combine = buf.readBoolean();
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;

      try {
         Entity entity = clientPlayer.getEntityWorld().getEntityByID(this.targetEntityID);
         Entity source = this.sourceNull ? null : clientPlayer.getEntityWorld().getEntityByID(this.attackerEntityID);
         StatusHandler status = Capabilities.getStatusHandler(entity);
         if (status != null) {
            if (this.combine) {
               status.combine(this.effectID, this.duration, this.level);
            } else {
               status.apply(this.effectID, this.duration, this.level, source);
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      EntityPlayerMP serverPlayer = server.player;
      WorldServer world = serverPlayer.getServerWorld();

      try {
         Entity entity = serverPlayer.getEntityWorld().getEntityByID(this.attackerEntityID);
         if (entity != null && entity instanceof EntityPlayerMP) {
            world = ((EntityPlayerMP)entity).getServerWorld();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }
}
