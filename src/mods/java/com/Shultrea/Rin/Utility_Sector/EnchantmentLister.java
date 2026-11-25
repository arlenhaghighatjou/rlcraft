package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Ench0_4_5.EnchantmentPandora;
import com.Shultrea.Rin.Interfaces.IAncientEnchantment;
import com.Shultrea.Rin.Interfaces.IEnchantmentRune;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentLister {
   static List<Enchantment> collection = new ArrayList();
   public static final List<Enchantment> NORMAL = new ArrayList();
   public static final List<Enchantment> CURSE = new ArrayList();
   public static final List<Enchantment> ANCIENT = new ArrayList();
   public static final List<Enchantment> RUNE = new ArrayList();

   public EnchantmentLister() {
   }

   public static void initEnchantmentList() {
      for(Enchantment s : Enchantment.REGISTRY) {
         collection.add(s);
      }

      for(Enchantment e : collection) {
         if (e instanceof IAncientEnchantment) {
            ANCIENT.add(e);
         } else if (e instanceof IEnchantmentRune) {
            RUNE.add(e);
         } else if (e.isCurse() && !(e instanceof EnchantmentPandora)) {
            CURSE.add(e);
         } else {
            NORMAL.add(e);
         }
      }

   }
}
