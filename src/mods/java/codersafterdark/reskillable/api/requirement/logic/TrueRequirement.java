package codersafterdark.reskillable.api.requirement.logic;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TrueRequirement extends Requirement {
   public TrueRequirement() {
      this.tooltip = TextFormatting.GREEN + (new TextComponentTranslation("reskillable.requirements.format.unobtainable", new Object[0])).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return true;
   }

   public String getToolTip(PlayerData data) {
      return this.tooltip;
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof TrueRequirement ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o instanceof TrueRequirement;
   }

   public int hashCode() {
      return 1;
   }
}
