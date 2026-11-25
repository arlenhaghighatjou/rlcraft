package bettercombat.mod.network;

import bettercombat.mod.util.Helpers;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMainhandAttack implements IMessage {
   private int entityId;
   private double motionX;
   private double motionY;
   private double motionZ;

   public PacketMainhandAttack() {
   }

   public PacketMainhandAttack(int parEntityId) {
      this.entityId = parEntityId;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
   }

   public PacketMainhandAttack(int parEntityId, double motionX, double motionY, double motionZ) {
      this.entityId = parEntityId;
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = ByteBufUtils.readVarInt(buf, 4);
      this.motionX = buf.readDouble();
      this.motionY = buf.readDouble();
      this.motionZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.entityId, 4);
      buf.writeDouble(this.motionX);
      buf.writeDouble(this.motionY);
      buf.writeDouble(this.motionZ);
   }

   public static class Handler implements IMessageHandler<PacketMainhandAttack, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PacketMainhandAttack message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            handle(message, ctx);
         });
         return null;
      }

      private static void handle(PacketMainhandAttack message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         Entity theEntity = player.world.getEntityByID(message.entityId);
         if (theEntity != null) {
            if (player.interactionManager.getGameType() == GameType.SPECTATOR) {
               player.setSpectatingEntity(theEntity);
            } else {
               Helpers.attackTargetEntityItem(player, theEntity, false, message.motionX, message.motionY, message.motionZ);
            }
         }

         ((WorldServer)player.world).getEntityTracker().sendToTracking(player, new SPacketAnimation(player, 0));
      }
   }
}
