package net.mcft.copy.backpacks.network;

import io.netty.buffer.ByteBuf;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.misc.BackpackCapability;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageBackpackUpdate implements IMessage {
   private int _entityId;
   private MessageBackpackUpdate.UpdateType _type;
   private ItemStack _stack = ItemStack.EMPTY;
   private boolean _open;

   public MessageBackpackUpdate() {
   }

   public static MessageBackpackUpdate stack(Entity entity, ItemStack stack) {
      MessageBackpackUpdate message = new MessageBackpackUpdate();
      message._entityId = entity.getEntityId();
      message._type = MessageBackpackUpdate.UpdateType.STACK;
      message._stack = stack;
      return message;
   }

   public static MessageBackpackUpdate open(Entity entity, boolean open) {
      MessageBackpackUpdate message = new MessageBackpackUpdate();
      message._entityId = entity.getEntityId();
      message._type = MessageBackpackUpdate.UpdateType.OPEN;
      message._open = open;
      return message;
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer buffer = new PacketBuffer(buf);

      try {
         this._entityId = buffer.readInt();
         this._type = MessageBackpackUpdate.UpdateType.fromByte(buffer.readByte());
         switch(this._type) {
         case STACK:
            this._stack = buffer.readItemStack();
            break;
         case OPEN:
            this._open = buffer.readBoolean();
            break;
         default:
            throw new RuntimeException("Invalid UpdateType");
         }
      } catch (Exception var4) {
         this._entityId = -1;
         this._type = MessageBackpackUpdate.UpdateType.INVALID;
         this._stack = ItemStack.EMPTY;
         this._open = false;
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer buffer = new PacketBuffer(buf);
      buffer.writeInt(this._entityId);
      buffer.writeByte(this._type.ordinal());
      switch(this._type) {
      case STACK:
         buffer.writeItemStack(this._stack);
         break;
      case OPEN:
         buffer.writeBoolean(this._open);
         break;
      default:
         throw new RuntimeException("Invalid UpdateType");
      }

   }

   public static class Handler extends BackpacksMessageHandler<MessageBackpackUpdate> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public void handle(MessageBackpackUpdate message, MessageContext ctx) {
         Entity entity = getWorld(ctx).getEntityByID(message._entityId);
         if (entity != null) {
            BackpackCapability backpack = (BackpackCapability)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
            if (backpack != null) {
               switch(message._type) {
               case STACK:
                  backpack.stack = message._stack;
                  break;
               case OPEN:
                  backpack.playersUsing = message._open ? 1 : 0;
                  break;
               default:
                  throw new RuntimeException("Invalid UpdateType");
               }

            }
         }
      }
   }

   private static enum UpdateType {
      INVALID,
      STACK,
      OPEN;

      private UpdateType() {
      }

      public static MessageBackpackUpdate.UpdateType fromByte(byte b) {
         switch(b) {
         case 1:
            return STACK;
         case 2:
            return OPEN;
         default:
            return INVALID;
         }
      }
   }
}
