package codersafterdark.compatskills.common.compats.astralsorcery.requirements;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class AttunedRequirement extends Requirement {
   private final IMajorConstellation constellation;

   public AttunedRequirement(IMajorConstellation constellation) {
      this.constellation = constellation;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.YELLOW + (new TextComponentTranslation("compatskills.requirements.format.attuned", new Object[]{"%s", constellation.getSimpleName()})).getUnformattedComponentText();
   }

   public static AttunedRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No major constellation given.");
      } else {
         IMajorConstellation constellation = ConstellationRegistry.getMajorConstellationByName(input);
         if (constellation == null) {
            throw new RequirementException("Could not find major constellation: '" + input + "'.");
         } else {
            return new AttunedRequirement(constellation);
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      PlayerProgress progress = player instanceof EntityPlayerMP ? ResearchManager.getProgress((EntityPlayerMP)player) : ResearchManager.clientProgress;
      return this.constellation.equals(progress.getAttunedConstellation());
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof AttunedRequirement && this.constellation.equals(((AttunedRequirement)o).constellation);
   }

   public int hashCode() {
      return this.constellation.hashCode();
   }

   public boolean isCacheable() {
      return false;
   }
}
