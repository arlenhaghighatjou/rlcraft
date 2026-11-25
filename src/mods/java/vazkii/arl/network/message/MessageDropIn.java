package vazkii.arl.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.DropInHandler;

public class MessageDropIn extends NetworkMessage<MessageDropIn> {
   public int slot;
   public ItemStack stack;

   public void toBytes(ByteBuf buf) {
      writeInt(this.slot, buf);
      writeItemStack(this.stack, buf);
   }

   public void fromBytes(ByteBuf buf) {
      this.slot = readInt(buf);
      this.stack = readItemStack(buf);
   }

   public MessageDropIn() {
      this.stack = ItemStack.EMPTY;
   }

   public MessageDropIn(int slot) {
      this.stack = ItemStack.EMPTY;
      this.slot = slot;
   }

   public MessageDropIn(int slot, ItemStack stack) {
      this(slot);
      this.stack = stack;
   }

   public IMessage handleMessage(MessageContext context) {
      EntityPlayerMP player = context.getServerHandler().player;
      player.mcServer.addScheduledTask(() -> {
         DropInHandler.executeDropIn(player, this.slot, this.stack);
      });
      return null;
   }
}
