package org.orecruncher.dsurround.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.capabilities.CapabilityEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityDataSettable;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.WorldUtils;

public class PacketEntityData implements IMessage {
   private int entityId;
   private boolean isAttacking;
   private boolean isFleeing;

   public PacketEntityData() {
   }

   public PacketEntityData(@Nonnull IEntityData data) {
      this.entityId = data.getEntityId();
      this.isAttacking = data.isAttacking();
      this.isFleeing = data.isFleeing();
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.entityId = buf.readInt();
      this.isAttacking = buf.readBoolean();
      this.isFleeing = buf.readBoolean();
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.isAttacking);
      buf.writeBoolean(this.isFleeing);
   }

   public static class PacketHandler implements IMessageHandler<PacketEntityData, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketEntityData message, @Nullable MessageContext ctx) {
         if (ctx != null) {
            ModBase.proxy().getThreadListener(ctx).addScheduledTask(() -> {
               World world = EnvironStateHandler.EnvironState.getWorld();
               if (world != null) {
                  Entity entity = WorldUtils.locateEntity(world, message.entityId);
                  if (entity != null) {
                     IEntityDataSettable data = (IEntityDataSettable)CapabilityEntityData.getCapability(entity);
                     if (data != null) {
                        data.setAttacking(message.isAttacking);
                        data.setFleeing(message.isFleeing);
                     }
                  }
               }

            });
         }

         return null;
      }
   }
}
