package codersafterdark.compatskills.common.compats.thaumcraft.requirements;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.ThaumcraftApi;

public class ResearchRequirement extends Requirement {
   private final String researchKey;

   public ResearchRequirement(String researchKey) {
      this.researchKey = researchKey;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.GOLD + (new TextComponentTranslation("compatskills.requirements.format.research", new Object[]{"%s", researchKey})).getUnformattedComponentText();
   }

   public static ResearchRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No research key given.");
      } else {
         return new ResearchRequirement(input);
      }
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayer) {
      return ThaumcraftApi.internalMethods.doesPlayerHaveRequisites(entityPlayer, this.researchKey);
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof ResearchRequirement && this.researchKey.equals(((ResearchRequirement)obj).researchKey);
   }

   public int hashCode() {
      return this.researchKey.hashCode();
   }
}
