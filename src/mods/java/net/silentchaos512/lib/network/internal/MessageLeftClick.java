package net.silentchaos512.lib.network.internal;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.network.MessageSL;

public final class MessageLeftClick extends MessageSL<MessageLeftClick> {
   public int type;
   public boolean mainHand;

   public MessageLeftClick() {
      this.type = 0;
      this.mainHand = true;
   }

   public MessageLeftClick(MessageLeftClick.Type type, EnumHand hand) {
      this.type = type.ordinal();
      this.mainHand = hand == EnumHand.MAIN_HAND;
   }

   public void fromBytes(ByteBuf buf) {
      this.mainHand = buf.readBoolean();
      this.type = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.mainHand);
      buf.writeInt(this.type);
   }

   public IMessage handleMessage(MessageContext context) {
      if (context.side != Side.SERVER) {
         return null;
      } else {
         EnumHand hand = this.mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
         EntityPlayer player = context.getServerHandler().player;
         ItemStack heldItem = player.getHeldItem(hand);
         if (!heldItem.isEmpty() && heldItem.getItem() instanceof ILeftClickItem) {
            ILeftClickItem item = (ILeftClickItem)heldItem.getItem();
            if (this.type == MessageLeftClick.Type.EMPTY.ordinal()) {
               item.onItemLeftClickSL(player.world, player, hand);
            } else {
               item.onItemLeftClickBlockSL(player.world, player, hand);
            }
         }

         return null;
      }
   }

   public static enum Type {
      EMPTY,
      BLOCK;

      private Type() {
      }
   }
}
