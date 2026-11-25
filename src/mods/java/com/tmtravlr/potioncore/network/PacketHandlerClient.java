package com.tmtravlr.potioncore.network;

import com.tmtravlr.potioncore.PotionCore;
import com.tmtravlr.potioncore.PotionCoreHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerClient implements IMessageHandler<SToCMessage, IMessage> {
   public static final int SET_DROWN = 1;
   public static final int KNOCK_UP = 2;
   public static final int REVIVAL_HEARTS = 3;
   public static final int SERVER_CHECK = 4;

   public PacketHandlerClient() {
   }

   public IMessage onMessage(SToCMessage packet, MessageContext context) {
      PacketBuffer buff = new PacketBuffer(Unpooled.wrappedBuffer(packet.getData()));
      EntityPlayer player = PotionCore.proxy.getPlayer();
      World world = null;
      if (player != null) {
         world = player.world;
      }

      int type = buff.readInt();
      switch(type) {
      case 1:
         int air = buff.readInt();
         if (player != null) {
            player.getEntityData().setInteger("potioncore - drown air", air);
         }
         break;
      case 2:
         int amount = buff.readInt();
         if (player != null) {
            player.motionY = (double)amount;
         }
         break;
      case 3:
         int entityId = buff.readInt();
         if (world != null) {
            Entity entity = world.getEntityByID(entityId);
            if (entity != null) {
               for(int i = 0; i < 9; ++i) {
                  double offset = (double)entity.width;
                  double yOffset = (double)entity.getEyeHeight();
                  double xOffset = offset * Math.sin((double)(i * 160) / 3.141592653589793D);
                  double zOffset = offset * Math.cos((double)(i * 160) / 3.141592653589793D);
                  entity.world.spawnParticle(EnumParticleTypes.HEART, entity.posX + xOffset, entity.posY + yOffset, entity.posZ + zOffset, 0.0D, 0.0D, 0.0D, new int[0]);
               }
            }
         }
         break;
      case 4:
         PotionCoreHelper.hasServer = true;
      }

      return null;
   }
}
