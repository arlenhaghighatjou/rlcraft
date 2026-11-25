package xzeroair.trinkets.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xzeroair.trinkets.util.TrinketsConfig;

public class IncreasedReachPacket extends ThreadSafePacket {
   private int targetEntityID;
   private int hand;
   private double x;
   private double y;
   private double z;

   public IncreasedReachPacket() {
   }

   public IncreasedReachPacket(EntityLivingBase entity, EnumHand hand, Entity targetEntity, Vec3d vec) {
      this(entity, hand, targetEntity, vec.x, vec.y, vec.z);
   }

   public IncreasedReachPacket(EntityLivingBase entity, EnumHand hand, Entity targetEntity, double x, double y, double z) {
      this.entityID = entity.getEntityId();
      this.targetEntityID = targetEntity.getEntityId();
      this.hand = hand == EnumHand.MAIN_HAND ? 0 : 1;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      buf.writeInt(this.hand);
      buf.writeInt(this.targetEntityID);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.hand = buf.readInt();
      this.targetEntityID = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      if (TrinketsConfig.SERVER.misc.reach) {
         World world = server.player.getEntityWorld();
         Entity entity = world.getEntityByID(this.entityID);
         Entity interacted = world.getEntityByID(this.targetEntityID);
         if (entity instanceof EntityPlayer && interacted != null) {
            EntityPlayer player = (EntityPlayer)entity;
            if (this.hand == 1) {
               if (!interacted.processInitialInteract(player, EnumHand.OFF_HAND)) {
                  ;
               }
            } else {
               player.attackTargetEntityWithCurrentItem(interacted);
            }
         }

      }
   }
}
