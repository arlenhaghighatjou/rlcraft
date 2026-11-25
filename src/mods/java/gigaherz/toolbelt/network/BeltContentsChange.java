package gigaherz.toolbelt.network;

import gigaherz.toolbelt.ToolBelt;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BeltContentsChange implements IMessage {
   public int player;
   public BeltContentsChange.ContainingInventory where;
   public int slot;
   public ItemStack stack;

   public BeltContentsChange() {
   }

   public BeltContentsChange(EntityLivingBase player, BeltContentsChange.ContainingInventory where, int slot, ItemStack stack) {
      this.player = player.getEntityId();
      this.where = where;
      this.slot = slot;
      this.stack = stack;
   }

   public void fromBytes(ByteBuf buf) {
      this.player = buf.readInt();
      this.where = BeltContentsChange.ContainingInventory.VALUES[buf.readByte()];
      this.slot = buf.readByte();
      this.stack = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.player);
      buf.writeByte(this.where.ordinal());
      buf.writeByte(this.slot);
      ByteBufUtils.writeItemStack(buf, this.stack);
   }

   public static enum ContainingInventory {
      MAIN,
      BAUBLES,
      BELT_SLOT;

      public static final BeltContentsChange.ContainingInventory[] VALUES = values();

      private ContainingInventory() {
      }
   }

   public static class Handler implements IMessageHandler<BeltContentsChange, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(BeltContentsChange message, MessageContext ctx) {
         ToolBelt.proxy.handleBeltContentsChange(message);
         return null;
      }
   }
}
