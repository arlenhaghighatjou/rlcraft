package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.potion.Potion;

public class PotionLister {
   static final List<Integer> buff_instant_ids = new ArrayList();
   static final List<Integer> debuff_instant_ids = new ArrayList();
   static final List<Integer> buff_ids = new ArrayList();
   static final List<Integer> debuff_ids = new ArrayList();
   static final List<Integer> wildcard_debuff_ids = new ArrayList();
   static final List<Integer> wildcard_buff_ids = new ArrayList();
   static final List<String> potionBlacklist = new ArrayList();

   public PotionLister() {
   }

   public static void Cycle() {
      potionBlacklist.clear();

      for(String str : ModConfig.miscellaneous.potionBlacklist) {
         potionBlacklist.add(str.toLowerCase(Locale.ENGLISH));
      }

      for(Potion p : Potion.REGISTRY) {
         String potionName = p.getRegistryName().toString().toLowerCase(Locale.ENGLISH);
         boolean listMatch = potionBlacklist.contains(potionName);
         if (listMatch == ModConfig.miscellaneous.potionBlacklistAsWhitelist) {
            int flag = 0;
            if (p.isBadEffect()) {
               flag += 2;
            }

            if (p.isInstant()) {
               ++flag;
            }

            int id = Potion.getIdFromPotion(p);
            switch(flag) {
            case 0:
               buff_ids.add(Integer.valueOf(id));
               wildcard_buff_ids.add(Integer.valueOf(id));
               break;
            case 1:
               buff_instant_ids.add(Integer.valueOf(id));
               wildcard_buff_ids.add(Integer.valueOf(id));
               break;
            case 2:
               debuff_ids.add(Integer.valueOf(id));
               wildcard_debuff_ids.add(Integer.valueOf(id));
               break;
            case 3:
               debuff_instant_ids.add(Integer.valueOf(id));
               wildcard_debuff_ids.add(Integer.valueOf(id));
            }

            SMElogM.logger.debug("PotionLister accepting potion : " + potionName);
         } else {
            SMElogM.logger.debug("PotionLister skipping potion : " + potionName);
         }
      }

      SMElogM.logger.debug("PotionLister Instant Debuffs: " + debuff_instant_ids.size());
      SMElogM.logger.debug("PotionLister Debuffs: " + debuff_ids.size());
   }
}
