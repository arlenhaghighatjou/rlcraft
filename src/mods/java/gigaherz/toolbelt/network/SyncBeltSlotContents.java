package gigaherz.toolbelt.network;

import gigaherz.toolbelt.ToolBelt;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import gigaherz.toolbelt.slot.ExtensionSlotBelt;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBeltSlotContents implements IMessage {
   public final NonNullList<ItemStack> stacks = NonNullList.create();
   public int entityId;

   public SyncBeltSlotContents() {
   }

   public SyncBeltSlotContents(EntityPlayer player, ExtensionSlotBelt extension) {
      this.entityId = player.getEntityId();
      Stream var10000 = extension.getSlots().stream().map(IExtensionSlot::getContents);
      NonNullList var10001 = this.stacks;
      this.stacks.getClass();
      var10000.forEach(var10001::add);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = ByteBufUtils.readVarInt(buf, 5);
      int numStacks = ByteBufUtils.readVarInt(buf, 5);

      for(int i = 0; i < numStacks; ++i) {
         this.stacks.add(ByteBufUtils.readItemStack(buf));
      }

   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.entityId, 5);
      ByteBufUtils.writeVarInt(buf, this.stacks.size(), 5);

      for(ItemStack stack : this.stacks) {
         ByteBufUtils.writeItemStack(buf, stack);
      }

   }

   public static class Handler implements IMessageHandler<SyncBeltSlotContents, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SyncBeltSlotContents message, MessageContext ctx) {
         ToolBelt.proxy.handleBeltSlotContents(message);
         return null;
      }
   }
}
