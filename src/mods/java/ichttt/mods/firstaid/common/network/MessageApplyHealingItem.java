package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageApplyHealingItem implements IMessage {
   private EnumPlayerPart part;
   private EnumHand hand;

   public MessageApplyHealingItem() {
   }

   public MessageApplyHealingItem(EnumPlayerPart part, EnumHand hand) {
      this.part = part;
      this.hand = hand;
   }

   public void fromBytes(ByteBuf buf) {
      this.part = EnumPlayerPart.fromID(buf.readByte());
      this.hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.part.id);
      buf.writeBoolean(this.hand == EnumHand.MAIN_HAND);
   }

   public static class Handler implements IMessageHandler<MessageApplyHealingItem, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageApplyHealingItem message, MessageContext ctx) {
         ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
            EntityPlayer player = ctx.getServerHandler().player;
            AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
            ItemStack stack = player.getHeldItem(message.hand);
            Item item = stack.getItem();
            AbstractPartHealer healer = FirstAidRegistryImpl.INSTANCE.getPartHealer(stack);
            if (healer == null) {
               FirstAid.LOGGER.warn("Player {} has invalid item in hand {} while it should be an healing item", player.getName(), item.getRegistryName());
               player.sendMessage(new TextComponentString("Unable to apply healing item!"));
            } else {
               stack.shrink(1);
               AbstractDamageablePart damageablePart = damageModel.getFromEnum(message.part);
               damageablePart.activeHealer = healer;
            }
         });
         return null;
      }
   }
}
