package codersafterdark.compatskills.common.compats.minecraft.looking;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class LookingAtEntityRequirement extends Requirement {
   private String entityID;

   public LookingAtEntityRequirement(String entityID, String name) {
      this.entityID = entityID;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.DARK_GREEN + (new TextComponentTranslation("compatskills.requirements.format.looking_at", new Object[]{"%s", name})).getUnformattedComponentText();
   }

   public static LookingAtEntityRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No Entity given.");
      } else {
         String translationName = EntityList.getTranslationName(new ResourceLocation(input));
         if (translationName == null) {
            throw new RequirementException("Unable to find entity: '" + input + "'.");
         } else {
            return new LookingAtEntityRequirement(input, (new TextComponentTranslation(translationName, new Object[0])).getUnformattedComponentText());
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      if (player == null) {
         return false;
      } else {
         Entity entity = Utils.lookingAt(player);
         return entity != null ? this.entityID.equals(Utils.getEntityID(entity)) : false;
      }
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof LookingAtEntityRequirement && this.entityID.equals(((LookingAtEntityRequirement)obj).entityID);
   }

   public int hashCode() {
      return this.entityID.hashCode();
   }

   public boolean isCacheable() {
      return false;
   }
}
