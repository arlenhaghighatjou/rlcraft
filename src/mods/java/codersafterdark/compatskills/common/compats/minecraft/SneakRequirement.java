package codersafterdark.compatskills.common.compats.minecraft;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class SneakRequirement extends Requirement {
   public SneakRequirement() {
      this.tooltip = TextFormatting.GRAY + " - " + (new TextComponentTranslation("compatskills.requirements.format.sneak", new Object[]{"%s"})).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return entityPlayerMP.isSneaking();
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof SneakRequirement;
   }

   public int hashCode() {
      return 12345678;
   }

   public boolean isCacheable() {
      return false;
   }
}
