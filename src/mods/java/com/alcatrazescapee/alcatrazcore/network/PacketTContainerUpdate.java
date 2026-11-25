package com.alcatrazescapee.alcatrazcore.network;

import com.alcatrazescapee.alcatrazcore.AlcatrazCore;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@ParametersAreNonnullByDefault
public abstract class PacketTContainerUpdate<T, D> implements IMessage {
   protected final Capability<T> capability;
   final TIntObjectMap<D> capabilityData = new TIntObjectHashMap();
   EnumFacing targetFace;
   int windowID;

   protected PacketTContainerUpdate(Capability<T> capability) {
      this.capability = capability;
   }

   protected PacketTContainerUpdate(Capability<T> capability, @Nullable EnumFacing targetFace, int windowID, int slotID, T instance) {
      this.capability = capability;
      this.targetFace = targetFace;
      this.windowID = windowID;
      D data = (D)this.readCapability(instance);
      if (data != null) {
         this.capabilityData.put(slotID, data);
      }

   }

   protected PacketTContainerUpdate(Capability<T> capability, @Nullable EnumFacing targetFace, int windowID, NonNullList<ItemStack> items) {
      this.capability = capability;
      this.targetFace = targetFace;
      this.windowID = windowID;

      for(int i = 0; i < items.size(); ++i) {
         T cap = (T)((ItemStack)items.get(i)).getCapability(capability, targetFace);
         if (cap != null) {
            D data = (D)this.readCapability(cap);
            if (data != null) {
               this.capabilityData.put(i, data);
            }
         }
      }

   }

   public final void fromBytes(ByteBuf buf) {
      this.windowID = buf.readInt();
      int facingIndex = buf.readByte();
      if (facingIndex >= 0) {
         this.targetFace = EnumFacing.getFront(facingIndex);
      } else {
         this.targetFace = null;
      }

      int numEntries = buf.readInt();

      for(int i = 0; i < numEntries; ++i) {
         int index = buf.readInt();
         D data = (D)this.deserializeCapability(buf);
         this.capabilityData.put(index, data);
      }

   }

   public final void toBytes(ByteBuf buf) {
      buf.writeInt(this.windowID);
      if (this.targetFace != null) {
         buf.writeByte(this.targetFace.getIndex());
      } else {
         buf.writeByte(-1);
      }

      buf.writeInt(this.capabilityData.size());
      this.capabilityData.forEachEntry((index, data) -> {
         buf.writeInt(index);
         this.serializeCapability(buf, data);
         return true;
      });
   }

   public final boolean hasData() {
      return !this.capabilityData.isEmpty();
   }

   protected abstract D readCapability(T var1);

   protected abstract void serializeCapability(ByteBuf var1, D var2);

   protected abstract D deserializeCapability(ByteBuf var1);

   @ParametersAreNonnullByDefault
   public abstract static class Handler<T, D, M extends PacketTContainerUpdate<T, D>> implements IMessageHandler<M, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(M message, MessageContext ctx) {
         if (!message.hasData()) {
            return null;
         } else {
            AlcatrazCore.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
               EntityPlayer player = AlcatrazCore.getProxy().getClientPlayer();
               Container container;
               if (message.windowID == 0) {
                  container = player.inventoryContainer;
               } else {
                  if (message.windowID != player.openContainer.windowId) {
                     return;
                  }

                  container = player.openContainer;
               }

               message.capabilityData.forEachEntry((index, data) -> {
                  ItemStack stack = container.getSlot(index).getStack();
                  T cap = (T)stack.getCapability(message.capability, message.targetFace);
                  if (cap != null) {
                     this.applyCapability(stack, cap, data);
                  }

                  return true;
               });
            });
            return null;
         }
      }

      public abstract void applyCapability(ItemStack var1, T var2, D var3);
   }
}
