package com.fantasticsource.mctools.attributes;

import java.util.ArrayList;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.translation.I18n;

public class AttributeMods {
   public AttributeMods() {
   }

   public static ArrayList<AttributeModifier> parseMods(String modList) {
      String[] modifiers = modList.split("&");

      for(int i = 0; i < modifiers.length; ++i) {
         modifiers[i] = modifiers[i].trim();
      }

      return parseMods(modifiers);
   }

   public static ArrayList<AttributeModifier> parseMods(String[] modList) {
      ArrayList<AttributeModifier> result = new ArrayList();

      for(String string : modList) {
         AttributeModifier modifier = parseMod(string);
         if (modifier == null) {
            return null;
         }

         result.add(modifier);
      }

      return result;
   }

   public static AttributeModifier parseMod(String modString) {
      String[] tokens = modString.split("=");
      if (tokens.length != 2) {
         System.out.println(I18n.translateToLocalFormatted("fantasticlib.error.malformedAMod", new Object[]{modString}));
         return null;
      } else {
         String[] tokens2 = tokens[1].split("@");
         if (tokens2.length > 2) {
            System.out.println(I18n.translateToLocalFormatted("fantasticlib.error.malformedAMod", new Object[]{modString}));
            return null;
         } else {
            try {
               return new AttributeModifier(tokens[0].trim(), Double.parseDouble(tokens2[0].trim()), tokens2.length > 1 ? Integer.parseInt(tokens2[1].trim()) : 0);
            } catch (NumberFormatException var4) {
               System.out.println(I18n.translateToLocalFormatted("fantasticlib.error.malformedAMod", new Object[]{modString}));
               return null;
            }
         }
      }
   }
}
