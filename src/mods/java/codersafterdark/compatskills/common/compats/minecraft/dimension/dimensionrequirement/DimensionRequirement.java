package codersafterdark.compatskills.common.compats.minecraft.dimension.dimensionrequirement;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class DimensionRequirement extends Requirement {
   private final int dimension;

   public DimensionRequirement(int dimension) {
      this.dimension = dimension;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.LIGHT_PURPLE + (new TextComponentTranslation("compatskills.requirements.format.dimension", new Object[]{"%s", dimension})).getUnformattedComponentText();
   }

   public static DimensionRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No dimension id given.");
      } else {
         try {
            return new DimensionRequirement(Integer.parseInt(input));
         } catch (NumberFormatException var2) {
            throw new RequirementException("Invalid dimension id '" + input + "'.");
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return entityPlayerMP.dimension == this.dimension;
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof DimensionRequirement && this.dimension == ((DimensionRequirement)o).dimension;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.dimension});
   }
}
