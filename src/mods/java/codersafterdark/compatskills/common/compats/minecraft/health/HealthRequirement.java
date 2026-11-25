package codersafterdark.compatskills.common.compats.minecraft.health;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class HealthRequirement extends Requirement {
   private final double percent;

   public HealthRequirement(double percent) {
      this.percent = percent;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.DARK_GREEN + (new TextComponentTranslation("compatskills.requirements.format.health", new Object[]{"%s", percent})).getUnformattedComponentText();
   }

   public static HealthRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No health percentage given.");
      } else {
         try {
            return new HealthRequirement(Double.parseDouble(input));
         } catch (NumberFormatException var2) {
            throw new RequirementException("Invalid health percentage '" + input + "'.");
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return (double)(entityPlayerMP.getHealth() / entityPlayerMP.getMaxHealth()) >= this.percent;
   }

   public RequirementComparision matches(Requirement o) {
      if (o == this) {
         return RequirementComparision.EQUAL_TO;
      } else {
         if (o instanceof HealthRequirement) {
            HealthRequirement other = (HealthRequirement)o;
            if (this.percent == other.percent) {
               return RequirementComparision.EQUAL_TO;
            }

            if (this.percent > other.percent) {
               return RequirementComparision.GREATER_THAN;
            }

            if (this.percent < other.percent) {
               return RequirementComparision.LESS_THAN;
            }
         }

         return RequirementComparision.NOT_EQUAL;
      }
   }

   public boolean equals(Object o) {
      return o == this || o instanceof HealthRequirement && this.percent == ((HealthRequirement)o).percent;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.percent});
   }
}
