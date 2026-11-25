package codersafterdark.compatskills.common.compats.astralsorcery.requirements;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ConstellationRequirement extends Requirement {
   private final IConstellation constellation;
   private final boolean seen;

   public ConstellationRequirement(IConstellation constellation, boolean seen) {
      this.constellation = constellation;
      this.seen = seen;
      String translationKey = "compatskills.requirements.format.constellation." + (seen ? "seen" : "known");
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.WHITE + (new TextComponentTranslation(translationKey, new Object[]{"%s", constellation.getSimpleName()})).getUnformattedComponentText();
   }

   public static ConstellationRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No constellation given.");
      } else {
         String[] inputInfo = input.split("\\|");
         if (inputInfo.length > 2) {
            throw new RequirementException("Too many parameters.");
         } else {
            String c = inputInfo[0];
            IConstellation constellation = ConstellationRegistry.getConstellationByName(c);
            if (constellation == null) {
               throw new RequirementException("Could not find constellation: '" + c + "'.");
            } else {
               boolean seen = true;
               if (inputInfo.length == 2) {
                  String s = inputInfo[1];
                  if (s.equalsIgnoreCase("known")) {
                     seen = false;
                  } else if (!s.equalsIgnoreCase("seen")) {
                     throw new RequirementException("Unknown final parameter: '" + s + "', expected either 'seen' or 'known'.");
                  }
               }

               return new ConstellationRequirement(constellation, seen);
            }
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      PlayerProgress progress = player instanceof EntityPlayerMP ? ResearchManager.getProgress((EntityPlayerMP)player) : ResearchManager.clientProgress;
      return (this.seen ? progress.getSeenConstellations() : progress.getKnownConstellations()).contains(this.constellation.getUnlocalizedName());
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ConstellationRequirement)) {
         return false;
      } else {
         ConstellationRequirement other = (ConstellationRequirement)o;
         return this.seen == other.seen && this.constellation.equals(other.constellation);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.seen, this.constellation});
   }

   public boolean isCacheable() {
      return false;
   }
}
