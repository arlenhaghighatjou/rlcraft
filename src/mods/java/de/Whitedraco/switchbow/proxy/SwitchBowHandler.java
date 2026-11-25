package de.Whitedraco.switchbow.proxy;

import de.Whitedraco.switchbow.helper.SwitchBowHelper;
import de.Whitedraco.switchbow.item.ItemSwitchBow;
import de.Whitedraco.switchbow.item.ItemSwitchCrossbow;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SwitchBowHandler implements IMessage {
   ItemStack newArrow;
   int hand;

   public SwitchBowHandler() {
   }

   public SwitchBowHandler(ItemStack selection, int hand) {
      this.newArrow = selection;
      this.hand = hand;
   }

   public void fromBytes(ByteBuf buf) {
      this.hand = buf.readInt();
      this.newArrow = ByteBufUtils.readItemStack(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.hand);
      ByteBufUtils.writeItemStack(buf, this.newArrow);
   }

   public void execute(MessageContext ctx) {
      ItemStack stack = ItemStack.EMPTY;
      if (this.hand == 0) {
         stack = ctx.getServerHandler().player.getHeldItemMainhand();
      } else {
         stack = ctx.getServerHandler().player.getHeldItemOffhand();
      }

      if (stack != ItemStack.EMPTY && (stack.getItem() instanceof ItemSwitchBow || stack.getItem() instanceof ItemSwitchCrossbow)) {
         SwitchBowHelper.setSelectionArrow(stack, this.newArrow);
      }

   }

   public static class Handler implements IMessageHandler<SwitchBowHandler, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SwitchBowHandler message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
