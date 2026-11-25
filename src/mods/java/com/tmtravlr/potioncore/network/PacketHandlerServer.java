package com.tmtravlr.potioncore.network;

import com.tmtravlr.potioncore.PotionCoreAttributes;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerServer implements IMessageHandler<CToSMessage, IMessage> {
   public static final int CLIMB_FALL = 1;
   public static final int USE_ENTITY = 2;
   public static final int ATTACK_ENTITY = 3;
   public static final int INTERACT_ENTITY = 4;
   public static final int INTERACT_AT_ENTITY = 5;

   public PacketHandlerServer() {
   }

   public IMessage onMessage(CToSMessage packet, MessageContext context) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      PacketBuffer buff = new PacketBuffer(Unpooled.wrappedBuffer(packet.getData()));
      int type = buff.readInt();
      switch(type) {
      case 1:
         server.addScheduledTask(() -> {
            Entity player = server.getPlayerList().getPlayerByUUID(new UUID(buff.readLong(), buff.readLong()));
            if (player != null) {
               player.fallDistance = 0.0F;
            }

         });
         break;
      case 2:
         server.addScheduledTask(() -> {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(new UUID(buff.readLong(), buff.readLong()));
            if (player != null) {
               useEntity(player, buff);
            }

         });
      }

      return null;
   }

   private static void useEntity(EntityPlayerMP player, PacketBuffer buff) {
      WorldServer worldServer = player.world.getMinecraftServer().getWorld(player.dimension);
      player.markPlayerActive();
      int targetId = buff.readInt();
      Entity entity = worldServer.getEntityByID(targetId);
      if (entity != null) {
         int type = buff.readInt();
         if ((double)player.getDistance(entity) <= PotionCoreAttributes.getPlayerReachDistance(player)) {
            switch(type) {
            case 3:
               if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityArrow) && entity != player) {
                  player.attackTargetEntityWithCurrentItem(entity);
               } else {
                  player.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                  FMLLog.warning("Player " + player.getName() + " tried to attack an invalid entity", new Object[0]);
               }
               break;
            case 4:
               EnumHand enumhand = (EnumHand)buff.readEnumValue(EnumHand.class);
               player.interactOn(entity, enumhand);
               break;
            case 5:
               EnumHand enumhand = (EnumHand)buff.readEnumValue(EnumHand.class);
               player.getHeldItem(enumhand);
               Vec3d posVec = new Vec3d(buff.readDouble(), buff.readDouble(), buff.readDouble());
               if (ForgeHooks.onInteractEntityAt(player, entity, posVec, enumhand) == null) {
                  entity.applyPlayerInteraction(player, posVec, enumhand);
               }
            }
         }

      }
   }
}
