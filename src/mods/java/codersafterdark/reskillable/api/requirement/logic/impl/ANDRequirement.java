package codersafterdark.reskillable.api.requirement.logic.impl;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.logic.DoubleRequirement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

public class ANDRequirement extends DoubleRequirement {
   public ANDRequirement(Requirement left, Requirement right) {
      super(left, right);
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return this.leftAchieved(player) && this.rightAchieved(player);
   }

   protected String getFormat() {
      return (new TextComponentTranslation("reskillable.requirements.format.and", new Object[0])).getUnformattedComponentText();
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof ANDRequirement) {
         ANDRequirement other = (ANDRequirement)o;
         RequirementComparision left = this.getLeft().matches(other.getLeft());
         RequirementComparision right = this.getRight().matches(other.getRight());
         boolean same = left.equals(right);
         if (same && left.equals(RequirementComparision.EQUAL_TO)) {
            return RequirementComparision.EQUAL_TO;
         }

         RequirementComparision leftAlt = this.getLeft().matches(other.getRight());
         RequirementComparision rightAlt = this.getRight().matches(other.getLeft());
         boolean altSame = leftAlt.equals(rightAlt);
         if (altSame && leftAlt.equals(RequirementComparision.EQUAL_TO)) {
            return RequirementComparision.EQUAL_TO;
         }

         if (same && left.equals(RequirementComparision.GREATER_THAN) || altSame && leftAlt.equals(RequirementComparision.GREATER_THAN)) {
            return RequirementComparision.GREATER_THAN;
         }

         if (same && left.equals(RequirementComparision.LESS_THAN) || altSame && leftAlt.equals(RequirementComparision.LESS_THAN)) {
            return RequirementComparision.LESS_THAN;
         }
      }

      return RequirementComparision.NOT_EQUAL;
   }
}
