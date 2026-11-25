package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerMovementMessage implements IMessage {
   public int entityId;
   public double x;
   public double y;
   public double z;
   public double mx;
   public double my;
   public double mz;

   public PlayerMovementMessage() {
   }

   public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
      this.entityId = entityId;
      this.x = x;
      this.y = y;
      this.z = z;
      this.mx = mx;
      this.my = my;
      this.mz = mz;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.entityId = buf.readInt();
         this.x = buf.readDouble();
         this.y = buf.readDouble();
         this.z = buf.readDouble();
         this.mx = buf.readDouble();
         this.my = buf.readDouble();
         this.mz = buf.readDouble();
      } catch (Exception var3) {
         System.out.print("Playermovement error: ");
         System.out.println(buf);
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.mx);
      buf.writeDouble(this.my);
      buf.writeDouble(this.mz);
   }

   public static class Handler implements IMessageHandler<PlayerMovementMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PlayerMovementMessage message, MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new PlayerMovementMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         PlayerMovementMessage message;
         MessageContext ctx;

         public runner(PlayerMovementMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World world = this.ctx.getServerHandler().player.world;
            Entity entity = world.getEntityByID(this.message.entityId);
            if (entity != null) {
               if (entity instanceof EntityPlayerMP) {
                  EntityPlayerMP referencedPlayer = (EntityPlayerMP)entity;
                  if (this.ctx.getServerHandler().player.getGameProfile().equals(referencedPlayer.getGameProfile())) {
                     entity.posX = this.message.x;
                     entity.posY = this.message.y;
                     entity.posZ = this.message.z;
                     entity.motionX = this.message.mx;
                     entity.motionY = this.message.my;
                     entity.motionZ = this.message.mz;
                     EntityPlayerMP player = (EntityPlayerMP)entity;
                     Method capturePositionMethod = grapplemod.proxy.getCapturePositionMethod();
                     if (capturePositionMethod != null) {
                        try {
                           capturePositionMethod.invoke(player.connection);
                        } catch (IllegalAccessException var8) {
                           var8.printStackTrace();
                        } catch (IllegalArgumentException var9) {
                           var9.printStackTrace();
                        } catch (InvocationTargetException var10) {
                           var10.printStackTrace();
                        }
                     } else {
                        System.out.println("Error capturePositionMethod is null");
                     }

                     if (!player.onGround) {
                        if (this.message.my >= 0.0D) {
                           player.fallDistance = 0.0F;
                        } else {
                           double gravity = 0.1D;
                           player.fallDistance = (float)(Math.pow(this.message.my, 2.0D) / (2.0D * gravity));
                        }
                     }
                  }
               }

            }
         }
      }
   }
}
