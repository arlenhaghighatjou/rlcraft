package gigaherz.toolbelt;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import gigaherz.toolbelt.belt.ItemToolBelt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class BeltFinderBaubles extends BeltFinder {
   public BeltFinderBaubles() {
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IBaublesItemHandler.class, BeltFinderBaubles::initBaubles);
   }

   public static void initBaubles(Capability cap) {
      BeltFinder.instances.add(new BeltFinderBaubles());
   }

   public void setToBaubles(EntityPlayer player, int slot, ItemStack stack) {
      IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
      baubles.setStackInSlot(slot, stack);
   }

   @Nullable
   public BeltFinder.BeltGetter findStack(EntityPlayer player) {
      IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

      for(int i = 0; i < baubles.getSlots(); ++i) {
         ItemStack inSlot = baubles.getStackInSlot(i);
         if (inSlot.getCount() > 0 && inSlot.getItem() instanceof ItemToolBelt) {
            return new BeltFinderBaubles.BaublesBeltGetter(player, i);
         }
      }

      return null;
   }

   private class BaublesBeltGetter implements BeltFinder.BeltGetter {
      private final EntityPlayer thePlayer;
      private final int slotNumber;

      private BaublesBeltGetter(EntityPlayer thePlayer, int slotNumber) {
         this.thePlayer = thePlayer;
         this.slotNumber = slotNumber;
      }

      public ItemStack getBelt() {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(this.thePlayer);
         return baubles.getStackInSlot(this.slotNumber);
      }

      public void syncToClients() {
      }
   }
}
