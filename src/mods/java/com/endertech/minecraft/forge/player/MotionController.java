package com.endertech.minecraft.forge.player;

import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@EventBusSubscriber
public class MotionController {
   protected static Map<Entity, Vect3d> entityToMotion = new ConcurrentHashMap();

   public MotionController() {
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         for(Entry<Entity, Vect3d> entry : entityToMotion.entrySet()) {
            Entity entity = (Entity)entry.getKey();
            Vect3d motion = (Vect3d)entry.getValue();
            ForgeEntity.addMotion(entity, motion);
            if (entity instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)entity;
               sendMotionToPlayer(player, MotionController.MotionMsg.Type.ADD, motion);
            }
         }

         entityToMotion.clear();
      }

   }

   public static void addMotionFor(Entity entity, Vect3d motion) {
      Vect3d mapedMotion = (Vect3d)entityToMotion.getOrDefault(entity, Vect3d.ZERO);
      Vect3d newMotion = mapedMotion.add(motion);
      entityToMotion.put(entity, newMotion);
   }

   public static void setMotionFor(Entity entity, Vect3d motion) {
      ForgeEntity.setMotion(entity, motion);
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         sendMotionToPlayer(player, MotionController.MotionMsg.Type.SET, motion);
      }

      entityToMotion.remove(entity);
   }

   protected static void sendMotionToPlayer(EntityPlayerMP player, MotionController.MotionMsg.Type type, Vect3d motion) {
      MotionController.MotionMsg message = MotionController.MotionMsg.addMotion(player, motion);
      ForgeMain.instance.getConnection().sendToPlayer(message, player);
   }

   public static class MotionMsg extends ForgeNetMsg {
      public int id = -1;
      public MotionController.MotionMsg.Type type = MotionController.MotionMsg.Type.ADD;
      public Vect3d motion = Vect3d.ZERO;

      public MotionMsg() {
      }

      public MotionMsg(EntityPlayerMP player, MotionController.MotionMsg.Type type, Vect3d motion) {
         this.id = ForgeEntity.getId(player);
         this.type = type;
         this.motion = motion;
      }

      public static MotionController.MotionMsg setMotion(EntityPlayerMP player, Vect3d motion) {
         return new MotionController.MotionMsg(player, MotionController.MotionMsg.Type.SET, motion);
      }

      public static MotionController.MotionMsg addMotion(EntityPlayerMP player, Vect3d motion) {
         return new MotionController.MotionMsg(player, MotionController.MotionMsg.Type.ADD, motion);
      }

      public static class Handler extends ForgeNetMsgHandler<MotionController.MotionMsg> {
         public Handler() {
         }

         public MotionController.MotionMsg onMessage(MotionController.MotionMsg message, MessageContext ctx) {
            this.addScheduledTask(ctx, () -> {
               EntityPlayer player = this.getPlayer(ctx);
               if (player != null) {
                  switch(message.type) {
                  case ADD:
                     ForgeEntity.addMotion(player, message.motion);
                     break;
                  case SET:
                     ForgeEntity.setMotion(player, message.motion);
                  }
               }

            });
            return null;
         }
      }

      public static enum Type {
         ADD,
         SET;

         private Type() {
         }
      }
   }
}
