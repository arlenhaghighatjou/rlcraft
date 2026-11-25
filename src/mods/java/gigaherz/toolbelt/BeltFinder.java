package gigaherz.toolbelt;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class BeltFinder {
   public static NonNullList<BeltFinder> instances = NonNullList.create();

   public BeltFinder() {
   }

   @Nullable
   public abstract BeltFinder.BeltGetter findStack(EntityPlayer var1);

   @Nullable
   public static BeltFinder.BeltGetter findBelt(EntityPlayer player) {
      for(int i = instances.size() - 1; i >= 0; --i) {
         BeltFinder instance = (BeltFinder)instances.get(i);
         BeltFinder.BeltGetter getter = instance.findStack(player);
         if (getter != null) {
            return getter;
         }
      }

      return null;
   }

   public static void sendSync(EntityPlayer player) {
      BeltFinder.BeltGetter stack = findBelt(player);
      if (stack != null) {
         stack.syncToClients();
      }

   }

   public void setToBaubles(EntityPlayer player, int slot, ItemStack stack) {
   }

   public void setToBeltSlot(EntityLivingBase player, ItemStack stack) {
   }

   public interface BeltGetter {
      ItemStack getBelt();

      void syncToClients();
   }
}
