package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.server.ServerBonus;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

public abstract class Bonus {
   public static int MODE_DISCOVERABLE = 0;
   public static int MODE_GLOBALLY_KNOWN = 1;
   public static int MODE_GLOBALLY_HIDDEN = 2;
   public String parsedString;
   public String id;
   public String name;
   public int discoveryMode;
   public ArrayList<ABonusRequirement> bonusRequirements = new ArrayList();
   public ArrayList<ABonusElement> bonusElements = new ArrayList();

   protected Bonus() {
   }

   public static Bonus getInstance(String parsableBonus, Side side) {
      Bonus bonus = (Bonus)(side == Side.SERVER ? new ServerBonus() : new ClientBonus());
      String[] tokens = parsableBonus.split(",");
      if (tokens.length < 3) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.notEnoughBonusArgs", new Object[]{parsableBonus}));
         return null;
      } else {
         bonus.id = tokens[0].trim();
         if (bonus.id.equals("")) {
            System.err.println(I18n.translateToLocalFormatted("setbonus.error.noBonusID", new Object[]{parsableBonus}));
            return null;
         } else {
            bonus.name = tokens[1].trim();

            try {
               bonus.discoveryMode = Integer.parseInt(tokens[2].trim());
            } catch (NumberFormatException var9) {
               System.err.println(I18n.translateToLocalFormatted("setbonus.error.bonusDiscoveryMode", new Object[]{parsableBonus}));
               return null;
            }

            if (bonus.discoveryMode >= 0 && bonus.discoveryMode <= 2) {
               for(String requirementString : (String[])Arrays.copyOfRange(tokens, 3, tokens.length)) {
                  ABonusRequirement requirement = ABonusRequirement.parse(requirementString, side);
                  if (requirement == null) {
                     System.err.println(I18n.translateToLocalFormatted("setbonus.error.unknownBonusReq", new Object[]{parsableBonus}));
                     return null;
                  }

                  bonus.bonusRequirements.add(requirement);
               }

               bonus.parsedString = parsableBonus;
               return bonus;
            } else {
               System.err.println(I18n.translateToLocalFormatted("setbonus.error.bonusDiscoveryMode", new Object[]{parsableBonus}));
               return null;
            }
         }
      }
   }
}
