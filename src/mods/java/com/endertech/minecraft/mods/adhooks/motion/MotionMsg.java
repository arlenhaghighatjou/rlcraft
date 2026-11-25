package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import com.endertech.minecraft.mods.adhooks.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** @deprecated */
@Deprecated
public class MotionMsg extends ForgeNetMsg {
   public int entityId;
   public Vect3d position;
   public Vect3d motion;

   public static MotionMsg of(Entity entity) {
      return new MotionMsg(entity);
   }

   public MotionMsg() {
   }

   protected MotionMsg(Entity entity) {
      this.entityId = entity.getEntityId();
      this.position = ForgeEntity.getCurPosition(entity);
      this.motion = ForgeEntity.getMotion(entity);
   }

   public void sendToAllTracking(Entity entity) {
      World world = entity.getEntityWorld();
      if (world instanceof WorldServer) {
         for(EntityPlayer player : ((WorldServer)world).getEntityTracker().getTrackingPlayers(entity)) {
            if (player instanceof EntityPlayerMP) {
               Main.instance.getConnection().sendToPlayer(this, (EntityPlayerMP)player);
            }
         }
      }

   }

   public void sendToServer() {
      Main.instance.getConnection().sendToServer(this);
   }

   public static class Handler extends ForgeNetMsgHandler<MotionMsg> {
      public Handler() {
      }

      public IMessage onMessage(MotionMsg message, MessageContext ctx) {
         World world = this.getWorld(ctx);
         if (world == null) {
            return null;
         } else {
            Entity entity = world.getEntityByID(message.entityId);
            if (entity == null) {
               return null;
            } else {
               if (entity instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)entity).connection.update();
               }

               this.addScheduledTask(ctx, () -> {
                  if (this.isServerSide(ctx)) {
                     ForgeEntity.setCurPosition(entity, message.position);
                  }

                  ForgeEntity.setMotion(entity, message.motion);
               });
               return null;
            }
         }
      }
   }
}
