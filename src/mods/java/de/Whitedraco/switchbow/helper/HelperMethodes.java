package de.Whitedraco.switchbow.helper;

import de.Whitedraco.switchbow.item.armor.ArcherArmor;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class HelperMethodes {
   public static final int potionduration = 60;

   public HelperMethodes() {
   }

   public static int getRandomNumberInRange(int min, int max) {
      if (min >= max) {
         throw new IllegalArgumentException("max must be greater than min");
      } else {
         Random r = new Random();
         return r.nextInt(max - min + 1) + min;
      }
   }

   public static boolean isTrueOrFalse(String string) {
      if (string.equals("true")) {
         return true;
      } else {
         return string.equals("false") ? false : false;
      }
   }

   public static boolean hasFullArcherArmor(EntityLivingBase entity) {
      for(ItemStack armor : entity.getArmorInventoryList()) {
         if (!(armor.getItem() instanceof ArcherArmor)) {
            return false;
         }
      }

      return true;
   }

   public static boolean checkProbability(int procent) {
      return getRandomNumberInRange(0, 100) <= procent;
   }
}
