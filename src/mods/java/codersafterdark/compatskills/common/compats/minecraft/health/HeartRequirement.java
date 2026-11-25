package codersafterdark.compatskills.common.compats.minecraft.health;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class HeartRequirement extends Requirement {
   private final int hearts;

   public HeartRequirement(int hearts) {
      this.hearts = hearts;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.YELLOW + (new TextComponentTranslation("compatskills.requirements.format.hearts", new Object[]{"%s", hearts})).getUnformattedComponentText();
   }

   public static HeartRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No heart number given.");
      } else {
         try {
            return new HeartRequirement(Integer.parseInt(input));
         } catch (NumberFormatException var2) {
            throw new RequirementException("Invalid number of hearts '" + input + "'.");
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return entityPlayerMP.getHealth() >= (float)this.hearts;
   }

   public RequirementComparision matches(Requirement o) {
      if (o == this) {
         return RequirementComparision.EQUAL_TO;
      } else if (o instanceof HeartRequirement) {
         HeartRequirement other = (HeartRequirement)o;
         if (this.hearts == other.hearts) {
            return RequirementComparision.EQUAL_TO;
         } else {
            return this.hearts > other.hearts ? RequirementComparision.GREATER_THAN : RequirementComparision.LESS_THAN;
         }
      } else {
         return RequirementComparision.NOT_EQUAL;
      }
   }

   public boolean equals(Object o) {
      return o == this || o instanceof HeartRequirement && this.hearts == ((HeartRequirement)o).hearts;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.hearts});
   }
}
