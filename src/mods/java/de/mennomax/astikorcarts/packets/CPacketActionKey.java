package de.mennomax.astikorcarts.packets;

import com.google.common.base.Predicate;
import de.mennomax.astikorcarts.capabilities.IPull;
import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.entity.AbstractDrawn;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketActionKey implements IMessage {
   public CPacketActionKey() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class ActionKeyPacketHandler implements IMessageHandler<CPacketActionKey, IMessage> {
      public ActionKeyPacketHandler() {
      }

      public IMessage onMessage(CPacketActionKey message, MessageContext ctx) {
         EntityPlayerMP sender = ctx.getServerHandler().player;
         sender.getServerWorld().addScheduledTask(() -> {
            List<AbstractDrawn> result = sender.getServerWorld().getEntitiesWithinAABB(AbstractDrawn.class, sender.getEntityBoundingBox().grow(3.0D), (entity) -> {
               return entity != sender.getRidingEntity() && entity.isEntityAlive();
            });
            if (!result.isEmpty()) {
               Entity target = (Entity)(sender.isRiding() ? sender.getRidingEntity() : sender);
               AbstractDrawn closest = (AbstractDrawn)result.get(0);

               for(AbstractDrawn cart : result) {
                  if (cart.getPulling() == target) {
                     cart.setPulling((Entity)null);
                     return;
                  }

                  if ((new Vec3d(cart.posX - sender.posX, cart.posY - sender.posY, cart.posZ - sender.posZ)).lengthVector() < (new Vec3d(closest.posX - sender.posX, closest.posY - sender.posY, closest.posZ - sender.posZ)).lengthVector()) {
                     closest = cart;
                  }
               }

               if (closest.canBePulledBy(target)) {
                  if (target.hasCapability(PullProvider.PULL, (EnumFacing)null)) {
                     AbstractDrawn drawn = ((IPull)target.getCapability(PullProvider.PULL, (EnumFacing)null)).getDrawn();
                     if (drawn != null && drawn.getPulling() == target) {
                        return;
                     }
                  }

                  closest.setPulling(target);
               }
            }

         });
         return null;
      }
   }
}
