package com.fantasticsource.mctools.potions;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Potions {
   public Potions() {
   }

   public static ArrayList<FantasticPotionEffect> parsePotions(String potionList) {
      return parsePotions(potionList, false);
   }

   public static ArrayList<FantasticPotionEffect> parsePotions(String[] potionList) {
      return parsePotions(potionList, false);
   }

   public static ArrayList<FantasticPotionEffect> parsePotions(String potionList, boolean ampFirst) {
      String[] potions = potionList.split("&");

      for(int i = 0; i < potions.length; ++i) {
         potions[i] = potions[i].trim();
      }

      return parsePotions(potions, ampFirst);
   }

   public static ArrayList<FantasticPotionEffect> parsePotions(String[] potionList, boolean ampFirst) {
      ArrayList<FantasticPotionEffect> result = new ArrayList();

      for(String string : potionList) {
         FantasticPotionEffect potion = parsePotion(string, ampFirst);
         if (potion != null) {
            result.add(potion);
         }
      }

      return result;
   }

   public static FantasticPotionEffect parsePotion(String potionString) {
      return parsePotion(potionString, false);
   }

   public static FantasticPotionEffect parsePotion(String potionString, boolean ampFirst) {
      potionString = potionString.trim();
      if (potionString.equals("")) {
         return null;
      } else {
         if (!potionString.contains(":")) {
            potionString = "minecraft:" + potionString;
         }

         String regString = "";
         Potion potion = null;

         for(Entry<ResourceLocation, Potion> entry : ForgeRegistries.POTIONS.getEntries()) {
            String testString = ((ResourceLocation)entry.getKey()).toString();
            boolean match = testString.equals(potionString);
            if (!match) {
               match = testString.length() < potionString.length() && potionString.substring(0, testString.length()).equals(testString);
            }

            if (match && testString.length() > regString.length()) {
               regString = testString;
               potion = (Potion)entry.getValue();
            }
         }

         if (regString.equals("")) {
            System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionNotFound", new Object[]{potionString}));
            return null;
         } else {
            potionString = potionString.replaceFirst(regString, "").replaceFirst("[.]", "").trim();
            String[] tokens = potionString.equals("") ? new String[0] : potionString.split(Pattern.quote("."));
            if (tokens.length > 3) {
               System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.tooManyPotionArgs", new Object[]{potionString}));
               return null;
            } else {
               int duration = Integer.MAX_VALUE;
               int amplifier = 0;
               int interval = 0;
               if (ampFirst) {
                  if (tokens.length > 0) {
                     String ampStr = tokens[0].trim();
                     if (ampStr.equals("*")) {
                        amplifier = Integer.MAX_VALUE;
                     } else {
                        try {
                           amplifier = Integer.parseInt(tokens[0].trim());
                           if (amplifier > 0) {
                              --amplifier;
                           }
                        } catch (NumberFormatException var14) {
                           System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionAmpNotNumber", new Object[]{potionString}));
                           return null;
                        }
                     }
                  }

                  if (tokens.length > 1) {
                     String durStr = tokens[1].trim();
                     if (durStr.equals("*")) {
                        duration = Integer.MAX_VALUE;
                     } else {
                        try {
                           duration = Integer.parseInt(tokens[1].trim());
                        } catch (NumberFormatException var13) {
                           System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionDurNotNumber", new Object[]{potionString}));
                           return null;
                        }
                     }
                  }
               } else {
                  if (tokens.length > 0) {
                     String durStr = tokens[0].trim();
                     if (durStr.equals("*")) {
                        duration = Integer.MAX_VALUE;
                     } else {
                        try {
                           duration = Integer.parseInt(tokens[0].trim());
                        } catch (NumberFormatException var12) {
                           System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionDurNotNumber", new Object[]{potionString}));
                           return null;
                        }
                     }
                  }

                  if (tokens.length > 1) {
                     String ampStr = tokens[1].trim();
                     if (ampStr.equals("*")) {
                        amplifier = Integer.MAX_VALUE;
                     } else {
                        try {
                           amplifier = Integer.parseInt(tokens[1].trim());
                           if (amplifier > 0) {
                              --amplifier;
                           }
                        } catch (NumberFormatException var11) {
                           System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionAmpNotNumber", new Object[]{potionString}));
                           return null;
                        }
                     }
                  }
               }

               if (tokens.length > 2) {
                  String intrvStr = tokens[2].trim();
                  if (intrvStr.equals("*")) {
                     interval = Integer.MAX_VALUE;
                  } else {
                     try {
                        interval = Integer.parseInt(tokens[2].trim());
                     } catch (NumberFormatException var10) {
                        System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.potionIntrvNotNumber", new Object[]{potionString}));
                        return null;
                     }
                  }
               }

               return (new FantasticPotionEffect(potion, duration, amplifier, false, true)).setInterval(interval);
            }
         }
      }
   }
}
