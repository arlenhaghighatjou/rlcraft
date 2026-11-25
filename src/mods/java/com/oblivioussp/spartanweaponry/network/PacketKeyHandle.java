package com.oblivioussp.spartanweaponry.network;

import baubles.api.BaublesApi;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class PacketKeyHandle extends PacketBase<PacketKeyHandle> {
   public PacketKeyHandle() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public void handleClientSide(PacketKeyHandle message, EntityPlayer player) {
   }

   public void handleServerSide(PacketKeyHandle message, EntityPlayerMP player) {
      this.handle(message, player);
   }

   public void handle(PacketKeyHandle message, EntityPlayer player) {
      if (message != null) {
         ItemStack quiver = ItemStack.EMPTY;
         ItemQuiverBase quiverItem = null;
         ItemQuiverBase.SlotType slotType = ItemQuiverBase.SlotType.UNDEFINED;
         int slot = -1;

         label89:
         for(QuiverHelper.IQuiverInfo info : QuiverHelper.info) {
            if (info.isWeapon(player.getHeldItemMainhand())) {
               if (quiver.isEmpty() && ModSpartanWeaponry.isBaublesLoaded) {
                  ItemStack bauble = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
                  if (!bauble.isEmpty() && info.isQuiver(bauble)) {
                     quiver = bauble;
                     quiverItem = (ItemQuiverBase)bauble.getItem();
                     slotType = ItemQuiverBase.SlotType.BAUBLE;
                     break;
                  }
               }

               if (!quiver.isEmpty() && quiverItem != null) {
                  break;
               }

               int i = 0;

               while(true) {
                  if (i >= 9) {
                     break label89;
                  }

                  ItemStack stack = player.inventory.getStackInSlot(i);
                  if (!stack.isEmpty() && info.isQuiver(stack)) {
                     quiver = stack;
                     quiverItem = (ItemQuiverBase)stack.getItem();
                     slotType = ItemQuiverBase.SlotType.HOTBAR;
                     slot = i;
                     break label89;
                  }

                  ++i;
               }
            }
         }

         if (quiver.isEmpty() && ModSpartanWeaponry.isBaublesLoaded) {
            ItemStack bauble = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
            if (!bauble.isEmpty() && bauble.getItem() instanceof ItemQuiverBase) {
               quiver = bauble;
               quiverItem = (ItemQuiverBase)bauble.getItem();
               slotType = ItemQuiverBase.SlotType.BAUBLE;
            }
         }

         if (quiver.isEmpty() || quiverItem == null) {
            for(int i = 0; i < 9; ++i) {
               ItemStack stack = player.inventory.getStackInSlot(i);
               if (!stack.isEmpty() && stack.getItem() instanceof ItemQuiverBase) {
                  quiver = stack;
                  quiverItem = (ItemQuiverBase)stack.getItem();
                  slotType = ItemQuiverBase.SlotType.HOTBAR;
                  slot = i;
                  break;
               }
            }
         }

         if (!quiver.isEmpty() && quiverItem != null && slotType != ItemQuiverBase.SlotType.UNDEFINED) {
            player.openGui(ModSpartanWeaponry.instance, quiverItem.getGuiId(), player.world, slotType.ordinal(), slot, 0);
         } else {
            String status = StringHelper.translateString("quiver_not_found", "message");
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + status), true);
         }
      }
   }
}
