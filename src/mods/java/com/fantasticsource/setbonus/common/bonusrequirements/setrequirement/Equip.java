package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.ItemFilter;
import net.minecraft.util.text.translation.I18n;

public class Equip {
   public String parsedString;
   public String id;
   public ItemFilter filter;

   private Equip() {
   }

   public static Equip getInstance(String parsableEquip) {
      Equip result = new Equip();
      String[] tokens = parsableEquip.split(",");
      if (tokens.length != 2) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.equipArgCount", new Object[]{parsableEquip}));
         return null;
      } else {
         result.id = tokens[0].trim();
         if (result.id.equals("")) {
            System.err.println(I18n.translateToLocalFormatted("setbonus.error.noEquipID", new Object[]{parsableEquip}));
            return null;
         } else {
            result.filter = ItemFilter.getInstance(tokens[1]);
            if (result.filter == null) {
               return null;
            } else {
               result.parsedString = parsableEquip;
               return result;
            }
         }
      }
   }
}
