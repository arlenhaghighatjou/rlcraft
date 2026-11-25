package boni.dummy.network;

import boni.dummy.EntityDummy;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncEquipmentMessage implements IMessage {
   private int entityID;
   private int slotId;
   private ItemStack itemstack;

   public SyncEquipmentMessage() {
   }

   public SyncEquipmentMessage(int entityId, int slotId, @Nonnull ItemStack itemstack) {
      this.entityID = entityId;
      this.slotId = slotId;
      this.itemstack = itemstack.copy();
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.slotId = buf.readInt();
      this.itemstack = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      buf.writeInt(this.slotId);
      ByteBufUtils.writeItemStack(buf, this.itemstack);
   }

   public static class MessageHandlerClient implements IMessageHandler<SyncEquipmentMessage, IMessage> {
      public MessageHandlerClient() {
      }

      public SyncEquipmentMessage onMessage(final SyncEquipmentMessage message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
            public void run() {
               Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
               if (entity != null && entity instanceof EntityDummy) {
                  EntityEquipmentSlot slot = EntityEquipmentSlot.values()[message.slotId];
                  entity.setItemStackToSlot(slot, message.itemstack);
               }

            }
         });
         return null;
      }
   }
}
