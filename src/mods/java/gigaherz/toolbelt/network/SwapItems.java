package gigaherz.toolbelt.network;

import gigaherz.toolbelt.BeltFinder;
import gigaherz.toolbelt.Config;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class SwapItems implements IMessage {
   public int swapWith;

   public SwapItems() {
   }

   public SwapItems(int windowId) {
      this.swapWith = windowId;
   }

   public void fromBytes(ByteBuf buf) {
      this.swapWith = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.swapWith);
   }

   public static void swapItem(int swapWith, EntityPlayer player) {
      BeltFinder.BeltGetter getter = BeltFinder.findBelt(player);
      if (getter != null) {
         ItemStack stack = getter.getBelt();
         if (stack.getCount() > 0) {
            ItemStack inHand = player.getHeldItemMainhand();
            if (Config.isItemStackAllowed(inHand)) {
               IItemHandlerModifiable cap = (IItemHandlerModifiable)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
               if (swapWith < 0) {
                  player.setHeldItem(EnumHand.MAIN_HAND, ItemHandlerHelper.insertItem(cap, inHand, false));
               } else {
                  ItemStack inSlot = cap.getStackInSlot(swapWith);
                  player.setHeldItem(EnumHand.MAIN_HAND, inSlot);
                  cap.setStackInSlot(swapWith, inHand);
               }

               getter.syncToClients();
            }
         }
      }
   }

   public static class Handler implements IMessageHandler<SwapItems, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SwapItems message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         WorldServer world = (WorldServer)player.world;
         world.addScheduledTask(() -> {
            SwapItems.swapItem(message.swapWith, player);
         });
         return null;
      }
   }
}
