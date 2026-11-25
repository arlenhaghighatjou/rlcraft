package gigaherz.toolbelt;

import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.belt.ItemToolBelt;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import gigaherz.toolbelt.network.BeltContentsChange;
import gigaherz.toolbelt.slot.ExtensionSlotBelt;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BeltFinderBeltSlot extends BeltFinder {
   public BeltFinderBeltSlot() {
   }

   @CapabilityInject(ExtensionSlotBelt.class)
   public static void initBaubles(Capability cap) {
      BeltFinder.instances.add(new BeltFinderBeltSlot());
   }

   @Nullable
   public BeltFinder.BeltGetter findStack(EntityPlayer player) {
      ExtensionSlotBelt beltSlot = ExtensionSlotBelt.get(player);
      if (beltSlot == null) {
         return null;
      } else {
         UnmodifiableIterator var3 = beltSlot.getSlots().iterator();

         while(var3.hasNext()) {
            IExtensionSlot slot = (IExtensionSlot)var3.next();
            ItemStack inSlot = slot.getContents();
            if (inSlot.getCount() > 0 && inSlot.getItem() instanceof ItemToolBelt) {
               return new BeltFinderBeltSlot.ExtensionSlotBeltGetter(slot);
            }
         }

         return null;
      }
   }

   public void setToBeltSlot(EntityLivingBase player, ItemStack stack) {
      ExtensionSlotBelt baubles = ExtensionSlotBelt.get(player);
      baubles.getBelt().setContents(stack);
   }

   private class ExtensionSlotBeltGetter implements BeltFinder.BeltGetter {
      private final IExtensionSlot slot;

      private ExtensionSlotBeltGetter(IExtensionSlot slot) {
         this.slot = slot;
      }

      public ItemStack getBelt() {
         return this.slot.getContents();
      }

      public void syncToClients() {
         EntityLivingBase thePlayer = this.slot.getContainer().getOwner();
         if (!thePlayer.world.isRemote) {
            BeltContentsChange message = new BeltContentsChange(thePlayer, BeltContentsChange.ContainingInventory.BELT_SLOT, 0, this.slot.getContents());
            ((WorldServer)thePlayer.world).getEntityTracker().getTrackingPlayers(thePlayer).forEach((p) -> {
               if (p instanceof EntityPlayerMP) {
                  ToolBelt.channel.sendTo(message, (EntityPlayerMP)p);
               }

            });
         }
      }
   }
}
