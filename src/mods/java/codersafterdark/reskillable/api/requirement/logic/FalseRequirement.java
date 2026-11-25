package codersafterdark.reskillable.api.requirement.logic;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class FalseRequirement extends Requirement {
   public FalseRequirement() {
      this.tooltip = TextFormatting.RED + (new TextComponentTranslation("reskillable.requirements.format.unobtainable", new Object[0])).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return false;
   }

   public String getToolTip(PlayerData data) {
      return this.tooltip;
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof FalseRequirement ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o instanceof FalseRequirement;
   }

   public int hashCode() {
      return 0;
   }
}
