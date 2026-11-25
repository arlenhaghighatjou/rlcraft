package meldexun.reachfix.integration;

import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class SpartanWeaponry {
   public SpartanWeaponry() {
   }

   public static double getReachBonus(EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      Item item = stack.getItem();
      if (!(item instanceof IWeaponPropertyContainer)) {
         return 0.0D;
      } else {
         IWeaponPropertyContainer<?> propertyContainer = (IWeaponPropertyContainer)item;
         WeaponProperty reachProperty = propertyContainer.getFirstWeaponPropertyWithType("reach");
         return reachProperty == null ? 0.0D : (double)reachProperty.getMagnitude() - 5.0D;
      }
   }
}
