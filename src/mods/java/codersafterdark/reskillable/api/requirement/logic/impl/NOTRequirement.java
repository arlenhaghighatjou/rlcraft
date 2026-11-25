package codersafterdark.reskillable.api.requirement.logic.impl;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.logic.OuterRequirement;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class NOTRequirement extends Requirement implements OuterRequirement {
   private final Requirement requirement;

   public NOTRequirement(Requirement requirement) {
      this.requirement = requirement;
      String parentToolTip = this.requirement.internalToolTip();
      if (parentToolTip.startsWith(TextFormatting.GRAY + " - ")) {
         parentToolTip = parentToolTip.replaceFirst(TextFormatting.GRAY + " - ", "");
      }

      String start = TextFormatting.GRAY + " - ";
      if (parentToolTip.length() > 2 && parentToolTip.startsWith("§")) {
         start = start + parentToolTip.substring(0, 2);
         parentToolTip = parentToolTip.substring(2);
      }

      this.tooltip = start + '!' + parentToolTip;
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return !this.requirement.achievedByPlayer(player);
   }

   public String getToolTip(PlayerData data) {
      try {
         return String.format(this.internalToolTip(), data != null && data.requirementAchieved(this) ? TextFormatting.GREEN : TextFormatting.RED);
      } catch (IllegalArgumentException var15) {
         String parentToolTip = this.requirement.getToolTip(data);
         if (parentToolTip == null) {
            return "";
         } else {
            if (parentToolTip.startsWith(TextFormatting.GRAY + " - ")) {
               parentToolTip = parentToolTip.replaceFirst(TextFormatting.GRAY + " - ", "");
            }

            char colorCode = 167;
            String start = TextFormatting.GRAY + " - ";
            if (parentToolTip.length() > 2 && parentToolTip.startsWith(Character.toString(colorCode))) {
               start = start + parentToolTip.substring(0, 2);
               parentToolTip = parentToolTip.substring(2);
            }

            start = start + '!';
            StringBuilder tooltip = new StringBuilder(start);
            char[] chars = parentToolTip.toCharArray();
            char lastChar = '!';
            char red = 'c';
            char green = 'a';

            for(char c : chars) {
               if (lastChar == colorCode && (c == red || c == green)) {
                  if (c == red) {
                     tooltip.append(green);
                  } else {
                     tooltip.append(red);
                  }
               } else {
                  tooltip.append(c);
               }

               lastChar = c;
            }

            return tooltip.toString();
         }
      }
   }

   public Requirement getRequirement() {
      return this.requirement;
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof NOTRequirement) {
         RequirementComparision match = this.requirement.matches(((NOTRequirement)o).requirement);
         switch(match) {
         case GREATER_THAN:
            return RequirementComparision.LESS_THAN;
         case LESS_THAN:
            return RequirementComparision.GREATER_THAN;
         default:
            return match;
         }
      } else {
         return RequirementComparision.NOT_EQUAL;
      }
   }

   public boolean equals(Object o) {
      return o == this || o instanceof NOTRequirement && this.requirement.equals(((NOTRequirement)o).requirement);
   }

   public int hashCode() {
      return this.requirement.hashCode();
   }

   @Nonnull
   public List<Class<? extends Requirement>> getInternalTypes() {
      return this.requirement instanceof OuterRequirement ? ((OuterRequirement)this.requirement).getInternalTypes() : Collections.singletonList(this.requirement.getClass());
   }

   public boolean isCacheable() {
      return this.requirement.isCacheable();
   }
}
